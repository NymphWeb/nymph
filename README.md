# Quick Start

<<<<<<< HEAD
#### ÅäÖÃÎÄ¼þ
##### * Ä¬ÈÏ¼ÓÔØclasspathÏÂµÄËùÓÐnymph¿ªÍ·µÄxml»òÕßymlÅäÖÃÎÄ¼þ

###### nymph-demo.ymlÅäÖÃÎÄ¼þ
```yml
webConfig: #×¢Òâ²ã´Î Ã¿¸ö×ÓµÄÅäÖÃÓÃÒ»¸ö¿Õ¸ñ»òÕßtabËõ½ø
  port: 9900 #ÄÚÇ¶tomcat¿ÉÒÔÔÚ´ËÉèÖÃ¶Ë¿ÚºÅ¡£¶Ô¶ÁÈ¡ web.xmlµÄtomcatÀ´ËµÕâÏîÅäÖÃÃ»ÓÃ, Ö»ÄÜ×Ô¼ºÈ¥server.xmlÅäÖÃ
  contextPath: '' #¶ÔÓÚÄÚÇ¶tomcatÀ´ËµËû¾Í±íÊ¾ÏîÄ¿Ãû, ¶ÔÓÚ¶ÁÈ¡web.xmlµÄtomcatÀ´ËµÕâ¸öÅäÖÃÃ»ÓÐÈÎºÎÒâÒå
  urlPattern: /   #±íÊ¾µÄÊÇÄãÏ£ÍûÈÃNymph´¦ÀíÄÄÐ©url, / ºÍ /*±íÊ¾ËùÓÐ  Çø±ðÊÇ / ²»»á½ØÈ¡µ½.jspºó×ºµÄurl
  suffix: .jsp   #·½·¨·µ»ØÖµÂ·¾¶µÄºó×º
  prefix: /WEB-INF #Í¬ÉÏ, Ç°×º
  exclutions:   #Ïë·ÅÐÐµÄ¾²Ì¬×ÊÔ´  Ò²¿ÉÒÔÖ±½Ó·ÅÐÐÕû¸öÎÄ¼þ¼Ð Èç /css/*   /js/* ÕâÖÖ¸ñÊ½
=======
#### é…ç½®æ–‡ä»¶
##### * é»˜è®¤åŠ è½½classpathä¸‹çš„æ‰€æœ‰nymphå¼€å¤´çš„xmlæˆ–è€…ymlé…ç½®æ–‡ä»¶

###### nymph-demo.ymlé…ç½®æ–‡ä»¶
```yml
webConfig: #æ³¨æ„å±‚æ¬¡ æ¯ä¸ªå­çš„é…ç½®ç”¨ä¸€ä¸ªç©ºæ ¼æˆ–è€…tabç¼©è¿›
  port: 9900 #å†…åµŒtomcatå¯ä»¥åœ¨æ­¤è®¾ç½®ç«¯å£å·ã€‚å¯¹è¯»å– web.xmlçš„tomcatæ¥è¯´è¿™é¡¹é…ç½®æ²¡ç”¨, åªèƒ½è‡ªå·±åŽ»server.xmlé…ç½®
  contextPath: '' #å¯¹äºŽå†…åµŒtomcatæ¥è¯´ä»–å°±è¡¨ç¤ºé¡¹ç›®å, å¯¹äºŽè¯»å–web.xmlçš„tomcatæ¥è¯´è¿™ä¸ªé…ç½®æ²¡æœ‰ä»»ä½•æ„ä¹‰
  urlPattern: /   #è¡¨ç¤ºçš„æ˜¯ä½ å¸Œæœ›è®©Nymphå¤„ç†å“ªäº›url, / å’Œ /*è¡¨ç¤ºæ‰€æœ‰  åŒºåˆ«æ˜¯ / ä¸ä¼šæˆªå–åˆ°.jspåŽç¼€çš„url
  suffix: .jsp   #æ–¹æ³•è¿”å›žå€¼è·¯å¾„çš„åŽç¼€
  prefix: /WEB-INF #åŒä¸Š, å‰ç¼€
  exclutions:   #æƒ³æ”¾è¡Œçš„é™æ€èµ„æº  ä¹Ÿå¯ä»¥ç›´æŽ¥æ”¾è¡Œæ•´ä¸ªæ–‡ä»¶å¤¹ å¦‚ /css/*   /js/* è¿™ç§æ ¼å¼
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
   - '*.css'
   - '*.ico'
   - '*.jpg'
  filters:
<<<<<<< HEAD
   - com.nymph.filter.TestFilter@*.do # @ºóÃæµÄ±íÊ¾À¹½ØµÄurlPattern ²»ÉèÖÃµÄ»°Ä¬ÈÏÊÇ/* À¹½ØËùÓÐ
  #Òì³£´¦ÀíÆ÷ÅäÖÃ ÅäÖÃµÄÀàÐèÒªÊµÏÖExceptionHandler½Ó¿Ú
  exceptionHandler: com.nymph.exception.impl.ExceptionHandlerImpl
  
