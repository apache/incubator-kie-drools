/**
 * 
 */
package org.drools.persistence.processinstance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int    id;
    
//    @ManyToOne
//    private long    processInstanceId;
    
    private String name;

    public EventType() {

    }
//
//    public EventType(long    processInstanceId, String name) {
//        this.name = name;
//        this.processInstanceId = processInstanceId;
//    }
    
    public EventType(String name) {
        this.name = name;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public long getProcessInstanceId() {
//        return processInstanceId;
//    }
//
//    public void setProcessInstanceId(long processInstanceId) {
//        this.processInstanceId = processInstanceId;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        EventType other = (EventType) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }
 
}