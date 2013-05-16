package org.jbpm.services.task.query;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.core.command.assertion.AssertEquals;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.model.InternalTaskSummary;
import org.kie.internal.task.api.model.SubTasksStrategy;

public class JaxbTaskSummaryTest extends Assert {

    @Test
    public void canSerializeRoundTripTaskSummary() throws Exception { 
        Marshaller marshaller = JAXBContext.newInstance(TaskSummaryImpl.class).createMarshaller();
        StringWriter stringWriter = new StringWriter();
        
        User user = new UserImpl("og");
        TaskSummary taskSummary = new TaskSummaryImpl(
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
        marshaller.marshal(taskSummary, stringWriter);
        
        Unmarshaller unmarshaller = JAXBContext.newInstance(TaskSummaryImpl.class).createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
        TaskSummaryImpl roundTripTaskSummary = (TaskSummaryImpl) unmarshaller.unmarshal(inputStream);
        
        for(Method getMethod : InternalTaskSummary.class.getMethods()) { 
            String getMethodName = getMethod.getName();
            if( getMethodName.startsWith("get") ) {
                Object orig = getMethod.invoke(taskSummary, new Object[0]);
                Object roundTrip = getMethod.invoke(roundTripTaskSummary, new Object[0]);
                assertEquals(getMethodName, orig, roundTrip); 
            }
        }
    }
}
