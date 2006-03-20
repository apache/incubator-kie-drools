package org.drools.rule;

public class Not extends GroupElement {
    public Object getChild() {
        return getChildren().get( 0 );
    }

}
