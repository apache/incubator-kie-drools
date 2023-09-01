package org.drools.tms.beliefsystem.defeasible;

public class Join {
    private JoinEntry firstLeft;
    private JoinEntry lastLeft;


    private JoinEntry firstRight;
    private JoinEntry lastRight;

    private static interface JoinEntry {
        JoinEntry getNext();
        JoinEntry getPrevious();
    }
}
