package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;

@XmlRootElement(name="dummy-deadlines")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbDeadlines implements Deadlines {

    public JaxbDeadlines() { 
       // no-arg constructor for JAXB 
    }
    
    @Override
    public List<Deadline> getStartDeadlines() {
        return Collections.emptyList();
    }

    @Override
    public void setStartDeadlines(List<Deadline> startDeadlines) {
        // no-op
    }

    @Override
    public List<Deadline> getEndDeadlines() {
        return Collections.emptyList();
    }

    @Override
    public void setEndDeadlines(List<Deadline> endDeadlines) {
        // no-op
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(Deadlines.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(Deadlines.class);
    }

}
