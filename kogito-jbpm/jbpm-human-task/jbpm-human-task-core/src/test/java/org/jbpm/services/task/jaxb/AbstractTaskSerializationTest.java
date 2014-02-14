package org.jbpm.services.task.jaxb;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.jbpm.services.task.MvelFilePath;
import org.jbpm.services.task.commands.CancelDeadlineCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.ProcessSubTaskCommand;
import org.jbpm.services.task.commands.SkipTaskCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import org.junit.Assume;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskSerializationTest {

    protected final Logger logger;
    
    public AbstractTaskSerializationTest() { 
         logger = LoggerFactory.getLogger(this.getClass());
    }
    
    public abstract Object testRoundTrip(Object input) throws Exception;
    public abstract TestType getType();
    public abstract void addClassesToSerializationContext(Class<?>... extraClass);
    
    public enum TestType { 
        JAXB, JSON, YAML;
    }
    
    protected Reflections reflections = new Reflections(ClasspathHelper.forPackage("org.jbpm.services.task"),
            new TypeAnnotationsScanner(), new FieldAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());

    // TESTS ----------------------------------------------------------------------------------------------------------------------
    
    @Test
    public void jaxbTaskTest() throws Exception {
        // Yaml serialization requires major changes in order to be supported.. :/ 
        Assume.assumeTrue(! getType().equals(TestType.YAML));
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.FullTask));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        ((InternalTask)task).setFormName("Bruno's Form");
        InternalTaskData taskData = (InternalTaskData) task.getTaskData();

        String payload = "brainwashArmitageRecruitCaseGetPasswordFromLady3JaneAscentToStraylightIcebreakerUniteWithNeuromancer";

        InternalComment comment = (InternalComment) TaskModelProvider.getFactory().newComment();
        comment.setId(42);
        comment.setText(payload);
        comment.setAddedAt(new Date());
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Case");;
        comment.setAddedBy(user);
        taskData.addComment(comment);

        InternalAttachment attach = (InternalAttachment) TaskModelProvider.getFactory().newAttachment();
        attach.setId(1);
        attach.setName("virus");
        attach.setContentType("ROM");
        attach.setAttachedAt(new Date());
        user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Wintermute");;
        attach.setAttachedBy(user);
        attach.setSize(payload.getBytes().length);
        attach.setAttachmentContentId(comment.getId());
        taskData.addAttachment(attach);

        JaxbTask xmlTask = new JaxbTask(task);
        assertNotNull(xmlTask.getNames());
        assertTrue(xmlTask.getNames().size() > 0);
        JaxbTask bornAgainTask = (JaxbTask) testRoundTrip(xmlTask);
        assertNotNull(bornAgainTask.getNames());
        assertTrue("Round-tripped task has empty 'names' list!", !bornAgainTask.getNames().isEmpty());

        ComparePair compare = new ComparePair(task, bornAgainTask, Task.class);
        Queue<ComparePair> compares = new LinkedList<ComparePair>();
        compares.add(compare);
        while (!compares.isEmpty()) {
            compares.addAll(compares.poll().compare());
        }

        assertNotNull( ((InternalTask) xmlTask).getFormName() );
        assertEquals( ((InternalTask) xmlTask).getFormName(), ((InternalTask) bornAgainTask).getFormName() );
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
                        assertEquals(fieldName + " list size:", origList.size(), copyList.size());
                        for (int i = 0; i < origList.size() && !origList.isEmpty(); ++i) {
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
    
    @Test
    public void taskCommandSubTypesCanBeSerialized() throws Exception { 
        // Yaml serialization not required for commands
        Assume.assumeTrue(! getType().equals(TestType.YAML));
        
        for (Class<?> jaxbClass : reflections.getSubTypesOf(TaskCommand.class) ) { 
        	if (jaxbClass.equals(UserGroupCallbackTaskCommand.class) ) {
        		continue;
        	}
            addClassesToSerializationContext(jaxbClass);
            Constructor<?> construct = jaxbClass.getConstructor(new Class [] {});
            Object jaxbInst = construct.newInstance(new Object [] {});
            try { 
                testRoundTrip(jaxbInst);
            } catch( Exception e) { 
                logger.warn( "Testing failed for" + jaxbClass.getName());
                throw e;
            }
        } 
    }
    
    @Test
    public void taskCompositeCommandCanBeSerialized() throws Exception { 
    	addClassesToSerializationContext(CompositeCommand.class);
    	addClassesToSerializationContext(StartTaskCommand.class);
    	addClassesToSerializationContext(CancelDeadlineCommand.class);
    	CompositeCommand<Void> cmd = new CompositeCommand<Void>(
				new StartTaskCommand(1, "john"),
				new CancelDeadlineCommand(1, true, false));
    	
    	CompositeCommand<?> returned = (CompositeCommand<?>) testRoundTrip(cmd);
    	assertNotNull(returned);
    	assertNotNull(returned.getMainCommand());
    	assertTrue(returned.getMainCommand() instanceof StartTaskCommand);
    	assertEquals(Long.valueOf(1), returned.getTaskId());
    	assertNotNull(returned.getCommands());
    	assertEquals(1, returned.getCommands().length);
 
    }
    
    @Test
    public void taskCompositeCommandMultipleCanBeSerialized() throws Exception { 
    	addClassesToSerializationContext(CompositeCommand.class);
    	addClassesToSerializationContext(SkipTaskCommand.class);
    	addClassesToSerializationContext(ProcessSubTaskCommand.class);
    	addClassesToSerializationContext(CancelDeadlineCommand.class);
    	CompositeCommand<Void> cmd = new CompositeCommand<Void>(
    			new SkipTaskCommand(1, "john"),
				new ProcessSubTaskCommand(1, "john"),
				new CancelDeadlineCommand(1, true, true));
    	
    	CompositeCommand<?> returned = (CompositeCommand<?>) testRoundTrip(cmd);
    	assertNotNull(returned);
    	assertNotNull(returned.getMainCommand());
    	assertTrue(returned.getMainCommand() instanceof SkipTaskCommand);
    	assertEquals(Long.valueOf(1), returned.getTaskId());
    	assertNotNull(returned.getCommands());
    	assertEquals(2, returned.getCommands().length);
 
    }
}
