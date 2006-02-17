package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvalDescr extends PatternDescr {
    private String text;

    private String[] declarations;    
    
    public EvalDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
    
    public void setDeclarations( String[] declarations) {
        this.declarations = declarations;
    }
    
    public String[] getDeclarations() {
        return this.declarations;
    }    
}
