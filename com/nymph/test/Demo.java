package com.nymph.test;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.JSON;

@HTTP
public class Demo {

    @GET("/get")
    @JSON
    public String test() {
        return "success";
    }
}
