package test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.POST;
import com.nymph.annotation.UrlHolder;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Transfer;

@HTTP("/start")
public class TestStart {

	private @Injection Man man;

	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		return "/index";
	}

	@POST("/no")
	@JSON
	public Person test2(Person person) {
		return person;
	}

	public static void main(String[] args) throws Exception {
		 MainStarter.start(TestStart.class);
	}
}
