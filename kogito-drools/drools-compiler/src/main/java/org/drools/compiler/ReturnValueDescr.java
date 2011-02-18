package org.drools.compiler;

import org.drools.lang.descr.BaseDescr;

public class ReturnValueDescr extends BaseDescr {
    private String text;
    
    public ReturnValueDescr() {
        
    }
    
    public ReturnValueDescr(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
