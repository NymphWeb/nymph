package com.nymph.consumer;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nymph.link.client.LinkProxy;
import com.nymph.link.demo.Nymph;
import com.nymph.link.demo.TestService;

public class TestClient {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        LinkProxy rpcProxy = context.getBean(LinkProxy.class);
        TestService testService = rpcProxy.create(TestService.class);
        String result = testService.say("World");
        System.out.println(result);
        Nymph person = new Nymph();
        person.setName("nymph");
        person.setAge(23);
        //Long time0 = System.currentTimeMillis();
        Nymph nymph = testService.say(person);
             System.out.println(nymph.getName());
    }
}
