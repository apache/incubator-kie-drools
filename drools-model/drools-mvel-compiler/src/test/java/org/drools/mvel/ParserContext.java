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

    public void addInput(String key, Class<?> klass) {
    	inputs.put(key, klass);

    }

    Map<String, Class<?>> inputs = new HashMap<>();

    public ParserContext withInput(String var1, Class<?> klass) {
        inputs.put(var1, klass);
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
