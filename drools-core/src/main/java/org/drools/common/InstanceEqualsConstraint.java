package org.drools.common;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import  org.drools.spi.FieldConstraint;
import org.drools.spi.Tuple;

public class InstanceEqualsConstraint implements FieldConstraint {        
    private Declaration[] declarations = new Declaration[0];
    
    private int otherColumn;

    public InstanceEqualsConstraint(int otherColumn) {
        this.otherColumn = otherColumn;
    }    
    
    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return ! (workingMemory.getObject( tuple.get( this.otherColumn ) )== workingMemory.getObject( handle ) );
    }
    
    public String toString() {
        return this.getClass().getSimpleName()+"[ otherColumn == "+this.otherColumn+" ]";
    }

}
