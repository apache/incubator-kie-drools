/**
 * 
 */
package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class AccessorKey
    implements
    Externalizable {
    private static final long serialVersionUID = 400;

    private String            className;
    private String            fieldName;
    private int               hashCode;
    private AccessorType      type;
    
    //private 

    public AccessorKey() {
    }

    public AccessorKey(String className,
                       String fieldName,
                       AccessorType type) {
        super();
        this.className = className;
        this.fieldName = fieldName;

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + className.hashCode();
        result = PRIME * result + ( (fieldName == null) ? 0 : fieldName.hashCode() );
        result = PRIME * result + type.hashCode();
        this.hashCode = result;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( className );
        out.writeObject( fieldName ); // use writeObject so it can be null
        out.writeInt( hashCode );
        out.writeObject( type );
    }
    
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        className = in.readUTF();
        fieldName = ( String ) in.readObject();  // use writeObject so it can be null
        hashCode = in.readInt();
        type = ( AccessorType ) in.readObject();
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public AccessorType getType() {
        return this.type;
    }

    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AccessorKey other = (AccessorKey) obj;
        
        if ( className == null ) {
            if ( other.className != null ) return false;
        } else if ( !className.equals( other.className ) ) return false;
        
        if ( fieldName == null ) {
            if ( other.fieldName != null ) return false;
        } else if ( !fieldName.equals( other.fieldName ) ) return false;

        if ( type == null ) {
            if ( other.type != null ) return false;
        } else if ( !type.equals( other.type ) ) return false;
        return true;
    }

    public String toString() {
        return this.className + "-" + this.fieldName;
    }
    
    public static enum AccessorType {
        FieldAccessor, ClassObjectType; //ObjectAccessor, GlobalAccessor;
    }
}