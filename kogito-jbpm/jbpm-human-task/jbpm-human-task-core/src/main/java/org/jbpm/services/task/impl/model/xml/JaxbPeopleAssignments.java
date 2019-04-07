/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.convertListFromInterfaceToJaxbImpl;
import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;
import static org.jbpm.services.task.impl.model.xml.JaxbOrganizationalEntity.convertListFromJaxbImplToInterface;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalPeopleAssignments;

import com.fasterxml.jackson.annotation.JsonAutoDetect;


@XmlType(name="people-assignments")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbPeopleAssignments implements InternalPeopleAssignments {

    @XmlElement(name="task-initiator-id")
    @XmlSchemaType(name="string")
    private String taskInitiatorId;

    @XmlElement(name="potential-owners")
    private List<JaxbOrganizationalEntity> potentialOwners;

    @XmlElement(name="business-administrators")
    private List<JaxbOrganizationalEntity> businessAdministrators;

    @XmlElement(name="excluded-owners")
    private List<JaxbOrganizationalEntity> excludedOwners;

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
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getBusinessAdministrators(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.excludedOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getExcludedOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getPotentialOwners(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.recipients = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getRecipients(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
        this.taskStakeholders = convertListFromInterfaceToJaxbImpl(((InternalPeopleAssignments) peopleAssignments).getTaskStakeholders(), OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }
    
    @Override
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
    public List<OrganizationalEntity> getPotentialOwners() {
        if( potentialOwners == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(potentialOwners));
    }

    public void setPotentialOwners(List<OrganizationalEntity> potentialOwners) {
        this.potentialOwners = convertListFromInterfaceToJaxbImpl(potentialOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        if( businessAdministrators == null ) { 
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(convertListFromJaxbImplToInterface(businessAdministrators));
    }

    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = convertListFromInterfaceToJaxbImpl(businessAdministrators, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.excludedOwners = convertListFromInterfaceToJaxbImpl(excludedOwners, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.taskStakeholders = convertListFromInterfaceToJaxbImpl(taskStakeholders, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
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
        this.recipients = convertListFromInterfaceToJaxbImpl(recipients, OrganizationalEntity.class, JaxbOrganizationalEntity.class);
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        unsupported(PeopleAssignments.class);
        
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        unsupported(PeopleAssignments.class);
    }

}
