/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.jbpm.task.utils.CollectionUtils;

@Entity
public class Task implements Externalizable {
    /**
     * WSHT uses a name for the unique identifier, for now we use a generated ID which is also the key, which can be
     * mapped to the name or a unique name field added later.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long                 id;
    
    @Version
    @Column(name = "OPTLOCK")
    private int                  version;

    /**
     * While WSHT says this is an expression, it always resolves to an integer, so resolve before setting
     * default value is 0.
     */
    private int                  priority;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Task_Names_Id", nullable = true)
    private List<I18NText> names        = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Task_Subjects_Id", nullable = true)
    private List<I18NText> subjects     = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Task_Descriptions_Id", nullable = true)
    private List<I18NText> descriptions = Collections.emptyList();


    @Embedded
    private PeopleAssignments    peopleAssignments;

    @Embedded
    private Delegation           delegation;

    @Embedded
    private TaskData             taskData;

    @Embedded
    private Deadlines            deadlines;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "Task_Id", nullable = true)
    private List<SubTasksStrategy> subTaskStrategies = Collections.emptyList();
    
    @Basic
    private Short archived = 0;
    

    public Task() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( id );
        out.writeInt( priority );
        out.writeShort( archived );
        CollectionUtils.writeI18NTextList( names, out );
        CollectionUtils.writeI18NTextList( subjects, out );
        CollectionUtils.writeI18NTextList( descriptions, out );

        if ( peopleAssignments != null ) {
            out.writeBoolean( true );
            peopleAssignments.writeExternal( out );
        } else {
            out.writeBoolean( false );
        }

        if ( delegation != null ) {
            out.writeBoolean( true );
            delegation.writeExternal( out );
        } else {
            out.writeBoolean( false );
        }

        if ( taskData != null ) {
            out.writeBoolean( true );
            taskData.writeExternal( out );
        } else {
            out.writeBoolean( false );
        }

        if ( deadlines != null ) {
            out.writeBoolean( true );
            deadlines.writeExternal( out );
        } else {
            out.writeBoolean( false );
        }

        out.writeInt( subTaskStrategies.size() );
        for( SubTasksStrategy strategy : subTaskStrategies ) {
            out.writeUTF(strategy.getName());
            strategy.writeExternal( out );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readLong();
        priority = in.readInt();
        archived = in.readShort();
        names = CollectionUtils.readI18NTextList( in );
        subjects = CollectionUtils.readI18NTextList( in );
        descriptions = CollectionUtils.readI18NTextList( in );

        if ( in.readBoolean() ) {
            peopleAssignments = new PeopleAssignments();
            peopleAssignments.readExternal( in );
        }

        if ( in.readBoolean() ) {
            delegation = new Delegation();
            delegation.readExternal( in );
        }

        if ( in.readBoolean() ) {
            taskData = new TaskData();
            taskData.readExternal( in );
        }

        if ( in.readBoolean() ) {
            deadlines = new Deadlines();
            deadlines.readExternal( in );
        }

        int size = in.readInt();
        List<SubTasksStrategy> list = new ArrayList<SubTasksStrategy>(size);
        for ( int i = 0; i < size; i++ ) {
            String name = in.readUTF();
            SubTasksStrategy strategy = SubTasksStrategyFactory.newStrategy(name) ;
            strategy.readExternal( in );
            list.add( strategy );
        }
       subTaskStrategies = list;

    }
    
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

     public Boolean isArchived() {
        if (archived == null) {
            return null;
        }
        return (archived == 1) ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setArchived(Boolean archived) {
        if (archived == null) {
            this.archived = null;
        } else {
            this.archived = (archived == true) ? new Short("1") : new Short("0");
        }
    }
    
    public int getVersion() {
        return this.version;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<I18NText> getNames() {
        return names;
    }

    public void setNames(List<I18NText> names) {
        this.names = names;
    }

    public List<I18NText> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<I18NText> subjects) {
        this.subjects = subjects;
    }

    public List<I18NText> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<I18NText> descriptions) {
        this.descriptions = descriptions;
    }

    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        this.peopleAssignments = peopleAssignments;
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    public Deadlines getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(Deadlines deadlines) {
        this.deadlines = deadlines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + version;
        result = prime * result + priority;
        result = prime * result + CollectionUtils.hashCode( descriptions );
        result = prime * result + CollectionUtils.hashCode( names );
        result = prime * result + CollectionUtils.hashCode( subjects );
        result = prime * result + ((peopleAssignments == null) ? 0 : peopleAssignments.hashCode());
        result = prime * result + ((delegation == null) ? 0 : delegation.hashCode());
        result = prime * result + ((taskData == null) ? 0 : taskData.hashCode());
        result = prime * result + ((deadlines == null) ? 0 : deadlines.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof Task) ) return false;
        Task other = (Task) obj;
        if ( this.version != other.version ) {
            return false;
        }
        if ( deadlines == null ) {
            if ( other.deadlines != null ) {

            }
        } else if ( !deadlines.equals( other.deadlines ) ) return false;
        if ( delegation == null ) {
            if ( other.delegation != null ) return false;
        } else if ( !delegation.equals( other.delegation ) ) return false;
        if ( peopleAssignments == null ) {
            if ( other.peopleAssignments != null ) return false;
        } else if ( !peopleAssignments.equals( other.peopleAssignments ) ) return false;

        if ( priority != other.priority ) return false;
        if ( taskData == null ) {
            if ( other.taskData != null ) return false;
        } else if ( !taskData.equals( other.taskData ) ) return false;
        return ( CollectionUtils.equals( descriptions, other.descriptions ) && CollectionUtils.equals( names, other.names )
        && CollectionUtils.equals( subjects, other.subjects ));
    }

    /**
     * @return the subTaskStrategies
     */
    public List<SubTasksStrategy> getSubTaskStrategies() {
        return subTaskStrategies;
    }

    /**
     * @param subTaskStrategies the subTaskStrategies to set
     */
    public void setSubTaskStrategies(List<SubTasksStrategy> subTaskStrategies) {
        this.subTaskStrategies = subTaskStrategies;
    }

   

}
