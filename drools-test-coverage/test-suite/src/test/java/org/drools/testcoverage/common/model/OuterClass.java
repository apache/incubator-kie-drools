package org.drools.testcoverage.common.model;

public class OuterClass {
    private String attr1;

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(final String attr1) {
        this.attr1 = attr1;
    }

    public static class InnerClass {
        private int intAttr;

        public InnerClass(final int intAttr) {
            super();
            this.intAttr = intAttr;
        }

        public int getIntAttr() {
            return intAttr;
        }

        public void setIntAttr(final int intAttr) {
            this.intAttr = intAttr;
        }
    }
}
