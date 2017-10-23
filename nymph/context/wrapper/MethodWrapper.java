package com.nymph.context.wrapper;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

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
        	initializedParameter();
        }
    }

    public int getParamterLength() {
        return parameters.length;
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
    
    private void initializedParameter() {
    	try {
    		ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
	        // 使用javassist的反射方法的参数名
	        MethodInfo methodInfo = ctMethod.getMethodInfo();
	        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
	        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
	                .getAttribute(LocalVariableAttribute.tag);
	        
	        String[] array = new String[parameters.length];
	        for (int i = 0; i < parameters.length; i++) {
	        	array[i] = attr.variableName(i + 1);
			}
	        javassistParameterName = array;
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    }

    @Override
    public String toString() {
        return "{" +
                "parameters:" + Arrays.toString(parameters) +
                '}';
    }
}
