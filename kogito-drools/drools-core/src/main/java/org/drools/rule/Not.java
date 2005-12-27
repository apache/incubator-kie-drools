package org.drools.rule;


public class Not extends ConditionalElement {
    public Object getChild(){
        return getChildren().get( 0 );
    }

}