scanner: #Ê¹ÓÃÁË@Beans @HTTP Ïà¹Ø×¢½âµÄ±ØÐëµÃÅäÖÃÕâ¸ö, ÈÃÈÝÆ÷ÄÜÉ¨Ãèµ½ÄãµÄÀà
  - com.nymph.web
component: #½«¸ø³öµÄÀà½»¸øÈÝÆ÷¹ÜÀí
  - com.nymph.bean.Woman
  - com.nymph.bean.Man
```
###### nymph-demo.xmlÅäÖÃÎÄ¼þ
=======
   - com.nymph.filter.TestFilter@*.do # @åŽé¢çš„è¡¨ç¤ºæ‹¦æˆªçš„urlPattern ä¸è®¾ç½®çš„è¯é»˜è®¤æ˜¯/* æ‹¦æˆªæ‰€æœ‰
  #å¼‚å¸¸å¤„ç†å™¨é…ç½® é…ç½®çš„ç±»éœ€è¦å®žçŽ°ExceptionHandleræŽ¥å£
  exceptionHandler: com.nymph.exception.impl.ExceptionHandlerImpl
  
scanner: #ä½¿ç”¨äº†@Beans @HTTP ç›¸å…³æ³¨è§£çš„å¿…é¡»å¾—é…ç½®è¿™ä¸ª, è®©å®¹å™¨èƒ½æ‰«æåˆ°ä½ çš„ç±»
  - com.nymph.web
component: #å°†ç»™å‡ºçš„ç±»äº¤ç»™å®¹å™¨ç®¡ç†
  - com.nymph.bean.Woman
  - com.nymph.bean.Man
```
###### nymph-demo.xmlé…ç½®æ–‡ä»¶
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
```xml
<?xml version="1.0" encoding="UTF-8"?>
<nymph xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.nymph.com/nymph" 
	xsi:schemaLocation="http://www.nymph.com/nymph http://www.nymph.com/nymph">
<<<<<<< HEAD
	<!-- ±íÊ¾´Ë°üÏÂµÄÀà½«»á±»ÈÝÆ÷É¨Ãèµ½, ²¢ÇÒ´øÓÐ@BeanÏà¹Ø×¢½âµÄÀà»á±»×¢²áµ½bean¹¤³§ -->	
=======
	<!-- è¡¨ç¤ºæ­¤åŒ…ä¸‹çš„ç±»å°†ä¼šè¢«å®¹å™¨æ‰«æåˆ°, å¹¶ä¸”å¸¦æœ‰@Beanç›¸å…³æ³¨è§£çš„ç±»ä¼šè¢«æ³¨å†Œåˆ°beanå·¥åŽ‚ -->	
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
	<scanners>
		<scanner location="com.test"/>
	</scanners>
	
<<<<<<< HEAD
	<!-- webÓ¦ÓÃµÄÏà¹ØÅäÖÃ -->
=======
	<!-- webåº”ç”¨çš„ç›¸å…³é…ç½® -->
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
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
<<<<<<< HEAD
#### HttpBean´úÂëÊµÀý
```java
@HTTP("/start") // ±íÊ¾´ËÀàÊÇÒ»¸öHttpÇëÇóµÄÓ³ÉäÀà
public class HelloWorld {

	// ×Ô¶¯×¢ÈëManµÄÊµÀý, Èç¹ûÈÝÆ÷ÖÐ´æÔÚ
	private @Injection Man man;

	// Ö»ÔÊÐíGetÇëÇó·ÃÎÊ´Ë·½·¨ @UrlHolder±íÊ¾urlÉÏÉùÃ÷µÄ±äÁ¿@test
	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		// transferÊÇÄÚÖÃµÄÀà£¬ ÓÃÀ´½«Êý¾Ý´æµ½servletµÄ¸÷×÷ÓÃÓò(request, session)
		transfer.ofRequest("q", man);
		// ±íÊ¾×ª·¢µ½/WEB-INF/index.jsp
		// µ±·µ»ØÖµÎª"->/index"Ê±±íÊ¾ÖØ¶¨Ïò
		return "/index";
	}

	// Ö»ÔÊÐíPostÇëÇó·ÃÎÊ´Ë·½·¨, @JSON±íÊ¾·µ»ØµÄ¶ÔÏó»á±»×ª»»Îªjson×Ö·û´®ÏìÓ¦µ½Ò³Ãæ
	@POST("/no")
	@JSON
	public Man test2() {
		return man;
	}
	
	// ÎÄ¼þÉÏ´«µÄ´¦Àí
	@GET("/upload")
	public void test5(Multipart multipart) throws IOException {
		// file±íÊ¾Ò³Ãæinput±êÇ©µÄname
		FileInf fileInf = multipart.getFileInf("file");
		// ½«ÎÄ¼þÐ´ÈëÖ¸¶¨µÄÎ»ÖÃ
		fileInf.writeTo("c:/data/demo.jpg");
	}
	

	// ÄÚÇ¶tomcatµÄÐÎÊ½Æô¶¯Ó¦ÓÃ
