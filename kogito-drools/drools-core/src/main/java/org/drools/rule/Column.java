package org.drools.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.base.ClassObjectType;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.Extractor;
import org.drools.spi.FieldConstraint;
import org.drools.spi.ObjectType;

public class Column implements Serializable {
    private final ObjectType    objectType;
    private List                constraints  = Collections.EMPTY_LIST;   
    final Declaration           declaration;
    private final int           index;

    public Column(int index,
                  ObjectType objectType) {
        this( index,
              objectType,
              null );
    }

    public Column(int index,
                  ObjectType objectType,
                  String identifier)

    {
        this.index = index;
        this.objectType = objectType;
        if (identifier != null) {
            this.declaration = new Declaration(identifier,  new ColumnExtractor( objectType ), index);
        } else {
            this.declaration = null;
        }
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public List getConstraints() {
        return Collections.unmodifiableList( this.constraints );
    }

    public void addConstraint(FieldConstraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList( 1 );
        }
        this.constraints.add( constraint );
    }
    
    public Declaration addDeclaration(String identifier, Extractor extractor) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList( 1 );
        }
        Declaration declaration = new Declaration( identifier,
                                                   extractor,
                                                   index );
        this.constraints.add( declaration );
        return declaration;
        
    }    

    public boolean isBound() {
        return (this.declaration != null);
    }

    public Declaration getDeclaration() {
        return this.declaration;
    }

    public int getIndex() {
        return this.index;
    }
    
    public String toString() {
        return "Column type='"+ this.objectType + "', index='"+ this.index + "' identifer='"+ this.declaration.getIdentifier() + "'";
    }
}
