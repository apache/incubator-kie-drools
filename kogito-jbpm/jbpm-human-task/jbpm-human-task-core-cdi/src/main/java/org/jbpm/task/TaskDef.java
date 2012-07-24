/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author salaboy
 */
@Entity
public class TaskDef implements Serializable {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    
    private String name;
    
    private int priority;
    @Embedded
    private PeopleAssignments peopleAssignments;
    @OneToMany
    private List<Delegation> delegations;
    @OneToMany
    private List<CompletionBehavior> completionBehaviors;
    @OneToMany
    private List<PresentationElement> presentationElements;
    @OneToMany
    private List<Rendering> renderings;

    public TaskDef() {
        
    }

    
    public TaskDef(String name) {
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

    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    public void setPeopleAssignments(PeopleAssignments peopleAssignments) {
        this.peopleAssignments = peopleAssignments;
    }


    public List<Delegation> getDelegations() {
        return delegations;
    }

    public void setDelegations(List<Delegation> delegations) {
        this.delegations = delegations;
    }

    public List<CompletionBehavior> getCompletionBehaviors() {
        return completionBehaviors;
    }

    public void setCompletionBehaviors(List<CompletionBehavior> completionBehaviors) {
        this.completionBehaviors = completionBehaviors;
    }

    public List<PresentationElement> getPresentationElements() {
        return presentationElements;
    }

    public void setPresentationElements(List<PresentationElement> presentationElements) {
        this.presentationElements = presentationElements;
    }

    public List<Rendering> getRenderings() {
        return renderings;
    }

    public void setRenderings(List<Rendering> renderings) {
        this.renderings = renderings;
    }
    
    
    
}
