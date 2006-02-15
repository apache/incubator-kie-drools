package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeDescr extends PatternDescr {
    private String text;    
    private List types = Collections.EMPTY_LIST;
    private List identifiers;
    
    public AttributeDescr(String text) {
        this.text = text;
    }
    
    public void AddParatmer(String type, String identifier) {
        if ( this.types == Collections.EMPTY_LIST ) {
            this.types = new ArrayList(1);
            this.identifiers = new ArrayList(1);
        }
        this.types.add( type );
        this.identifiers.add( identifier );
    }
    
    public String getText() {
        return this.text;
    } 
}
