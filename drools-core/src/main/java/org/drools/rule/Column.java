package org.drools.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.drools.spi.ObjectType;

public class Column
{
    private final ObjectType    objectType;
    private List                constraints = Collections.EMPTY_LIST;
    private final ColumnBinding binding;
    private final int           index;

    public Column(int index,
                  ObjectType objectType)
    {
        this( index, 
              objectType,
              null );
    }

    public Column(int index,
                  ObjectType objectType,
                  ColumnBinding binding)
    {
        this.index = index;
        this.objectType = objectType;
        this.binding = binding;
    }

    public ObjectType getObjectType()
    {
        return this.objectType;
    }

    public List getConstraints()
    {
        return Collections.unmodifiableList( this.constraints );
    }

    public void addConstraint(Object constraint)
    {
        if (constraints == Collections.EMPTY_LIST)
        {
            this.constraints = new ArrayList( 1 );
        }
        this.constraints.add( constraint );
    }

    public boolean isBound()
    {
        return (binding != null);
    }

    public Binding getBinding()
    {
        return this.binding;
    }

    public int getIndex()
    {
        return this.index;
    }
}
