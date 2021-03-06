package com.nymph.context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.nymph.context.core.ResovlerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.DateFmt;
import com.nymph.annotation.JSON;
import com.nymph.annotation.Request;
import com.nymph.annotation.Serialize;
import com.nymph.annotation.UrlHolder;
import com.nymph.bean.web.MapperInfoContainer.MapperInfo;
import com.nymph.context.method.WrapperMethod;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.interceptor.Interceptors;
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
public class ResovlerParamImpl extends AbstractResolver implements ResovlerParam {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResovlerParamImpl.class);
	/** 
	 * 将要解析的参数 
	 */
	private ContextParameter contextParameter;
	/** 
	 * 请求中携带的所有参数 
	 */
	private Map<String, String[]> paramMap;
	/** 
	 * Method的包装类 
	 */
	private WrapperMethod methodWrapper;
	/** 
	 * 视图队列 
	 */
	private NyQueue<ContextView> queue;
	/**
	 * httpBean的实例
	 */
	private Object httpBean;
	/**
	 * httpBean的方法返回值
	 */
	private Object result;
	/**
	 * 当前方法是否被拦截
	 */
	private boolean isIntercept;
	/** 
	 * resultSingle()方法的递归次数
	 */
	private int cycleNumber;
	/** 
	 * 时间的格式字符串
	 */
	private String format;

	public ResovlerParamImpl(ContextParameter contextParameter, NyQueue<ContextView> queue) {
		super(contextParameter.getContext());
		this.queue = queue;
		this.contextParameter = contextParameter;
	}
	/**
	 * 查找url映射的类和方法
	 * @throws Throwable
	 */
	@Override
	public void resolver() throws Throwable {
		MapperInfo info = contextParameter.getMapperInfo();
		this.httpBean = beansFactory.getBean(info.getName());
		this.methodWrapper = new WrapperMethod(info.getMethod());
		this.paramMap = contextParameter.getParams();
		// 请求方式检查
		checkRequestType();
		// 注入值后的形参列表
		Object[] agrs = injectParameters();
		// 目标方法执行返回结果
		this.result = invokeMethod(agrs);
		// 判断是否应该停止解析
		if (stopController()) return;
		// 被代理的目标方法是否想返回json数据
		boolean isJson = methodWrapper.isAnnotationPresent(JSON.class);
		// 将视图解析器需要的参数放入视图解析器队列
		queue.put(new ContextView(wrapper, result, format, isJson));
	}

	@Override
	public Object[] injectParameters() throws Throwable {
		Object[] args = new Object[methodWrapper.getParamterLength()];

		for (int i = 1; i < args.length; i++) {
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
				args[i] = multipartCheck(contextParameter.getMultipart());
			} 
			else if (parameter.isAnnotationPresent(UrlHolder.class)) {
				// 获取到方法参数内的@UrlHolder注解对象
				UrlHolder urlVar = parameter.getAnnotation(UrlHolder.class);
				// 在map中获取@UrlHolder value()方法中字符串对应的值
				String string = contextParameter.getPlaceHolder(paramName);
				if (string == null) {
					string = contextParameter.getPlaceHolder(urlVar.value());
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
	public Object invokeMethod(Object[] args) throws Throwable {
		// 拦截器链前置执行的方法
		for (Interceptors preHandle : intercepts) {
			if (!preHandle.preHandle(wrapper)) {
				// 如果方法被拦截了就停止运行下面代码
				LOGGER.info("请求被拦截, url: {}", 
					wrapper.httpRequest().getRequestURL());
				return isIntercept = true;
			}
		}
		try {
			Object invoke = methodWrapper.invoke(httpBean, args);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("parameter name: {}", methodWrapper);
				LOGGER.debug("parameter value: {}", Arrays.toString(args));
			}
			// 拦截器链的后置执行方法
			for (Interceptors beHandle : intercepts) {
				beHandle.behindHandle(wrapper);
			}
			return invoke;
		} catch (Throwable e) {
			// catch  httpBean的异常
			Throwable cause = e.getCause();
			throw cause == null ? e : cause;
		}
	}
	
	/**
	 * 根据目标方法的类型，将请求中携带的参数封装成一个集合
	 * @param type	httpBean中的方法参数集合的泛型类型
	 * @return		
	 * @throws Exception
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
	 * @param clazz	httpBean中方法参数的类型
	 * @param index	用于resultList()方法标识索引位置
	 * @return
	 * @throws Exception
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
			
			if (BasicUtil.isCommonType(field))
				field.set(instance, BasicUtil.convert(field, param));
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
	 * 方法的停止控制一般情况是拦截器拦截, 方法返回值为void时停止执行解析方法
	 * @return	true or false
	 * @throws IOException
	 */
	private boolean stopController() throws IOException {
		// 方法返回值为void或者被拦截时, 提交当前请求并结束后续的代码执行
		if(isIntercept || methodReturnCheck()) {
			wrapper.commit();
			return true;
		}

		// 当方法被@Serialize注解标识时, 将返回值序列化到请求头中
		if (methodWrapper.isAnnotationPresent(Serialize.class)) {
			wrapper.sendObject(result);
			wrapper.commit();
			return true;
		}
		return false;
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
	 * @return true or false
	 */
	private boolean methodReturnCheck() {
		return methodWrapper.getReturnType() == void.class;
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
