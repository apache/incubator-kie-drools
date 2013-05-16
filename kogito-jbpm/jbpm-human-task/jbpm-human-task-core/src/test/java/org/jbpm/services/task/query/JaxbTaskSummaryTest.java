package org.jbpm.services.task.query;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.SubTasksStrategy;

public class JaxbTaskSummaryTest {

    @Test
    public void canSerializeTaskSummary() throws Exception { 
        Marshaller marshaller = JAXBContext.newInstance(TaskSummaryImpl.class).createMarshaller();
        StringWriter stringWriter = new StringWriter();
        
        User user = new UserImpl("og");
        TaskSummary ts = new TaskSummaryImpl(
                1l, 2l, 
                "a", "b", "c", 
                Status.Completed, 
                3, false, 
                user, user, 
                new Date(), new Date(), new Date(), 
                "d", 
                5, 
                SubTasksStrategy.NoAction, 
                7l);
        marshaller.marshal(ts, stringWriter);
        System.out.println(stringWriter.toString());
    }
}
