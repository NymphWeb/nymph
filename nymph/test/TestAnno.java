package test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.UrlVar;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Transfer;

@HTTP("/start")
public class TestAnno {

	private  Man person;

	@GET("/yes/@(test)")
	public String test(@UrlVar("test") String field, Transfer transfer) {
		transfer.forRequest("q", person);
		return "/index";
	}

	@GET("/no/@{test}")
	@JSON
	public String test2() {
		System.out.println(person.getController());
		return person.toString();
	}
	@Injection
	public TestAnno(Man man) {
		person = man;
	}
	
	public TestAnno() {
	}

	public static void main(String[] args) throws Exception {
		MainStarter.start(TestAnno.class);
	}
}
