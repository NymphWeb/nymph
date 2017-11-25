# Quick Start

#### �����ļ�
##### * Ĭ�ϼ���classpath�µ�����nymph��ͷ��xml����yml�����ļ�

###### nymph-demo.yml�����ļ�
```yml
webConfig: #ע���� ÿ���ӵ�������һ���ո����tab����
  port: 9900 #��Ƕtomcat�����ڴ����ö˿ںš��Զ�ȡ web.xml��tomcat��˵��������û��, ֻ���Լ�ȥserver.xml����
  contextPath: '' #������Ƕtomcat��˵���ͱ�ʾ��Ŀ��, ���ڶ�ȡweb.xml��tomcat��˵�������û���κ�����
  urlPattern: /   #��ʾ������ϣ����Nymph������Щurl, / �� /*��ʾ����  ������ / �����ȡ��.jsp��׺��url
  suffix: .jsp   #��������ֵ·���ĺ�׺
  prefix: /WEB-INF #ͬ��, ǰ׺
  exclutions:   #����еľ�̬��Դ  Ҳ����ֱ�ӷ��������ļ��� �� /css/*   /js/* ���ָ�ʽ
   - '*.css'
   - '*.ico'
   - '*.jpg'
  filters:
   - com.nymph.filter.TestFilter@*.do # @����ı�ʾ���ص�urlPattern �����õĻ�Ĭ����/* ��������
  #�쳣���������� ���õ�����Ҫʵ��ExceptionHandler�ӿ�
  exceptionHandler: com.nymph.exception.impl.ExceptionHandlerImpl
  
scanner: #ʹ����@Beans @HTTP ���ע��ı�����������, ��������ɨ�赽�����
  - com.nymph.web
component: #���������ཻ����������
  - com.nymph.bean.Woman
  - com.nymph.bean.Man
```
###### nymph-demo.xml�����ļ�
```xml
<?xml version="1.0" encoding="UTF-8"?>
<nymph xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.nymph.com/nymph" 
	xsi:schemaLocation="http://www.nymph.com/nymph http://www.nymph.com/nymph">
	<!-- ��ʾ�˰��µ��ཫ�ᱻ����ɨ�赽, ���Ҵ���@Bean���ע�����ᱻע�ᵽbean���� -->	
	<scanners>
		<scanner location="com.test"/>
	</scanners>
	
	<!-- webӦ�õ�������� -->
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
#### HttpBean����ʵ��
```java
@HTTP("/start") // ��ʾ������һ��Http�����ӳ����
public class HelloWorld {

	// �Զ�ע��Man��ʵ��, ��������д���
	private @Injection Man man;

	// ֻ����Get������ʴ˷��� @UrlHolder��ʾurl�������ı���@test
	@GET("/get/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		// transfer�����õ��࣬ ���������ݴ浽servlet�ĸ�������(request, session)
		transfer.ofRequest("q", man);
		// ��ʾת����/WEB-INF/index.jsp
		// ������ֵΪ"->/index"ʱ��ʾ�ض���
		return "/index";
	}

	// ֻ����Post������ʴ˷���, @JSON��ʾ���صĶ���ᱻת��Ϊjson�ַ�����Ӧ��ҳ��
	@POST("/post/@test")
	@JSON
	public Man test2(@UrlHolder String test) {
		System.out.println(test);
		return man;
	}
	
	// �ļ��ϴ�
	@GET("/upload")
	public void test3(Multipart multipart) throws IOException {
		// file��ʾҳ��input��ǩ��name
		FileInf fileInf = multipart.getFileInf("file");
		// ���ļ�д��ָ����λ��
		fileInf.writeTo("c:/data/demo.jpg");
	}
	
	// �ļ�����
	@GET("/downloads")
	public void test4(Share share) {
		share.shareFile("C:/hello.jpg");
	}

	// ��Ƕtomcat����ʽ����Ӧ��
	public static void main(String[] args) {
		MainStarter.start(HelloWorld.class);
	}
}
```

#### ͨ��HttpChannel��ȡHttpBean���������л�����
```java
// �����
@HTTP("/demo")
public class HttpTest {

	// �������л�����Ĵ��� @Serializeע���ʾ���صĶ��󽫱����л�����Ӧͷ�У����صĶ�����Ҫʵ��Serializable�ӿڣ�
	@GET("/class")
	@Serialize
	public Man test() {
		// ����һ�����л�����
		Man man = new Man();
		man.setName("��ѧ��");
		return man;
	}
	
	public static void main(String[] args) {
		MainStarter.start(HttpTest.class);
	}
}

// �ͻ���
public class Test {
	public static void main(String[] args) {
		HttpChannel channel = new HttpChannel("127.0.0.1", 9900);
		Man man = (Man)channel.getObject("/demo/class", Pattern.GET);
		System.out.println(man.getName());
		// �˴�man��nameΪ "��ѧ��"
	}
}
```
* author: ����(QQ: 564778568)
* author: ���춫(QQ: 1275976240)
