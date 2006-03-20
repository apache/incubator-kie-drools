package org.drools.rule;

public class Exists extends GroupElement {
    public Object getChild() {
        return getChildren().get( 0 );
    }
}
