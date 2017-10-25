package com.nymph.link.model;
/**
 * Response封装体
 * @author Nymph
 *
 */
public class Response {
	/**
	 * 请求requestId
	 */
	private String requestId;
	/**
	 * 处理失败,异常
	 */
    private Exception exception;
    /**
     * 处理成功,结果
     */
    private Object result;
    
    public boolean hasException() {
        return exception != null;
    }
    
    ///setter and getter
    
    public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
    
}
