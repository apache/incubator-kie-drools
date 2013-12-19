package org.jbpm.services.task.impl.model.xml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.OrganizationalEntityXmlAdapter;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;


@XmlRootElement(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPeopleAssignments implements InternalPeopleAssignments {

    @XmlElement(name="task-initiator")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private User taskInitiator;

    @XmlElement(name="potential-owner")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> potentialOwners;

    @XmlElement(name="business-administrator")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> businessAdministrators;

    @XmlElement(name="excluded-owners")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> excludedOwners;

    @XmlElement(name="task-stakeholders")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> taskStakeholders;

    @XmlElement(name="recipients")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> recipients;
    
    public JaxbPeopleAssignments() { 
       // Default constructor for JAXB
    }
    
    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) { 
        this.taskInitiator = peopleAssignments.getTaskInitiator();
        this.potentialOwners = peopleAssignments.getPotentialOwners();
        this.potentialOwners.toArray();
        this.businessAdministrators = peopleAssignments.getBusinessAdministrators();
        this.businessAdministrators.toArray();
    }
    
    @Override
    public User getTaskInitiator() {
        return taskInitiator;
    }

    public void setTaskInitiator(User taskInitiator) {
        this.taskInitiator = taskInitiator;
    }

    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        if( potentialOwners == null ) { 
            potentialOwners = Collections.emptyList();
        }
        return Collections.unmodifiableList(potentialOwners);
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( businessAdministrators == null ) { 
            businessAdministrators = Collections.emptyList();
        }
        return Collections.unmodifiableList(businessAdministrators);
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    @Override
    public List<OrganizationalEntity> getExcludedOwners() {
        if( excludedOwners == null ) { 
            excludedOwners = Collections.emptyList();
        }
        return Collections.unmodifiableList(excludedOwners); 
    }

    @Override
    public void setExcludedOwners(List<OrganizationalEntity> excludedOwners) {
        this.excludedOwners = excludedOwners;
    }

    @Override
    public List<OrganizationalEntity> getTaskStakeholders() {
        if( taskStakeholders == null ) { 
            taskStakeholders = Collections.emptyList();
        }
        return Collections.unmodifiableList(taskStakeholders); 
    }

    @Override
    public void setTaskStakeholders(List<OrganizationalEntity> taskStakeholders) {
        this.taskStakeholders = taskStakeholders;
    }

    @Override
    public List<OrganizationalEntity> getRecipients() {
        if( recipients == null ) { 
            recipients = Collections.emptyList();
        }
        return Collections.unmodifiableList(recipients); 
    }

    @Override
    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = recipients;
    }

    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + PeopleAssignments.class.getSimpleName()
                + " implementation.");
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + PeopleAssignments.class.getSimpleName()
                + " implementation.");
    }


}
