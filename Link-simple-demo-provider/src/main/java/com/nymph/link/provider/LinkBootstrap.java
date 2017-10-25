package com.nymph.link.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LinkBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkBootstrap.class);

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        LOGGER.debug("start server");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
