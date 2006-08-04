package org.drools.facttemplates;

import org.drools.RuntimeDroolsException;
import org.drools.base.ValueType;
import org.drools.spi.Evaluator;

public class FieldTemplateImpl
    implements
    FieldTemplate {

    private static final long serialVersionUID = 320;

    private final String      name;
    private final int         index;
    private final ValueType   valueType;

    public FieldTemplateImpl(String name,
                             int index,
                             Class clazz) {
        this.name = name;
        this.index = index;
        this.valueType = ValueType.determineValueType( clazz );
    }

    /* (non-Javadoc)
     * @see org.drools.facttemplates.FieldTemplate#getIndex()
     */
    public int getIndex() {
        return this.index;
    }

    /* (non-Javadoc)
     * @see org.drools.facttemplates.FieldTemplate#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see org.drools.facttemplates.FieldTemplate#getValueType()
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.index;
        result = PRIME * result + this.name.hashCode();
        result = PRIME * result + this.valueType.hashCode();
        return result;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }
        
        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }
        
        final FieldTemplateImpl other = (FieldTemplateImpl) object;
        
        return this.index == other.index && this.name.equals( other.name ) && this.valueType.equals( other.valueType );
    }

    
}
