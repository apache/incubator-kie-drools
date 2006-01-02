package org.drools.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.spi.ObjectType;

public class Column {
    private final ObjectType    objectType;
    private List                constraints  = Collections.EMPTY_LIST;
    private List                declarations = Collections.EMPTY_LIST;
    private final ColumnBinding binding;
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
        this.binding = new ColumnBinding( identifier,
                                          this.objectType,
                                          this );
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public List getDeclarations() {
        return Collections.unmodifiableList( this.declarations );
    }

    public void addDeclaration(Declaration declaration) {
        if ( this.declarations == Collections.EMPTY_LIST ) {
            this.declarations = new ArrayList( 1 );
        }
        this.declarations.add( declaration );
    }

    public List getConstraints() {
        return Collections.unmodifiableList( this.constraints );
    }

    public void addConstraint(Object constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList( 1 );
        }
        this.constraints.add( constraint );
    }

    public boolean isBound() {
        return (this.binding != null);
    }

    public Binding getBinding() {
        return this.binding;
    }

    public int getIndex() {
        return this.index;
    }
}
