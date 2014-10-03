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

    @XmlElement(name="task-initiator-id")
    @XmlSchemaType(name="string")
    private String taskInitiatorId;

    @XmlElement(name="potential-owners")
    private List<JaxbOrganizationalEntity> potentialOwners;

    @XmlElement(name="business-administrators")
    private List<JaxbOrganizationalEntity> businessAdministrators;

    @XmlElement(name="excluded-owners")
    private List<JaxbOrganizationalEntity> excludedOwner;

    @XmlElement(name="task-stakeholders")
    private List<JaxbOrganizationalEntity> taskStakeholders;

    @XmlElement
    private List<JaxbOrganizationalEntity> recipients;
    
    public JaxbPeopleAssignments() { 
       // Default constructor for JAXB
    }
    
    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) { 
        User taskInitiatorUser = peopleAssignments.getTaskInitiator();
        if( taskInitiatorUser != null ) { 
            this.taskInitiatorId = taskInitiatorUser.getId();
        }
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getBusinessAdministrators());
        this.excludedOwner = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getExcludedOwners());
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getPotentialOwners());
        this.recipients = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getRecipients());
        this.taskStakeholders = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getTaskStakeholders());
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
        if( potentialOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(potentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(potentialOwners);
    }

    public List<JaxbOrganizationalEntity> getJaxbPotentialOwners() {
        return potentialOwners;
    }

    public void setJaxbPotentialOwners(List<JaxbOrganizationalEntity> jaxbPotentialOwners) {
        this.potentialOwners = jaxbPotentialOwners;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( businessAdministrators == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(businessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(businessAdministrators);
    }

    public List<JaxbOrganizationalEntity> getJaxbBusinessAdministrators() {
        return businessAdministrators;
    }

    public void setJaxbBusinessAdministrators(List<JaxbOrganizationalEntity> jaxbBusinessAdministrators) {
        this.businessAdministrators = jaxbBusinessAdministrators;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getExcludedOwners() {
        if( excludedOwner == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(excludedOwner)); 
    }

    @Override
    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.excludedOwner = convertListFromInterfaceToJaxbImpl(excludedOwners);
    }

    public List<JaxbOrganizationalEntity> getJaxbExcludedOwners() {
        return excludedOwner;
    }

    public void setJaxbExcludedOwners(List<JaxbOrganizationalEntity> jaxbExcludedOwners) {
        this.excludedOwner = jaxbExcludedOwners;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getTaskStakeholders() {
        if( taskStakeholders == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(taskStakeholders)); 
    }

    @Override
    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        this.taskStakeholders = convertListFromInterfaceToJaxbImpl(taskStakeholders);
    }

    public List<JaxbOrganizationalEntity> getJaxbTaskStakeholders() {
        return taskStakeholders;
    }

    public void setJaxbTaskStakeholders(List<JaxbOrganizationalEntity> jaxbTaskStakeholders) {
        this.taskStakeholders = jaxbTaskStakeholders;
    }

    @Override
    @JsonIgnore
    public List<OrganizationalEntity> getRecipients() {
        if( recipients == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(recipients)); 
    }

    @Override
    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = convertListFromInterfaceToJaxbImpl(recipients);
    }

    public List<JaxbOrganizationalEntity> getJaxbRecipients() {
        return recipients;
    }

    public void setJaxbRecipients(List<JaxbOrganizationalEntity> jaxbRecipients) {
        this.recipients = jaxbRecipients;
    }

    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(PeopleAssignments.class);
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(PeopleAssignments.class);
    }

}
