package org.drools.compiler.integrationtests.concurrency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Karaoke {

    private Map<String, Album> dvd = new HashMap<>();

    public Map<String, Album> getDvd() {
        return dvd;
    }

    public void fix() {
        dvd = Collections.unmodifiableMap(dvd);
    }
}