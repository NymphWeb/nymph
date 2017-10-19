package com.nymph.context.wrapper;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 主要用于获取方法的参数信息
 * @author LiuYang
 * @author LiangTianDong
 */
public class Methods {

    private static boolean isUseJavassist;

    private Parameter[] parameters;

    private Method method;

    static {
        try {
            Method method = Methods.class.getMethod("getParameter", int.class);
            isUseJavassist = method.getParameters()[0].getName().equals("arg0");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Methods(Method method) {
        this.method = method;
        this.parameters = method.getParameters();
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
        if (!isUseJavassist) {
            return parameters[index].getName();
        }
        try {
            return getParamNames(index);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取方法参数名(非静态方法)
     * @return
     */
    public String getParamNames(int index) throws NotFoundException {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
            // 使用javassist的反射方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            return attr == null ? "null" : attr.variableName(index + 1);
    }

    @Override
    public String toString() {
        return "{" +
                "parameters:" + Arrays.toString(parameters) +
                '}';
    }
}
