# Quick Start

#### 配置文件
##### * 默认加载classpath下的所有nymph开头的xml或者yml配置文件

###### nymph-demo.yml配置文件
```yml
webConfig: #注意层次 每个子的配置用一个空格或者tab缩进
  port: 9900 #内嵌tomcat可以在此设置端口号。对读取 web.xml的tomcat来说这项配置没用, 只能自己去server.xml配置
  contextPath: '' #对于内嵌tomcat来说他就表示项目名, 对于读取web.xml的tomcat来说这个配置没有任何意义
  urlPattern: /   #表示的是你希望让Nymph处理哪些url, / 和 /*表示所有  区别是 / 不会截取到.jsp后缀的url
  suffix: .jsp   #方法返回值路径的后缀
  prefix: /WEB-INF #同上, 前缀
  exclutions:   #想放行的静态资源  也可以直接放行整个文件夹 如 /css/*   /js/* 这种格式
   - '*.css'
   - '*.ico'
   - '*.jpg'
  filters:
   - com.nymph.filter.TestFilter@*.do # @后面的表示拦截的urlPattern 不设置的话默认是/* 拦截所有
  #异常处理器配置 配置的类需要实现ExceptionHandler接口
  exceptionHandler: com.nymph.exception.impl.ExceptionHandlerImpl
  
scanner: #使用了@Beans @HTTP 相关注解的必须得配置这个, 让容器能扫描到你的类
  - com.nymph.web
component: #将给出的类交给容器管理
  - com.nymph.bean.Woman
  - com.nymph.bean.Man
```
###### nymph-demo.xml配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<nymph xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.nymph.com/nymph" 
	xsi:schemaLocation="http://www.nymph.com/nymph http://www.nymph.com/nymph">
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
#### HttpBean代码实例
```java
@HTTP("/start") // 表示此类是一个Http请求的映射类
public class HelloWorld {

	// 自动注入Man的实例, 如果容器中存在
	private @Injection Man man;

	// 只允许Get请求访问此方法 @UrlHolder表示url上声明的变量@test
	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		// transfer是内置的类， 用来将数据存到servlet的各作用域(request, session)
		transfer.ofRequest("q", man);
		// 表示转发到/WEB-INF/index.jsp
		// 当返回值为"->/index"时表示重定向
		return "/index";
	}

	// 只允许Post请求访问此方法, @JSON表示返回的对象会被转换为json字符串响应到页面
	@POST("/no")
	@JSON
	public Man test2() {
		return man;
	}
	
	// 文件上传的处理
	@GET("/upload")
	public void test5(Multipart multipart) throws IOException {
		// file表示页面input标签的name
		FileInf fileInf = multipart.getFileInf("file");
		// 将文件写入指定的位置
		fileInf.writeTo("c:/data/demo.jpg");
	}
	

	// 内嵌tomcat的形式启动应用
	public static void main(String[] args) {
		MainStarter.start(HelloWorld.class);
	}
}
```

#### 通过HttpChannel获取HttpBean发出的序列化对象
```java
@HTTP("/demo")
public class HttpTest {

	// 关于序列化对象的传输 @Serialize注解表示返回的对象将被序列化到响应头中（返回的对象需要实现Serializable接口）
	@GET("/class")
	@Serialize
	public Man test3() {
		// 发送一个序列化对象
		Man man = new Man();
		man.setName("张学友");
		return man;
	}
	
	public static void main(String[] args) {
		MainStarter.start(HttpTest.class);
	}
}

public class Test {
	public static void main(String[] args) {
		HttpChannel channel = new HttpChannel("127.0.0.1", 9900);
		Man man = (Man)channel.getObject("/demo/class", Pattern.GET);
		System.out.println(man.getName());
		// 此处man的name为 "张学友"
		
		// 只使用一次的时候应该关掉socket连接
		channel.close();
	}
}
```

* author: 刘洋, 梁天东