=======
#### HttpBeanä»£ç å®žä¾‹
```java
@HTTP("/start") // è¡¨ç¤ºæ­¤ç±»æ˜¯ä¸€ä¸ªHttpè¯·æ±‚çš„æ˜ å°„ç±»
public class HelloWorld {

	// è‡ªåŠ¨æ³¨å…¥Mançš„å®žä¾‹, å¦‚æžœå®¹å™¨ä¸­å­˜åœ¨
	private @Injection Man man;

	// åªå…è®¸Getè¯·æ±‚è®¿é—®æ­¤æ–¹æ³• @UrlHolderè¡¨ç¤ºurlä¸Šå£°æ˜Žçš„å˜é‡@test
	@GET("/get/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		// transferæ˜¯å†…ç½®çš„ç±»ï¼Œ ç”¨æ¥å°†æ•°æ®å­˜åˆ°servletçš„å„ä½œç”¨åŸŸ(request, session)
		transfer.ofRequest("q", man);
		// è¡¨ç¤ºè½¬å‘åˆ°/WEB-INF/index.jsp
		// å½“è¿”å›žå€¼ä¸º"->/index"æ—¶è¡¨ç¤ºé‡å®šå‘
		return "/index";
	}

	// åªå…è®¸Postè¯·æ±‚è®¿é—®æ­¤æ–¹æ³•, @JSONè¡¨ç¤ºè¿”å›žçš„å¯¹è±¡ä¼šè¢«è½¬æ¢ä¸ºjsonå­—ç¬¦ä¸²å“åº”åˆ°é¡µé¢
	@POST("/post/@test")
	@JSON
	public Man test2(@UrlHolder String test) {
		System.out.println(test);
		return man;
	}
	
	// æ–‡ä»¶ä¸Šä¼ 
	@GET("/upload")
	public void test3(Multipart multipart) throws IOException {
		// fileè¡¨ç¤ºé¡µé¢inputæ ‡ç­¾çš„name
		FileInf fileInf = multipart.getFileInf("file");
		// å°†æ–‡ä»¶å†™å…¥æŒ‡å®šçš„ä½ç½®
		fileInf.writeTo("c:/data/demo.jpg");
	}
	
	// æ–‡ä»¶ä¸‹è½½
	@GET("/downloads")
	public void test4(Share share) {
		share.shareFile("C:/hello.jpg");
	}

	// å†…åµŒtomcatçš„å½¢å¼å¯åŠ¨åº”ç”¨
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
	public static void main(String[] args) {
		MainStarter.start(HelloWorld.class);
	}
}
```

<<<<<<< HEAD
#### Í¨¹ýHttpChannel»ñÈ¡HttpBean·¢³öµÄÐòÁÐ»¯¶ÔÏó
```java
@HTTP("/demo")
public class HttpTest {

	// ¹ØÓÚÐòÁÐ»¯¶ÔÏóµÄ´«Êä
	@GET("/class")
	@Serialize
	public Man test3(Share share) {
		// ·¢ËÍÒ»¸öÐòÁÐ»¯¶ÔÏó
		Man man = new Man();
		man.setName("ÕÅÑ§ÓÑ");
=======
#### é€šè¿‡HttpChannelèŽ·å–HttpBeanå‘å‡ºçš„åºåˆ—åŒ–å¯¹è±¡
```java
// æœåŠ¡ç«¯
@HTTP("/demo")
public class HttpTest {

	// å…³äºŽåºåˆ—åŒ–å¯¹è±¡çš„ä¼ è¾“ @Serializeæ³¨è§£è¡¨ç¤ºè¿”å›žçš„å¯¹è±¡å°†è¢«åºåˆ—åŒ–åˆ°å“åº”å¤´ä¸­ï¼ˆè¿”å›žçš„å¯¹è±¡éœ€è¦å®žçŽ°SerializableæŽ¥å£ï¼‰
	@GET("/class")
	@Serialize
	public Man test() {
		// å‘é€ä¸€ä¸ªåºåˆ—åŒ–å¯¹è±¡
		Man man = new Man();
		man.setName("å¼ å­¦å‹");
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
		return man;
	}
	
	public static void main(String[] args) {
		MainStarter.start(HttpTest.class);
	}
}

<<<<<<< HEAD
=======
// å®¢æˆ·ç«¯
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
public class Test {
	public static void main(String[] args) {
		HttpChannel channel = new HttpChannel("127.0.0.1", 9900);
		Man man = (Man)channel.getObject("/demo/class", Pattern.GET);
		System.out.println(man.getName());
<<<<<<< HEAD
		// ´Ë´¦manµÄnameÎª "ÕÅÑ§ÓÑ"
		
		// Ö»Ê¹ÓÃÒ»´ÎµÄÊ±ºòÓ¦¸Ã¹ØµôsocketÁ¬½Ó
		channel.close();
=======
		// æ­¤å¤„mançš„nameä¸º "å¼ å­¦å‹"
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
	}
}
```

<<<<<<< HEAD
* author: ÁõÑó, ÁºÌì¶«
=======
* author: åˆ˜æ´‹, æ¢å¤©ä¸œ
>>>>>>> b151ce2e14ffcc6001f3d432fab7a9f2e1536c46
