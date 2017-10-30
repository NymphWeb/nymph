# nymph

##配置文件
###默认加载classpath下的所有nymph开头的xml或者yml配置文件

```java
<?xml version="1.0" encoding="UTF-8"?>
<nymph xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.nymph.com/nymph" xsi:schemaLocation="http://www.nymph.com/nymph http://www.nymph.com/nymph">
	<!-- 表示此包下的类将会被容器扫描到, 并且带有@Bean相关注解的类会被注册到bean工厂 -->	
	<scanners>
		<scanner location="com.test"/>
	</scanners>
	
	<!-- web应用的相关配置 -->
	<webConfig>
		<port value="9900"/>
		<encoding value="UTF-8"/>
		<contextPath value=""/>
		<exclutions>
			<exclution value="/css/*"/>
			<exclution value="*.ico"/>
		</exclutions>
		<prefix value="/WEB-INF"/>
		<suffix value=".jsp"/>
		<urlPattern value="/"/>
	</webConfig>
</nymph>
```
##HttpBean代码实例
```java
@HTTP("/start") // 表示此类是一个Http请求的映射类
public class HelloWorld {

	// 自动注入Man的实例, 如果容器中存在
	private @Injection Man man;

	// 只允许get请求访问此方法 @UrlHolder表示url上声明的变量@test
	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		transfer.forRequest("q", man);
		// 表示转发到/WEB-INF/index.jsp
		// 当返回值为"->/index"时表示重定向
		return "/index";
	}

	// 只允许get请求访问此方法, @JSON表示返回的值为json格式
	@POST("/no/@test")
	@JSON
	public Man test2() {
		return man;
	}

	// 内嵌tomcat的形式启动应用
	public static void main(String[] args) throws Exception {
		MainStarter.start(HelloWorld.class);
	}
}
```
