package test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.UrlHolder;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Transfer;


@HTTP("/start")
public class HelloWorld {

	// �Զ�ע��Man��ʵ��, ��������д���
	private @Injection Man man;

	// Get������Է��ʵ��˷��� @UrlHolder��ʾurl�������ı���@test
	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		transfer.forRequest("q", man);
		return "/index";
	}

	// Post������Է��ʵ��˷���, @JSON��ʾ���ص�ֵΪjson��ʽ
	@POST("/no/@test")
	@JSON
	public Man test2() {
		return man;
	}

	// ��Ƕtomcat����
	public static void main(String[] args) throws Exception {
		MainStarter.start(HelloWorld.class);
	}
}
