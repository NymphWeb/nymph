package com.nymph.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.DateFmt;
import com.nymph.annotation.JSON;
import com.nymph.annotation.UrlVar;
import com.nymph.annotation.Request;
import com.nymph.bean.impl.HttpBean;
import com.nymph.core.ParamResolver;
import com.nymph.core.model.NyParam;
import com.nymph.core.model.NyView;
import com.nymph.exception.MethodReturnVoidException;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.transfer.Multipart;
import com.nymph.transfer.Share;
import com.nymph.transfer.Transfer;
import com.nymph.utils.BasicUtils;
import com.nymph.utils.DateUtils;

/**
 * 请求参数解析器的实现	
 * @author NYMPH
 * @date 2017年10月7日下午8:21:40
 */
public class NyParamResolver extends AbstractResolver implements ParamResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(NyParamResolver.class);
	/**
	 *  时间的格式字符串(yyyy-MM-dd)
	 */
	private String format;
	/**
	 *  web层被访问方法的参数
	 */
	private Parameter[] parameters;
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

		String mappingName = httpBean.getName();
		Object mapClass = beansFactory.getBean(mappingName);

		Method method = httpBean.getMethod();

		checkRequestType(method); // 请求方式检查
		methodReturnCheck(method); // 方法返回值检查
		
		// 方法的形参对象
		this.parameters = method.getParameters();
		
		// 已经注入完值的方法参数
		Object[] args = methodParamsInjection();
		// 代理类的执行返回结果
		Object result = WebProxy.execute(mapClass, method, args, wrapper, intercepts);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("method parameters: {}", Arrays.toString(parameters));
			LOGGER.debug("resolver complete args: {}", Arrays.toString(args));
		}

		// 被代理的目标方法是否想返回json数据
		boolean isJson = method.isAnnotationPresent(JSON.class);
		// 将视图解析器需要的参数放入视图解析器队列
		NyDispatcher.VIEW_QUEUE.put(new NyView(wrapper, result, format, isJson));
	}

	@Override
	public Object[] methodParamsInjection() throws Throwable {
		Map<String, String[]> paramMap = nyParam.getParams();
		Object[] args = new Object[parameters.length];

		for (int i = 0; i < args.length; i++) {
			// 在请求参数中寻找和当前方法形参名相同的对应 的参数值
			String[] param = paramMap.get(parameters[i].getName());
			// 当前方法参数的类型
			Class<?> currentParamType = parameters[i].getType();
			
			if (currentParamType == Transfer.class) {
				// 操作request 和 session的类
				args[i] = new Transfer(wrapper);
			} 
			else if (currentParamType == Share.class) {
				// 文件下载类
				args[i] = new Share(wrapper);
			} 
			else if (currentParamType == Multipart.class) {
				// 文件上传的类
				args[i] = multipartCheck(nyParam.getMultipart());
			} 
			else if (parameters[i].isAnnotationPresent(UrlVar.class)) {
				// 获取到方法参数内的@UrlVar注解对象
				UrlVar urlVar = parameters[i].getAnnotation(UrlVar.class);
				// 在map中获取@UrlVar value()方法中字符串对应的值
				String string = nyParam.getPlaceHolder(parameters[i].getName());
				if (string == null) {
					string = nyParam.getPlaceHolder(urlVar.value());
				}
				args[i] = BasicUtils.convert(currentParamType, string);

			} 
			else if (BasicUtils.isCollection(currentParamType)) {
				 // 当参数为集合时
				// 当集合的泛型是primitive时 如 int long 这里也把String当做原生类型
				if (BasicUtils.isCommonCollection(parameters[i].getParameterizedType())) {
					args[i] = BasicUtils.convertList(currentParamType, param);
				}
				// 当集合的泛型为 除上面之外的其他java对象时
				else {
					existDateFormatAnnotation(parameters[i]);
					args[i] = BasicUtils.resultType(currentParamType, paramMap,
						(ParameterizedType) parameters[i].getParameterizedType(), format);
				}
			} 
			else if (BasicUtils.isCommonType(currentParamType)) {
				// 当参数为原生的类型时(基本类型)
				// param数组为空时结束此次循环
				if (BasicUtils.notNullAndLenNotZero(param)) 
					continue;
				args[i] = BasicUtils.convert(currentParamType, param[0]);

			} 
			else if (currentParamType == Date.class) {
				// 当参数为时间类型时
				// param数组为空时结束此次循环
				if (BasicUtils.notNullAndLenNotZero(param))
					continue;
				existDateFormatAnnotation(parameters[i]);
				dateFormatCheck(format);
				args[i] = DateUtils.resolve(param[0], format);

			} 
			else { // 这里一般表示用户自定义的java对象
				existDateFormatAnnotation(parameters[i]);
				args[i] = BasicUtils.resultType(currentParamType, paramMap, 0, format);
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("bind param: {}", args[i]);
				LOGGER.debug("method paramsType: {}", currentParamType);
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
			throw new IllegalArgumentException("必须给出时间格式, 请在方法参数前使用@DateFormat(yyyy-MM-dd) ");
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

}
