package org.jbpm.services.task.impl.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.OrganizationalEntityXmlAdapter;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;


@XmlRootElement(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPeopleAssignments extends AbstractJaxbTaskObject<PeopleAssignments> implements PeopleAssignments {

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
       super(PeopleAssignments.class);
    }
    
    public JaxbPeopleAssignments(PeopleAssignments peopleAssignments) { 
        super(peopleAssignments, PeopleAssignments.class);
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

}
