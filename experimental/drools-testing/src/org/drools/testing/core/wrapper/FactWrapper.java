package org.drools.testing.core.wrapper;

import org.drools.testing.core.beans.Fact;

public class FactWrapper extends Fact {

	public FactWrapper () {
		super();
	}
	
	public boolean equals(java.lang.Object obj)
    {
        if ( this == obj )
            return true;
        
        if (obj instanceof Fact) {
        
        	Fact temp = (Fact)obj;
            if (this.getType() != null) {
                if (temp.getType() == null) return false;
                else if (!(this.getType().equals(temp.getType()))) 
                    return false;
            }
            else if (temp.getType() != null)
                return false;
            
            return true;
        }
        return false;
    }
}
