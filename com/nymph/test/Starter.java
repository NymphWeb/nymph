package com.nymph.test;

import com.nymph.start.MainStarter;
import com.nymph.start.NymphStarter;

@NymphStarter(packageScanner = "com.nymph.test")
public class Starter {

    public static void main(String[] args) {
        MainStarter.start(Starter.class);
    }
}
