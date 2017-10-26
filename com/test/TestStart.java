package com.test;


import java.io.IOException;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.POST;
import com.nymph.annotation.UrlHolder;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Share;
import com.nymph.transfer.Transfer;

@HTTP
public class TestStart {

	private @Injection Person man;

	@GET("/yes/@test")
	public String test(@UrlHolder("test") String field, Transfer transfer) {
		return "/index";
	}

	@POST("/no")
	@JSON
	public Person test2(Person person) {
		return person;
	}
	
	@GET("/test")
	@JSON
	public String test3(String url) {
		return "success" + url;
	}
	
	@GET("/class")
	public void test4(Share share) throws IOException {
		Man man = new Man("张学友");
		share.shareObject(man);
	}

	public static void main(String[] args) throws Exception {
		 MainStarter.start(TestStart.class);
	}
}
