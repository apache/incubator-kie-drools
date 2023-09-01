package org.drools.tms.beliefsystem.defeasible;

public enum DefeasibleRuleNature {


    STRICT( Strict.class.getSimpleName() ),
    DEFEASIBLE( Defeasible.class.getSimpleName() ),
    DEFEATER( Defeater.class.getSimpleName() );

    private String label;

    DefeasibleRuleNature( String lab ) {
        this.label = lab;
    }

    public String getLabel() {
        return label;
    }

}
