package com.nymph.context.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.DateFmt;
import com.nymph.annotation.JSON;
import com.nymph.annotation.Request;
import com.nymph.annotation.UrlHolder;
import com.nymph.bean.web.HttpBeansContainer.HttpBean;
import com.nymph.context.ParamResolver;
import com.nymph.context.model.NyParam;
import com.nymph.context.model.NyView;
import com.nymph.context.wrapper.MethodWrapper;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.interceptor.NyInterceptors;
import com.nymph.queue.NyQueue;
import com.nymph.transfer.Multipart;
import com.nymph.transfer.Share;
import com.nymph.transfer.Transfer;
import com.nymph.utils.AnnoUtil;
import com.nymph.utils.BasicUtil;
import com.nymph.utils.DateUtil;

/**
 * 请求参数解析器的实现
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月7日下午8:21:40
 */
public class NyParamResolver extends AbstractResolver implements ParamResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(NyParamResolver.class);
	/** 
	 * 时间的格式字符串(yyyy-MM-dd)
	 */
	private String format;
	/** 
	 * 将要解析的参数 
	 */
	private NyParam nyParam;
	/** 
	 * 请求中携带的所有参数 
	 */
	private Map<String, String[]> paramMap;
	/** 
	 * Method的包装类 
	 */
	private MethodWrapper methodWrapper;
	/** 
	 * 视图队列 
	 */
	private NyQueue<NyView> queue;
	/** 
	 * 是否停止执行解析方法并提交请求
	 */
	private boolean isStop;
	/** 
	 * resultSingle()方法的递归次数
	 */
	private int cycleNumber;

	public NyParamResolver(NyParam nyParam, NyQueue<NyView> queue) {
		super(nyParam.getContext());
		this.queue = queue;
		this.nyParam = nyParam;
		this.paramMap = nyParam.getParams();
	}

	/**
	 * 查找url映射的类和方法
	 * @throws Throwable
	 */
	@Override
	public void resolver() throws Throwable {
		// 获取HttpBean
		HttpBean httpBean = nyParam.getHttpBean();
		Object mapClass = beansFactory.getBean(httpBean.getName());
		this.methodWrapper = new MethodWrapper(httpBean.getMethod());
		// 请求方式检查
		checkRequestType();
		// 已经注入完值的方法参数
		Object[] args = methodParamsInjection();
		// 代理类的执行返回结果
		Object result = intercept(mapClass, args);
		// 方法返回值检查
		methodReturnCheck();
		
		if (isStop) {
			wrapper.commit();
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("args: {}", Arrays.toString(args));
			LOGGER.debug("param name: {}", methodWrapper);
		}

		// 被代理的目标方法是否想返回json数据
		boolean isJson = methodWrapper.isAnnotationPresent(JSON.class);
		// 将视图解析器需要的参数放入视图解析器队列
		queue.put(new NyView(wrapper, result, format, isJson));
	}

	@Override
	public Object[] methodParamsInjection() throws Throwable {
		Object[] args = new Object[methodWrapper.getParamterLength()];

		for (int i = 0; i < args.length; i++) {
			// 当前方法参数的名称
			String paramName = methodWrapper.getParameterName(i);
			// 在请求参数中寻找和当前方法形参名相同的对应的参数值
			String[] param = paramMap.get(paramName);
			// 当前方法的参数对象
			Parameter parameter = methodWrapper.getParameter(i);
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
				// 获取到方法参数内的@UrlHolder注解对象
				UrlHolder urlVar = parameter.getAnnotation(UrlHolder.class);
				// 在map中获取@UrlHolder value()方法中字符串对应的值
				String string = nyParam.getPlaceHolder(paramName);
				if (string == null) {
					string = nyParam.getPlaceHolder(urlVar.value());
				}
				args[i] = BasicUtil.convert(paramType, string);
			} 
			else if (BasicUtil.isCollection(paramType)) { // 当参数为集合时
				// 当集合的泛型是primitive时 如 int long 这里也把String当做原生类型
				if (BasicUtil.isCommonCollection(parameter.getParameterizedType())) {
					args[i] = BasicUtil.convertList(paramType, param);
				}
				// 当集合的泛型为 除上面之外的其他java对象时
				else {
					existDateFormatAnnotation(parameter);
					args[i] = resultList(parameter.getParameterizedType());
					cycleControl();
				}
			} 
			else if (BasicUtil.isCommonType(paramType)) { // 当参数为基本类型时
				// param数组为空时结束此次循环
				if (BasicUtil.notNullAndLenNotZero(param)) 
					continue;
				args[i] = BasicUtil.convert(paramType, param[0]);
			} 
			else if (paramType == Date.class) { // 当参数为时间类型时
				// param数组为空时结束此次循环
				if (BasicUtil.notNullAndLenNotZero(param)) 
					continue;
				existDateFormatAnnotation(parameter);
				dateFormatCheck(format);
				args[i] = DateUtil.resolve(param[0], format);
			} 
			else { // 这里一般表示用户自定义的java对象
				existDateFormatAnnotation(parameter);
				args[i] = resultSingle(paramType, 0);
				cycleControl();
			}
		}
		return args;
	}
	
	@Override
	public Object intercept(Object target, Object[] param) throws Throwable {
		// 拦截器链前置执行的方法
		for (NyInterceptors preHandle : intercepts) {
			if (!preHandle.preHandle(wrapper)) {
				// 如果方法被拦截了就停止运行下面代码
				LOGGER.info("请求被拦截, url: {}", wrapper.httpRequest().getRequestURL());
				return isStop = true;
			}
		}
		try {
			Object invoke = methodWrapper.invoke(target, param);
			// 拦截器链的后置执行方法
			for (NyInterceptors beHandle : intercepts) {
				beHandle.behindHandle(wrapper);
			}
			return invoke;
		} catch (Throwable e) {
			// catch  HttpBean的异常
			Throwable cause = e.getCause();
			throw cause == null ? e : cause;
		}
	}
	
	/**
	 * 根据目标方法的类型，将请求中携带的参数封装成一个集合
	 * @param type	HttpBean中的方法参数集合的泛型类型
	 * @return		
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private List<?> resultList(Type type) throws Exception {
		
		List<Object> objs = new ArrayList<>();
		ParameterizedType paramType = (ParameterizedType)type;
		Class<?> generic = (Class<?>) paramType.getActualTypeArguments()[0];
		Field[] fields = generic.getDeclaredFields();
		
		int length = 0;
		for (Field field : fields) {
			String[] strings = paramMap.get(field.getName());
			if (strings == null) continue;
			// 判断有几个对象
			length = length < strings.length ? strings.length : length;
		}

		for (int i = 0; i < length; i++) {
			objs.add(resultSingle(generic, i));
		}
		return objs;
	}

	/**
	 * 根据类型将请求中的参数封装成一个对象并返回
	 * @param clazz	HttpBean中方法参数的类型
	 * @param index	用于resultList()方法标识索引位置
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private Object resultSingle(Class<?> clazz, int index) throws Exception {
		if (cycleNumber >= 10) {
			cycleNumber++;
			return null;
		}
		
		Object instance = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		
		for (Field field : fields) {
			if (BasicUtil.isCollection(clazz)) {
				instance = resultList(field.getGenericType());
				continue;
			} 
			
			String[] vals = paramMap.get(field.getName());
			
			if (vals == null || index >= vals.length)
				continue;
			
			String param = vals[index];
			
			if (BasicUtil.isCommonType(field)) {
				field.set(instance, BasicUtil.convert(field, param));
			}
			else if (field.getType() == Date.class) {
				dateFormatCheck(format);
				Date date = DateUtil.resolve(param, format);
				field.set(instance, date);
			}
			else {
				resultSingle(field.getType(), 0);
			}
		}
		return instance;
	}
	
	/**
	 * 循环控制, 防止栈溢出
	 */
	private void cycleControl() {
		if (cycleNumber >= 10) {
			throw new IllegalArgumentException(
				"当前方法参数绑定时递归次数过多, method: " + methodWrapper.getMethodName());
		}
	}
	
	/**
	 * 请求方式检查, 浏览器的请求方式和目标方法要求的请求方式不匹配时抛出异常
	 * @param method  被检查的方法
	 */
	private void checkRequestType() {
		String reqType = wrapper.httpRequest().getMethod();
		Annotation[] annos = methodWrapper.getAnnotations();
		Annotation methodType = AnnoUtil.get(annos, Request.class);
		String type = methodType.annotationType().getSimpleName();
		if (!type.equals(reqType)) {
			throw new PatternNoMatchException(reqType);
		}
	}

	/**
	 * 方法的返回值检查, 为void时会直接提交请求
	 * @param method
	 */
	private void methodReturnCheck() {
		isStop = methodWrapper.getReturnType() == void.class;
	}

	/**
	 * 时间格式检查
	 * @param format
	 */
	private void dateFormatCheck(String format) {
		if (null == format) {
			throw new IllegalArgumentException(
				"必须给出时间格式, 请在方法参数前使用@DateFmt注解设置时间格式");
		}
	}
	/**
	 * 文件上传检查
	 * @param multipart
	 */
	private Multipart multipartCheck(Multipart multipart) {
		if (multipart == null) {
			throw new IllegalArgumentException(
				"请确认form标签是否设置了multipart/form-data属性");
		}
		return multipart;
	}

	/**
	 * 存在@Datefmt注解时的操作
	 * @param parameter
	 */
	private void existDateFormatAnnotation(Parameter parameter) {
		DateFmt annotation = parameter.getAnnotation(DateFmt.class);
		if (annotation != null)
			format = annotation.value();
	}

}
