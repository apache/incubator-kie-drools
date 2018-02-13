package org.drools.modelcompiler.inlinecast;

public class ICB extends ICAbstractB {

    private ICAbstractC someC;

    public ICAbstractC getSomeC() {
        return someC;
    }

    public void setSomeC(ICAbstractC someC) {
        this.someC = someC;
    }

    public String onlyConcrete() {
        return "Hello";
    }
}
