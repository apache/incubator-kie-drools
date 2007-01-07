package org.mvel.util;

public class Sandbox {
    public static final int TEST = 1 << 1;

    public static void main(String[] args) {
        boolean bool;
        boolean second = true;

        int field = 10;

        long tm = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            bool = (field & TEST) != 0;
        }
        System.out.println(System.currentTimeMillis() - tm);

        tm = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            bool = second ? false : true;
        }
        System.out.println(System.currentTimeMillis() - tm);


    }

}
