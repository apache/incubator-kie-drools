package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.*;
import static org.jbpm.services.task.impl.model.xml.JaxbOrganizationalEntity.convertListFromInterfaceToJaxbImpl;
import static org.jbpm.services.task.impl.model.xml.JaxbOrganizationalEntity.convertListFromJaxbImplToInterface;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;


@XmlRootElement(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPeopleAssignments implements InternalPeopleAssignments {

    @XmlElement(name="task-initiator")
    @XmlSchemaType(name="string")
    private String taskInitiatorId;

    @XmlElement(name="potential-owner")
    private List<JaxbOrganizationalEntity> jaxbPotentialOwners;

    @XmlElement(name="business-administrator")
    private List<JaxbOrganizationalEntity> jaxbBusinessAdministrators;

    @XmlElement(name="excluded-owners")
    private List<JaxbOrganizationalEntity> jaxbExcludedOwners;

    @XmlElement(name="task-stakeholders")
    private List<JaxbOrganizationalEntity> jaxbTaskStakeholders;

    @XmlElement(name="recipients")
    private List<JaxbOrganizationalEntity> jaxbRecipients;
    
    public JaxbPeopleAssignments() { 
       // Default constructor for JAXB
    }
    
    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) { 
        User taskInitiatorUser = peopleAssignments.getTaskInitiator();
        if( taskInitiatorUser != null ) { 
            this.taskInitiatorId = taskInitiatorUser.getId();
        }
        this.jaxbBusinessAdministrators = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getBusinessAdministrators());
        this.jaxbExcludedOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getExcludedOwners());
        this.jaxbPotentialOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getPotentialOwners());
        this.jaxbRecipients = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getRecipients());
        this.jaxbTaskStakeholders = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getTaskStakeholders());
    }
    
    @Override
    @JsonIgnore
    public User getTaskInitiator() {
        if( this.taskInitiatorId != null ) { 
            return new GetterUser(this.taskInitiatorId);
        } 
        return null;
    }

    public void setTaskInitiator(User taskInitiatorUser) {
        if( taskInitiatorUser != null ) { 
            this.taskInitiatorId = taskInitiatorUser.getId();
        }
    }

    public String getTaskInitiatorId() {
        return taskInitiatorId;
    }

    public void setTaskInitiatorId(String taskInitiatorId) {
        this.taskInitiatorId = taskInitiatorId;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getPotentialOwners() {
        if( jaxbPotentialOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(jaxbPotentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.jaxbPotentialOwners = convertListFromInterfaceToJaxbImpl(potentialOwners);
    }

    public List<JaxbOrganizationalEntity> getJaxbPotentialOwners() {
        return jaxbPotentialOwners;
    }

    public void setJaxbPotentialOwners(List<JaxbOrganizationalEntity> jaxbPotentialOwners) {
        this.jaxbPotentialOwners = jaxbPotentialOwners;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( jaxbBusinessAdministrators == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(jaxbBusinessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.jaxbBusinessAdministrators = convertListFromInterfaceToJaxbImpl(businessAdministrators);
    }

    public List<JaxbOrganizationalEntity> getJaxbBusinessAdministrators() {
        return jaxbBusinessAdministrators;
    }

    public void setJaxbBusinessAdministrators(List<JaxbOrganizationalEntity> jaxbBusinessAdministrators) {
        this.jaxbBusinessAdministrators = jaxbBusinessAdministrators;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getExcludedOwners() {
        if( jaxbExcludedOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(jaxbExcludedOwners)); 
    }

    @Override
    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.jaxbExcludedOwners = convertListFromInterfaceToJaxbImpl(excludedOwners);
    }

    public List<JaxbOrganizationalEntity> getJaxbExcludedOwners() {
        return jaxbExcludedOwners;
    }

    public void setJaxbExcludedOwners(List<JaxbOrganizationalEntity> jaxbExcludedOwners) {
        this.jaxbExcludedOwners = jaxbExcludedOwners;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getTaskStakeholders() {
        if( jaxbTaskStakeholders == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(jaxbTaskStakeholders)); 
    }

    @Override
    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        this.jaxbTaskStakeholders = convertListFromInterfaceToJaxbImpl(taskStakeholders);
    }

    public List<JaxbOrganizationalEntity> getJaxbTaskStakeholders() {
        return jaxbTaskStakeholders;
    }

    public void setJaxbTaskStakeholders(List<JaxbOrganizationalEntity> jaxbTaskStakeholders) {
        this.jaxbTaskStakeholders = jaxbTaskStakeholders;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getRecipients() {
        if( jaxbRecipients == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(jaxbRecipients)); 
    }

    @Override
    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.jaxbRecipients = convertListFromInterfaceToJaxbImpl(recipients);
    }

    public List<JaxbOrganizationalEntity> getJaxbRecipients() {
        return jaxbRecipients;
    }

    public void setJaxbRecipients(List<JaxbOrganizationalEntity> jaxbRecipients) {
        this.jaxbRecipients = jaxbRecipients;
    }

    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(PeopleAssignments.class);
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(PeopleAssignments.class);
    }

}
