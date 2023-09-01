package org.drools.model.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class NamesGenerator {

    private static final AtomicInteger index = new AtomicInteger();

    private NamesGenerator() { }

    public static String generateName(String topic) {
        return "$" + topic + "$" + index.incrementAndGet() + "$";
    }
}
