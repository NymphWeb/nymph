package com.nymph.context.wrapper;

import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class JavassistUtil {

	/**
	 * 获取方法参数名
	 * @param method
	 * @return
	 * @throws NotFoundException
	 */
	public static String[] getParamName(Method method) throws NotFoundException {
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
