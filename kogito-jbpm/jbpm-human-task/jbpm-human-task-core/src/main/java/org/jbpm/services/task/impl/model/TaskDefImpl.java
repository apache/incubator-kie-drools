/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name="TaskDef")
@SequenceGenerator(name="taskDefIdSeq", sequenceName="TASK_DEF_ID_SEQ")
public class TaskDefImpl implements org.kie.internal.task.api.model.TaskDef {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="taskDefIdSeq")
    @Column(name = "TaskDefId")
    private long id;
    
    private String name;
    
    private int priority;


    public TaskDefImpl() {
        
    }

    
    public TaskDefImpl(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( id );
        if( name == null ) { 
            name = "";
        }
        out.writeUTF( name );
        
        out.writeInt( priority );        
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readLong();
        name = in.readUTF();
        priority = in.readInt();       
    }
    
}
