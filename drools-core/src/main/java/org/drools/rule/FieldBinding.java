package org.drools.rule;

import org.drools.spi.Constraint;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;



public class FieldBinding extends Binding implements Constraint {
    private final Extractor  extractor;

    private final int        column;    
    

    public FieldBinding(String identifier,
                        ObjectType objectType,
                        Extractor extractor,
                        int column) {
        super( identifier,
               objectType,
               extractor );
        this.extractor = extractor;
        this.column = column;
    }


    public int getColumn() {
        return this.column;
    }
      
}
