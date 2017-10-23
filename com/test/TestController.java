package com.test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.UrlHolder;
import com.nymph.transfer.Transfer;

@HTTP
public class TestController {

	@GET("/test/@page/qwe/@num")
	public String test(@UrlHolder("page")String qwe, Transfer transfer) {
		transfer.forRequest("q", qwe);
		return "/index";
	}
	
	@GET("/test/@page/asd/@num")
	public String test2(@UrlHolder("page")String qwe, Transfer transfer) {
		transfer.forRequest("q", qwe);
		return "/index";
	}
}
