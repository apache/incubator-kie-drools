package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class EvalDescr extends PatternDescr
    implements
    ConditionalElementDescr {
    private String   text;

    private String[] declarations;

    private String   classMethodName;

    public EvalDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public void setDeclarations(String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }
}
