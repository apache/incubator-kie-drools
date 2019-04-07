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

package org.jbpm.services.task.commands;

import org.jbpm.services.task.impl.model.xml.JaxbFaultData;
import org.jbpm.services.task.impl.model.xml.JaxbI18NText;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.SubTasksStrategy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Date;
import java.util.List;

@XmlRootElement(name="set-task-property-command")
@XmlAccessorType(XmlAccessType.NONE)
public class SetTaskPropertyCommand extends UserGroupCallbackTaskCommand<Void> {

    public static final int FAULT_PROPERTY = 1;
    public static final int OUTPUT_PROPERTY = 2;
    public static final int PRIORITY_PROPERTY = 3;
    public static final int TASK_NAMES_PROPERTY = 4;
    public static final int EXPIRATION_DATE_PROPERTY = 5;
    public static final int DESCRIPTION_PROPERTY = 6;
    public static final int SKIPPABLE_PROPERTY = 7;
    public static final int SUB_TASK_STRATEGY_PROPERTY = 8;
    
	private static final long serialVersionUID = -836520791223188840L;

	@XmlElement(required=true)
	@XmlSchemaType(name="integer")
	private Integer property;
	
	@XmlElement
	private JaxbFaultData faultData;

	@XmlElement
	private Object output;
	
	@XmlElement
	@XmlSchemaType(name="int")
	private Integer priority;
	
	@XmlElement(name="names-or-descriptions")
	private List<JaxbI18NText> namesOrDescriptions;
	
	@XmlElement(name="expiration-date")
	@XmlSchemaType(name="dateTime")
	private Date expirationDate;

	@XmlElement
    @XmlSchemaType(name="boolean")
	private Boolean skippable;

	@XmlElement(name="sub-tasks-strategy")
	private SubTasksStrategy subTasksStrategy;

	public SetTaskPropertyCommand() {
	}
	
	public SetTaskPropertyCommand(long taskId, String userId, Integer property, Object value) {
		this.taskId = taskId;
		this.userId = userId;
		this.property = property;
		
		JaxbFaultData newValue = null;
		List<JaxbI18NText> newListValue = null;
        switch (property) {
        case FAULT_PROPERTY:
            if( value != null ) { 
                checkValueType(value, FaultData.class, property, true, false);
                newValue = new JaxbFaultData((FaultData) value);
            }
            this.faultData = newValue;
            break;
        case OUTPUT_PROPERTY:
            this.output = value;
            break;
        case PRIORITY_PROPERTY:
            checkValueType(value, Integer.class, property, false, false);
            this.priority = (Integer) value;
            break;
        case TASK_NAMES_PROPERTY:
            if( value != null ) { 
                checkValueType(value, I18NText.class, property, true, true);
                newListValue = JaxbI18NText.convertListFromInterfaceToJaxbImpl(((List<I18NText>) value), I18NText.class, JaxbI18NText.class);
            }
            this.namesOrDescriptions = newListValue;
            break;
        case EXPIRATION_DATE_PROPERTY:
            checkValueType(value, Date.class, property, false, false);
            this.expirationDate = (Date) value;
            break;
        case DESCRIPTION_PROPERTY:
            if( value != null ) { 
                checkValueType(value, I18NText.class, property, true, true);
                newListValue = JaxbI18NText.convertListFromInterfaceToJaxbImpl(((List<I18NText>) value), I18NText.class, JaxbI18NText.class);
            }
            this.namesOrDescriptions = newListValue;
            break;
        case SKIPPABLE_PROPERTY:
            checkValueType(value, Boolean.class, property, false, false);
            this.skippable = (Boolean) value;
            break;
        case SUB_TASK_STRATEGY_PROPERTY:
            checkValueType(value, SubTasksStrategy.class, property, false, false);
            this.subTasksStrategy = (SubTasksStrategy) value;
            break;
        default:
            throw new IllegalStateException("Unknown property in " + this.getClass().getSimpleName() + " constructor: " + property);
        }
    }

