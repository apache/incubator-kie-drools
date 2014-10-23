package org.drools.examples.diagnostics;

public class WontStart {
    public static Question newQ1() {
        return new Question("1", "Starter cranks?");
    }

    public static Question newQ2() {
        return new Question("2", "Starter spins?");
    }

    public static Question newQ3() {
        return new Question("3", "Battery read over 12V?");
    }

    public static Question newQ4() {
        return new Question("4", "Cleaned terminals?");
    }
}
