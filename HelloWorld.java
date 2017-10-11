package test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.UrlVar;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Transfer;


@HTTP("/start")
public class HelloWorld {

	// 自动注入Man的实例, 如果容器中存在
	private @Injection Man man;

	// 只有Get请求可以访问到此方法 @UrlVar表示url上声明的变量@("test")
	@GET("/yes/@(test)")
	public String test(@UrlVar("test") String field, Transfer transfer) {
		transfer.forRequest("q", man);
		return "/index";
	}

	// 只有Post请求可以访问到此方法, @JSON表示返回的值为json格式
	@POST("/no/@{test}")
	@JSON
	public Man test2() {
		return man;
	}

	public static void main(String[] args) throws Exception {
		MainStarter.start(TestAnno.class);
	}
}