	private void checkValueType(Object value, Class expectedClass, int property, boolean assignable, boolean list) { 
	    if( value == null ) {
	        return;
	    }
	    String propType = null;
	    switch(property) { 
        case FAULT_PROPERTY:
            propType = "FAULT_PROPERTY (" + property + ")";
            break;
        case OUTPUT_PROPERTY:
            propType = "OUTPUT_PROPERTY (" + property + ")";
            break;
        case PRIORITY_PROPERTY:
            propType = "PRIORITY_PROPERTY (" + property + ")";
            break;
        case TASK_NAMES_PROPERTY:
            propType = "TASK_NAMES_PROPERTY (" + property + ")";
            break;
        case EXPIRATION_DATE_PROPERTY:
            propType = "EXPIRATION_DATE_PROPERTY (" + property + ")";
            break;
        case DESCRIPTION_PROPERTY:
            propType = "DESCRIPTION_PROPERTY (" + property + ")";
            break;
        case SKIPPABLE_PROPERTY:
            propType = "SKIPPABLE_PROPERTY (" + property + ")";
            break;
        case SUB_TASK_STRATEGY_PROPERTY:
            propType = "SUB_TASK_STRATEGY_PROPERTY (" + property + ")";
            break;
        default:
            throw new IllegalStateException("Unknown property in " + this.getClass().getSimpleName() + " constructor check: " + property);
	    }
	    Class valueClass = value.getClass();
	    if( list ) { 
	       if( ! List.class.isAssignableFrom(valueClass) ) { 
	            throw new IllegalStateException("Expected a " + expectedClass.getSimpleName() + " for property " + propType + ", not a " + valueClass.getName() );
	       }
	       List listVal = (List) value;
	       if( listVal.isEmpty() ) { 
	           return;
	       }
	       value = listVal.get(0);
	       valueClass = value.getClass();
	    }
	    
	    if( assignable ) { 
	        if( ! expectedClass.isAssignableFrom(valueClass) ) { 
	            throw new IllegalStateException("Expected a " + expectedClass.getSimpleName() + " for property " + propType + ", not a " + valueClass.getName() );
	        } 
	    } else { 
	       if( ! expectedClass.isInstance(value) )  {
	            throw new IllegalStateException("Expected a " + expectedClass.getSimpleName() + " for property " + propType + ", not a " + valueClass.getName() );
	       }
	    }
	}
	
    public Integer getProperty() {
		return property;
	}

	public void setProperty(Integer name) {
		this.property = name;
	}

	public JaxbFaultData getFaultData() {
        return faultData;
    }

    public void setFaultData( JaxbFaultData faultData ) {
        this.faultData = faultData;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput( Object output ) {
        this.output = output;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority( Integer priority ) {
        this.priority = priority;
    }

    public List<JaxbI18NText> getNamesOrDescriptions() {
        return namesOrDescriptions;
    }

    public void setNamesOrDescriptions( List<JaxbI18NText> namesOrDescriptions ) {
        this.namesOrDescriptions = namesOrDescriptions;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate( Date expirationDate ) {
        this.expirationDate = expirationDate;
    }

    public Boolean getSkippable() {
        return skippable;
    }

    public void setSkippable( Boolean skippable ) {
        this.skippable = skippable;
    }

    public SubTasksStrategy getSubTasksStrategy() {
        return subTasksStrategy;
    }

    public void setSubTasksStrategy( SubTasksStrategy subTasksStrategy ) {
        this.subTasksStrategy = subTasksStrategy;
    }

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        TaskInstanceService service = context.getTaskInstanceService();
        
        switch (property) {
		case FAULT_PROPERTY:
			doCallbackUserOperation(userId, context, true);
			service.setFault(taskId, userId, faultData);
			break;
		case OUTPUT_PROPERTY:
			doCallbackUserOperation(userId, context, true);
			service.setOutput(taskId, userId, output);
			break;
		case PRIORITY_PROPERTY:
			service.setPriority(taskId, priority );
			break;
		case TASK_NAMES_PROPERTY:
		    List<I18NText> names = null;
		    if( namesOrDescriptions != null ) { 
		        names = JaxbI18NText.convertListFromJaxbImplToInterface(namesOrDescriptions);
		    }
			service.setTaskNames(taskId, names);
			break;
		case EXPIRATION_DATE_PROPERTY:
			service.setExpirationDate(taskId, expirationDate);
			break;
		case DESCRIPTION_PROPERTY:
		    List<I18NText> descriptions = null;
		    if( namesOrDescriptions != null ) { 
		        descriptions = JaxbI18NText.convertListFromJaxbImplToInterface(namesOrDescriptions);
		    }
			service.setDescriptions(taskId, descriptions);
			break;
		case SKIPPABLE_PROPERTY:
			service.setSkipable(taskId, skippable);
			break;
		case SUB_TASK_STRATEGY_PROPERTY:
			service.setSubTaskStrategy(taskId, subTasksStrategy);
			break;
		default:
		    throw new IllegalStateException("Unknown property in " + this.getClass().getSimpleName() + " execute: " + property);
		}
        return null;
    }

}
