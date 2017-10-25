package com.nymph.link.model;
/**
 * Request封装体
 * @author Nymph
 *
 */
public class Request {
	 /**
	  * 请求id
	  */
	private String requestId;
	/**
	 * 接口名称
	 */
	private String interfaceName;
	/**
	 * 当前服务版本
	 */
	private String serviceVersion;
	/**
	 * 当前方法名
	 */
	private String methodName;
	/**
	 * 形参类型
	 */
	private Class<?>[] parameterTypes;
	/**
	 * 形参值
	 */
	
	////setter and getter
	private Object[] parameters;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	

}
