package com.nymph.context.method;

import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public interface Javassists {

	/**
	 * 获取方法参数名
	 * @param method
	 * @return 
	 */
	public static String[] getParamName(Method method) throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass ctClass = pool.get(method.getDeclaringClass().getName());
		CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
		// 使用javassist的反射方法的参数名
		MethodInfo methodInfo = ctMethod.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) 
			codeAttribute.getAttribute(LocalVariableAttribute.tag);

		int length = ctMethod.getParameterTypes().length;
		String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = attr.variableName(i + 1);
		}
		return array;
	}

}
