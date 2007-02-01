package org.drools.testing.core.wrapper;

import org.drools.testing.core.beans.Field;

public class FieldWrapper extends Field {
	
	public FieldWrapper () {
		super();
	}

	public boolean equals(java.lang.Object obj)
    {
        if ( this == obj )
            return true;
        
        if (obj instanceof Field) {
        
        	Field temp = (Field)obj;
            if (this.getName() != null) {
                if (temp.getName() == null) return false;
                else if (!(this.getName().equals(temp.getName()))) 
                    return false;
            }
            else if (temp.getName() != null)
                return false;
            
            return true;
        }
        return false;
    } 
}
