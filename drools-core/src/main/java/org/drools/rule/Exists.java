package org.drools.rule;


public class Exists extends ConditionalElement {
    public Object getChild(){
        return getChildren().get( 0 );
    }
}
