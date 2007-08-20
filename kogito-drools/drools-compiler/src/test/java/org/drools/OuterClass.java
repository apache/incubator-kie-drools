package org.drools;

public class OuterClass {
    private String attr1;

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public static class InnerClass {
        private int intAttr;

        public InnerClass(int intAttr) {
            super();
            this.intAttr = intAttr;
        }

        public int getIntAttr() {
            return intAttr;
        }

        public void setIntAttr(int intAttr) {
            this.intAttr = intAttr;
        }
    }
}
