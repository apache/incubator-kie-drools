package org.jbpm.services.task;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.AttachmentImpl;
import org.jbpm.services.task.impl.model.CommentImpl;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Task;

public class JaxbTaskTest extends Assert {

    @Test
    public void roundTripXmlAndTestEquals() throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.FullTask));
        TaskImpl task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        TaskDataImpl taskData = (TaskDataImpl) task.getTaskData();

        String payload = "brainwashArmitageRecruitCaseGetPasswordFromLady3JaneAscentToStraylightIcebreakerUniteWithNeuromancer";

        CommentImpl comment = new CommentImpl();
        comment.setId(42);
        comment.setText(payload);
        comment.setAddedAt(new Date());
        comment.setAddedBy(new UserImpl("Case"));
        taskData.addComment(comment);

        AttachmentImpl attach = new AttachmentImpl();
        attach.setId(1);
        attach.setName("virus");
        attach.setContentType("ROM");
        attach.setAttachedAt(new Date());
        attach.setAttachedBy(new UserImpl("Wintermute"));
        attach.setSize(payload.getBytes().length);
        attach.setAttachmentContentId(comment.getId());
        taskData.addAttachment(attach);

        JaxbTask xmlTask = new JaxbTask(task);
        String xmlStr = convertJaxbObjectToString(xmlTask);
        JaxbTask bornAgainTask = (JaxbTask) convertStringToJaxbObject(xmlStr);

        ComparePair compare = new ComparePair(task, bornAgainTask, Task.class);
        Queue<ComparePair> compares = new LinkedList<ComparePair>();
        compares.add(compare);
        while (!compares.isEmpty()) {
            compares.addAll(compares.poll().compare());
        }
    }

    private static class ComparePair {
        private Object orig;
        private Object copy;
        private Class<?> objInterface;

        public ComparePair(Object a, Object b, Class<?> c) {
            this.orig = a;
            this.copy = b;
            this.objInterface = c;
        }

        public List<ComparePair> compare() {
            return compareObjects(orig, copy, objInterface);
        }

        private List<ComparePair> compareObjects(Object orig, Object copy, Class<?> objInterface) {
            List<ComparePair> cantCompare = new ArrayList<ComparePair>();
            for (Method getIsMethod : objInterface.getDeclaredMethods()) {
                String methodName = getIsMethod.getName();
                String fieldName;
                if (methodName.startsWith("get")) {
                    fieldName = methodName.substring(3);
                } else if (methodName.startsWith("is")) {
                    fieldName = methodName.substring(2);
                } else {
                    continue;
                }
                // getField -> field (lowercase f)
                fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                try {
                    Object origField = getIsMethod.invoke(orig, new Object[0]);
                    Object copyField = getIsMethod.invoke(copy, new Object[0]);
                    if (origField == null) {
                        fail("Please fill in the " + fieldName + " field in the " + objInterface.getSimpleName() + "!");
                    }
                    if( !(origField instanceof Enum) && origField.getClass().getPackage().getName().startsWith("org.")) {
                        cantCompare.add(new ComparePair(origField, copyField, getInterface(origField)));
                        continue;
                    } else if (origField instanceof List<?>) {
                        List<?> origList = (List) origField;
                        List<?> copyList = (List) copyField;
                        for (int i = 0; i < origList.size(); ++i) {
                            Class<?> newInterface = origField.getClass();
                            while (newInterface.getInterfaces().length > 0) {
                                newInterface = newInterface.getInterfaces()[0];
                            }
                            cantCompare.add(new ComparePair(origList.get(i), copyList.get(i), getInterface(origList.get(i))));
                        }
                        continue;
                    }
                    assertEquals(fieldName, origField, copyField);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to compare " + fieldName, e);
                }
            }
            return cantCompare;
        }

        private Class<?> getInterface(Object obj) {
            Class<?> newInterface = obj.getClass();
            Class<?> parent = newInterface;
            while (parent != null) {
                parent = null;
                if (newInterface.getInterfaces().length > 0) {
                    Class<?> newParent = newInterface.getInterfaces()[0];
                    if (newParent.getPackage().getName().startsWith("org.")) {
                        parent = newInterface = newParent;
                    }
                }
            }
            return newInterface;
        }
    }

    private static Random random = new Random();

    private I18NText createText(String string) {
        I18NTextImpl text = new I18NTextImpl("en-UK", string);
        text.setId(random.nextLong());
        return text;
    }

    private static Class[] jaxbClasses = { JaxbTask.class };

    public static String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();

        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();

        return output;
    }

    public static Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());

        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);

        return jaxbObj;
    }
}
