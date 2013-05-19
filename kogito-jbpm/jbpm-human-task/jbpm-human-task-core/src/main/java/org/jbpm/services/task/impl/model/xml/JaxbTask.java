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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.I18NTextXmlAdapter;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;

@XmlRootElement(name="task")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbTask implements Task {

    @XmlElement
    @XmlSchemaType(name="long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name="long")
    private Integer priority;

    @XmlElement(name="task-type")
    @XmlSchemaType(name="int")
    private String taskType; 

    @XmlElement(name="name")
    @XmlJavaTypeAdapter(value=I18NTextXmlAdapter.class)
    private List<I18NText> names;
    
    @XmlElement(name="subject")
    @XmlJavaTypeAdapter(value=I18NTextXmlAdapter.class)
    private List<I18NText> subjects;
    
    @XmlElement(name="description")
    @XmlJavaTypeAdapter(value=I18NTextXmlAdapter.class)
    private List<I18NText> descriptions;
    
    @XmlElement
    private JaxbPeopleAssignments peopleAssignments;
    
    @XmlElement
    private JaxbTaskData taskData;
    
    public JaxbTask() { 
        // Default constructor
    }
    
    public JaxbTask(Task task) { 
        this.id = task.getId();
        this.priority = task.getPriority();
        this.peopleAssignments = new JaxbPeopleAssignments(task.getPeopleAssignments());
        this.names = task.getNames();
        this.subjects = task.getSubjects();
        this.descriptions = task.getDescriptions();
        this.taskData = new JaxbTaskData(task.getTaskData());
        this.taskType = task.getTaskType();
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public List<I18NText> getNames() {
        if( names == null ) { 
            names = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(names);
    }

    @Override
    public List<I18NText> getSubjects() {
        if( subjects == null ) { 
            subjects = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(subjects);
    }

    @Override
    public List<I18NText> getDescriptions() {
        if( descriptions == null ) { 
            descriptions = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(descriptions);
    }

    @Override
    public PeopleAssignments getPeopleAssignments() {
        return peopleAssignments;
    }

    @Override
    public TaskData getTaskData() {
        return taskData;
    }

    @Override
    public String getTaskType() {
        return taskType;
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException( methodName + " is not supported on the JAXB " + Task.class.getSimpleName() + " implementation.");
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException( methodName + " is not supported on the JAXB " + Task.class.getSimpleName() + " implementation.");
    }

}
