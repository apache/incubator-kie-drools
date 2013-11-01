package org.jbpm.services.task.impl.model.xml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
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


@XmlRootElement(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPeopleAssignments implements PeopleAssignments {

    @XmlElement(name="task-initiator")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private User taskInitiator;

    @XmlElement(name="potential-owner")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> potentialOwners;

    @XmlElement(name="business-administrator")
    @XmlJavaTypeAdapter(value=OrganizationalEntityXmlAdapter.class)
    private List<OrganizationalEntity> businessAdministrators;

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

    @Override
    public List<OrganizationalEntity> getPotentialOwners() {
        if( potentialOwners == null ) { 
            potentialOwners = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(potentialOwners);
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( businessAdministrators == null ) { 
            businessAdministrators = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(businessAdministrators);
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
