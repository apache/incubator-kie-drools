package org.drools.workbench.models.testscenarios.shared;

public class FactAssignmentField
        implements Field {

    private String fieldName;

    private Fact fact;

    public FactAssignmentField() {
    }

    public FactAssignmentField( final String fieldName,
                                final String factType ) {
        this.fieldName = fieldName;
        this.fact = new Fact( factType );
    }

    public void setName( final String fieldName ) {
        this.fieldName = fieldName;
    }

    public void setFact( final Fact fact ) {
        this.fact = fact;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public Fact getFact() {
        return fact;
    }
}
