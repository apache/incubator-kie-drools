package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.BaseDescr;

public class ReturnValueDescr extends BaseDescr {
    private String text;
    
    public ReturnValueDescr() { }
    
    public ReturnValueDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
