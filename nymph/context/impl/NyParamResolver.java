package com.nymph.context.impl;

import com.nymph.annotation.DateFmt;
import com.nymph.annotation.JSON;
import com.nymph.annotation.Request;
import com.nymph.annotation.UrlHolder;
import com.nymph.bean.impl.HttpBeansContainer.HttpBean;
import com.nymph.context.ParamResolver;
import com.nymph.context.model.NyParam;
import com.nymph.context.model.NyView;
import com.nymph.context.wrapper.Methods;
import com.nymph.exception.MethodReturnVoidException;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.transfer.Multipart;
import com.nymph.transfer.Share;
import com.nymph.transfer.Transfer;
import com.nymph.utils.BasicUtils;
import com.nymph.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 请求参数解析器的实现	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月7日下午8:21:40
 */
public class NyParamResolver extends AbstractResolver implements ParamResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(NyParamResolver.class);
	/**
	 *  对HttpBean的方法代理
	 */
	private static WebProxy methodProxy = new WebProxy(beansFactory.getBeansComponent().getInterceptors());
	/**
	 *  时间的格式字符串(yyyy-MM-dd)
	 */
	private String format;
	/**
	 *  被访问方法的目标方法
	 */
	private Methods methods;
	/**
	 *  将要解析的参数
	 */
	private NyParam nyParam;

	public NyParamResolver(NyParam nyParam) {
		super(nyParam.getContext());
		this.nyParam = nyParam;
	}

	/**
	 * 查找url映射的类和方法
	 * 
	 * @throws Throwable
	 */
	@Override
	public void resolver() throws Throwable {

		HttpBean httpBean = nyParam.getHttpBean();
		Object mapClass = beansFactory.getBean(httpBean.getName());
		Method method = httpBean.getMethod();

		checkRequestType(method); // 请求方式检查
		methodReturnCheck(method); // 方法返回值检查
		
		this.methods = new Methods(method);

		// 已经注入完值的方法参数
		Object[] args = methodParamsInjection();
		// 代理类的执行返回结果
		Object result = methodProxy.execute(mapClass, method, args, wrapper);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("method {}", methods);
			LOGGER.debug("resolver complete args: {}", Arrays.toString(args));
		}

		// 被代理的目标方法是否想返回json数据
		boolean isJson = method.isAnnotationPresent(JSON.class);
		// 将视图解析器需要的参数放入视图解析器队列
		NyDispatcher.VIEW_QUEUE.offer(new NyView(wrapper, result, format, isJson));
	}

	@Override
	public Object[] methodParamsInjection() throws Throwable {
		Map<String, String[]> paramMap = nyParam.getParams();
		Object[] args = new Object[methods.getParamterLength()];

		for (int i = 0; i < args.length; i++) {
			// 当前方法参数的名称
			String paramName = methods.getParameterName(i);
			// 在请求参数中寻找和当前方法形参名相同的对应的参数值
			String[] param = paramMap.get(paramName);
			// 当前方法的参数对象
			Parameter parameter = methods.getParameter(i);
			// 当前方法参数的类型
			Class<?> paramType = parameter.getType();
			
			if (paramType == Transfer.class) {
				// 操作request 和 session的类
				args[i] = new Transfer(wrapper);
			} 
			else if (paramType == Share.class) {
				// 文件下载类
				args[i] = new Share(wrapper);
			} 
			else if (paramType == Multipart.class) {
				// 文件上传的类
				args[i] = multipartCheck(nyParam.getMultipart());
			} 
			else if (parameter.isAnnotationPresent(UrlHolder.class)) {
				// 获取到方法参数内的@UrlVar注解对象
				UrlHolder urlVar = parameter.getAnnotation(UrlHolder.class);
				// 在map中获取@UrlHolder value()方法中字符串对应的值
				String string = nyParam.getPlaceHolder(paramName);
				if (string == null) {
					string = nyParam.getPlaceHolder(urlVar.value());
				}
				args[i] = BasicUtils.convert(paramType, string);

			} 
			else if (BasicUtils.isCollection(paramType)) {
				 // 当参数为集合时
				// 当集合的泛型是primitive时 如 int long 这里也把String当做原生类型
				if (BasicUtils.isCommonCollection(parameter.getParameterizedType())) {
					args[i] = BasicUtils.convertList(paramType, param);
				}
				// 当集合的泛型为 除上面之外的其他java对象时
				else {
					existDateFormatAnnotation(parameter);
					args[i] = resultList(paramType, paramMap,
							(ParameterizedType) parameter.getParameterizedType());
				}
			} 
			else if (BasicUtils.isCommonType(paramType)) {
				// 当参数为原生的类型时(基本类型)
				// param数组为空时结束此次循环
				if (BasicUtils.notNullAndLenNotZero(param)) 
					continue;
				args[i] = BasicUtils.convert(paramType, param[0]);

			} 
			else if (paramType == Date.class) {
				// 当参数为时间类型时
				// param数组为空时结束此次循环
				if (BasicUtils.notNullAndLenNotZero(param))
					continue;
				existDateFormatAnnotation(parameter);
				dateFormatCheck(format);
				args[i] = DateUtils.resolve(param[0], format);

			} 
			else { // 这里一般表示用户自定义的java对象
				existDateFormatAnnotation(parameter);
				args[i] = resultSingle(paramType, paramMap, 0);
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("bind param: {}", args[i]);
				LOGGER.debug("method paramsType: {}", paramType);
			}
		}
		return args;
	}

	/**
	 * 请求方式检查, 浏览器的请求方式和目标方法要求的请求方式不匹配时抛出异常
	 * @param context
	 * @param method
	 */
	private void checkRequestType(Method method) {
		String reqType = wrapper.httpRequest().getMethod();
		Annotation methodType = BasicUtils.getAnno(method.getAnnotations(), Request.class);
		String type = methodType.annotationType().getSimpleName();
		if (!type.equals(reqType)) {
			throw new PatternNoMatchException(reqType);
		}
	}

	/**
	 * 方法的返回值检查, 为void时会抛出这个异常 并且不对请求作出任何响应, 而是会直接提交请求
	 * @param method
	 */
	private void methodReturnCheck(Method method) {
		if (method.getReturnType() == void.class)
			throw new MethodReturnVoidException();
	}

	/**
	 * 时间格式检查
	 * 
	 * @param format
	 */
	private void dateFormatCheck(String format) {
		if (null == format) {
			throw new IllegalArgumentException("必须给出时间格式, 请在方法参数前使用@DateFmt注解设置时间格式");
		}
	}
	/**
	 * 文件上传检查
	 * @param multipart
	 */
	private Multipart multipartCheck(Multipart multipart) {
		if (multipart == null) {
			throw new IllegalArgumentException("请确认form标签是否设置了multipart/form-data属性");
		}
		return multipart;
	}

	/**
	 * 存在@Datefmt注解时的操作
	 * 
	 * @param parameter
	 */
	private void existDateFormatAnnotation(Parameter parameter) {
		DateFmt annotation = parameter.getAnnotation(DateFmt.class);
		if (annotation != null)
			format = annotation.value();
	}
	
	/**
	 * 根据类型返回对应的集合
	 * @param clazz
	 * @param params
	 * @param type
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public List<?> resultList(Class<?> clazz, Map<String, String[]> params, ParameterizedType type) 
			throws InstantiationException, IllegalAccessException {
		List<Object> objs = new ArrayList<>();
		Class<?> generic = (Class<?>) type.getActualTypeArguments()[0];
		Field[] fields = generic.getDeclaredFields();
		
		int length = 0;
		for (Field field : fields) {
			String[] strings = params.get(field.getName());
			if (strings == null) continue;
			
			length = length < strings.length ? strings.length : length;
		}

		for (int i = 0; i < length; i++) {
			objs.add(resultSingle(generic, params, i));
		}
		return objs;
	}

	/**
	 * 根据类型将map中的值封装成一个对象并返回
	 * @param clazz
	 * @param params
	 * @param index
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object resultSingle(Class<?> clazz, Map<String, String[]> params, int index) 
			throws InstantiationException, IllegalAccessException {
		
		Object instance = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		
		for (Field field : fields) {
			if (BasicUtils.isCollection(clazz)) {
				ParameterizedType paramType = (ParameterizedType) field.getGenericType();
				instance = resultList(clazz, params, paramType);
				continue;
			} 
			
			String[] vals = params.get(field.getName());
			
			if (vals == null || index >= vals.length)
				continue;
			
			String param = vals[index];
			
			if (BasicUtils.isCommonType(field)) {
				field.set(instance, BasicUtils.convert(field, param));
			}
			else if (field.getType() == Date.class) {
				dateFormatCheck(format);
				Date date = DateUtils.resolve(param, format);
				field.set(instance, date);
			}
			else {
				resultSingle(field.getType(), params, 0);
			}
		}
		return instance;
	}

}
