package org.drools.persistence.processinstance.variabletypes;

import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 *
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
@Entity
public class SerializablePersistedVariable extends VariableInstanceInfo {

	private static final long serialVersionUID = 510l;
	
	@Lob
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final SerializablePersistedVariable other = (SerializablePersistedVariable) obj;
        if (!Arrays.equals(this.content, other.content)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + Arrays.hashCode(this.content);
        return hash;
    }
    
    public String toString() {
    	return super.toString() + " byteSize=" + (content == null ? 0 : content.length); 
    }

  

}
