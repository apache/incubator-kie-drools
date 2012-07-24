package org.jbpm.task;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Language {

    @Column(nullable=false)
    private String mapkey;

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String language) {
        this.mapkey = language;
    }
 
    public Language() { 
        
    }
    
    public Language(String lang) { 
        this.mapkey = lang;
    }

    @Override
    public int hashCode() {
        return this.mapkey == null ? 0 : this.mapkey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == null ) { 
           if( this.mapkey == null ) { 
               return true;
           }
           return false;
        }
        else if( obj instanceof String ) { 
            return obj.equals(this.mapkey);
        }
        else if( obj instanceof Language ) { 
            Language other = (Language) obj;
            if( this.mapkey == null ) { 
                return (other).mapkey == null;
            }
            else { 
                return this.mapkey.equals(other.mapkey);
            }
        }
        else { 
            return false;
        }
    }

    @Override
    public String toString() {
        return mapkey == null ? null : mapkey.toString();
    }
    
}
