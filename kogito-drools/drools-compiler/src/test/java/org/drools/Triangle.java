package org.drools;

public class Triangle {
    public enum Type { INCOMPLETE, UNCLASSIFIED, EQUILATERAL, ISOSCELES, RECTANGLED, ISOSCELES_RECTANGLED, ACUTE, OBTUSE; }
    
    int a, b, c;
    private Type type = Type.UNCLASSIFIED;

    public Triangle() {
    }

    public Triangle(int a,
                    int b,
                    int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType( Type type ) {
        this.type = type;
    }
    
}
