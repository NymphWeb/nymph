package com.nymph.link.provider;

import com.nymph.link.annotation.Service;
import com.nymph.link.demo.Nymph;
import com.nymph.link.demo.TestService;

@Service(TestService.class)
public class HelloServiceImpl implements TestService {

	@Override
	public String say(String name) {
		return "Hello,"+name +" Link is comming";
	}

	@Override
	public Nymph say(Nymph nymph) {
		nymph.setName(nymph.getName()+"Link has changed this name!");
		return nymph;
	}
}
