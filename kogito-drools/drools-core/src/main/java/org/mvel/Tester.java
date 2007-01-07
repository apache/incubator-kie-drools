package org.mvel;

import org.mvel.util.ParseTools;

import java.util.Map;
import java.util.HashMap;

public class Tester {
    public static void main(String[] args) {
        Map<Integer, String> b = new HashMap<Integer, String>();

        Class[] test = new Class[] { String.class, int.class };
        b.put(ParseTools.createClassSignatureHash(test), "Hello");

        System.out.println(ParseTools.createClassSignatureHash(test));

        System.out.println(b.containsKey(ParseTools.createClassSignatureHash(new Class[] { String.class, int.class })));

    }
}
