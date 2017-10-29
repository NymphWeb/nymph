package com.nymph.context.wrapper;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import javassist.NotFoundException;

/**
 * 主要用于获取方法的参数信息
 * @author LiuYang
 * @author LiangTianDong
 */
public class MethodWrapper {

	// 是否是用javassist或去方法参数名
    private static boolean isUseJavassist;

    // javassist获取到的方法参数名
    private String[] javassistParameterName;
    
    // jdk reflect api的Parameter
    private Parameter[] parameters;

    // jdk reflect api的Method
    private Method method;

    static {
        try {
            Method method = MethodWrapper.class.getMethod("getParameter", int.class);
            isUseJavassist = method.getParameters()[0].getName().equals("arg0");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public MethodWrapper(Method method) {
        this.method = method;
        this.parameters = method.getParameters();
        if (isUseJavassist) {
        	try {
        		javassistParameterName = JavassistUtil.getParamName(method);
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
        }
    }

    public int getParamterLength() {
        return parameters.length;
    }
    
    /**
     * 判断方法是否被指定注解标识
     * @param annotation
     * @return
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
    	return method.isAnnotationPresent(annotation);
    }
    
    /**
     * 获取方法上的所有注解
     * @return
     */
    public Annotation[] getAnnotations() {
    	return method.getAnnotations();
    }
    
    /**
     * 获取方法的返回值类型
     * @return
     */
    public Class<?> getReturnType() {
    	return method.getReturnType();
    }

    /**
     * 获取方法参数
     * @param index
     * @return
     */
    public Parameter getParameter(int index) {
        return parameters[index];
    }
    
    /**
     * 调用方法
     * @param obj
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object obj, Object... args) throws Throwable {
    	return method.invoke(obj, args);
    }

    /**
     * 获取方法参数名称
     * @param index
     * @return
     */
    public String getParameterName(int index) {
        if (isUseJavassist) {
            return javassistParameterName[index];
        }
        return parameters[index].getName();
    }
    
    /**
     * 获取方法名
     * @return
     */
    public String getMethodName() {
    	return method.getName();
    }
    
    @Override
    public String toString() {
        return "{" +
                "parameters:" + Arrays.toString(parameters) +
                '}';
    }
}
