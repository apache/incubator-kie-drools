package org.drools.mvel;

import java.util.HashMap;
import java.util.Map;

public class ParserContext {

    private ParserConfiguration pconf;

    public ParserContext(ParserConfiguration pconf) {

        this.pconf = pconf;
    }

    public ParserContext() {


    }

    public static ParserContext create() {
        return new ParserContext();
    }

    public void setStrongTyping(boolean b) {

    }

    public void addInput(String a, Class<?> longClass) {

    }

    Map<String, Class> inputs = new HashMap<>();

    public ParserContext withInput(String var1, Class<Double> doubleClass) {
        inputs.put(var1, doubleClass);
        return this;
    }

    public ParserContext stronglyTyped() {
        return this;
    }

    public Map getMap() {
        return inputs;
    }

    public void setStrictTypeEnforcement(boolean b) {

    }
}
