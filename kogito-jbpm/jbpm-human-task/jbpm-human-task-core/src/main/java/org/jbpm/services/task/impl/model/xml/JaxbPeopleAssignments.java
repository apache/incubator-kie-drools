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

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;


@XmlRootElement(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPeopleAssignments implements InternalPeopleAssignments {

    @XmlElement(name="task-initiator")
    @XmlSchemaType(name="string")
    private String taskInitiator;

    @XmlElement(name="potential-owner")
    private List<JaxbOrganizationalEntity> potentialOwners;

    @XmlElement(name="business-administrator")
    private List<JaxbOrganizationalEntity> businessAdministrators;

    @XmlElement(name="excluded-owners")
    private List<JaxbOrganizationalEntity> excludedOwners;

    @XmlElement(name="task-stakeholders")
    private List<JaxbOrganizationalEntity> taskStakeholders;

    @XmlElement(name="recipients")
    private List<JaxbOrganizationalEntity> recipients;
    
    public JaxbPeopleAssignments() { 
       // Default constructor for JAXB
    }
    
    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) { 
        User taskInitiatorUser = peopleAssignments.getTaskInitiator();
        if( taskInitiatorUser != null ) { 
            this.taskInitiator = taskInitiatorUser.getId();
        }
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getBusinessAdministrators());
        this.excludedOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getExcludedOwners());
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getPotentialOwners());
        this.recipients = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getRecipients());
        this.taskStakeholders = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getTaskStakeholders());
    }
    
    @Override
    public User getTaskInitiator() {
        if( this.taskInitiator != null ) { 
            return new GetterUser(this.taskInitiator);
        } 
        return null;
    }

    public void setTaskInitiator(User taskInitiatorUser) {
        if( taskInitiatorUser != null ) { 
            this.taskInitiator = taskInitiatorUser.getId();
        }
    }

    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        if( potentialOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(potentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(potentialOwners);
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( businessAdministrators == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(businessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(businessAdministrators);
    }

    @Override
    public List<OrganizationalEntity> getExcludedOwners() {
        if( excludedOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(excludedOwners)); 
    }

    @Override
    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.excludedOwners = convertListFromInterfaceToJaxbImpl(excludedOwners);
    }

    @Override
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

    @Override
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

    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(PeopleAssignments.class);
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(PeopleAssignments.class);
    }

}
