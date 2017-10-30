# nymph
=======
```java
@HTTP("/start")
public class HelloWorld {

	// 自动注入Man的实例, 如果容器中存在
	private @Injection Man man;

	// Get请求可以访问到此方法 @UrlHolder表示url上声明的变量@test
	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		transfer.forRequest("q", man);
		return "/index";
	}

	// Post请求可以访问到此方法, @JSON表示返回的值为json格式
	@POST("/no/@test")
	@JSON
	public Man test2() {
		return man;
	}

	// 内嵌tomcat启动
	public static void main(String[] args) throws Exception {
		MainStarter.start(HelloWorld.class);
	}
}
