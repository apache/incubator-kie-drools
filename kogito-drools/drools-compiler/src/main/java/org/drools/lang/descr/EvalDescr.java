package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvalDescr extends PatternDescr {
    private String text;

    public EvalDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
