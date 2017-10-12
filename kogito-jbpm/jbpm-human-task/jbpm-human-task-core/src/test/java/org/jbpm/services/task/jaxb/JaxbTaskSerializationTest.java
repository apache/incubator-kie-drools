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

package org.jbpm.services.task.jaxb;

import static org.jbpm.services.task.commands.SetTaskPropertyCommand.DESCRIPTION_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.EXPIRATION_DATE_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.FAULT_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.OUTPUT_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.PRIORITY_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.SKIPPABLE_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.SUB_TASK_STRATEGY_PROPERTY;
import static org.jbpm.services.task.commands.SetTaskPropertyCommand.TASK_NAMES_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamSource;

import org.jbpm.services.task.admin.listener.internal.GetCurrentTxTasksCommand;
import org.jbpm.services.task.commands.AddContentFromUserCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.SetTaskPropertyCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.impl.model.FaultDataImpl;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import org.jbpm.services.task.jaxb.AbstractTaskSerializationTest.TestType;
import org.junit.Assume;
import org.junit.Test;
import org.kie.api.task.model.I18NText;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

public class JaxbTaskSerializationTest extends AbstractTaskSerializationTest {

    private Class<?>[] jaxbClasses = { JaxbTask.class, JaxbContent.class };

    public TestType getType() {
        return TestType.JAXB;
    }

    protected static Reflections reflections =
            new Reflections(ClasspathHelper.forPackage("org.jbpm.services.task"),
                            ClasspathHelper.forPackage("org.jbpm.services.task.commands"),
                            new TypeAnnotationsScanner(), new FieldAnnotationsScanner(), new MethodAnnotationsScanner(), new SubTypesScanner());

    @Override
    public <T> T testRoundTrip(T input) throws Exception {
        String xmlStr = convertJaxbObjectToString(input);
        logger.debug(xmlStr);
        if( input instanceof JAXBElement ) {
            return (T) convertStringToJaxbElement(xmlStr, ((JAXBElement) input).getValue().getClass());
        }
        return (T) convertStringToJaxbObject(xmlStr);
    }

    @Test
    public void taskCmdUniqueRootElementTest() throws Exception {
        Set<String> uniqueRootElemSet = new HashSet<String>();
        for (Class<?> jaxbClass : reflections.getTypesAnnotatedWith(XmlRootElement.class) ) {
            XmlRootElement xmlRootElemAnno = jaxbClass.getAnnotation(XmlRootElement.class);
            assertTrue( xmlRootElemAnno.name() + " is not a unique @XmlRootElement value!",  uniqueRootElemSet.add(xmlRootElemAnno.name()));
        }
    }

    @Test
    public void taskCommandSubTypesCanBeSerialized() throws Exception {
        for (Class<?> jaxbClass : reflections.getSubTypesOf(TaskCommand.class)) {
            if (jaxbClass.equals(UserGroupCallbackTaskCommand.class)
            		|| jaxbClass.equals(GetCurrentTxTasksCommand.class)) {
                continue;
            }
            addClassesToSerializationContext(jaxbClass);
            Constructor<?> construct = jaxbClass.getConstructor(new Class[] {});
            try {
                Object jaxbInst = construct.newInstance(new Object[] {});
                testRoundTrip(jaxbInst);
            } catch (Exception e) {
                logger.warn("Testing failed for" + jaxbClass.getName());
                throw e;
            }
        }
    }

    @Test
    public void compositeCommandXmlElementsAnnoTest() throws Exception {
       Field [] comCmdFields = CompositeCommand.class.getDeclaredFields();
       for( Field field : comCmdFields ) {
           XmlElements xmlElemsAnno = field.getAnnotation(XmlElements.class);
           if( xmlElemsAnno != null ) {
               Set<Class<? extends TaskCommand>> taskCmdSubTypes = reflections.getSubTypesOf(TaskCommand.class);
               Set<Class<? extends UserGroupCallbackTaskCommand>> userGrpTaskCmdSubTypes = reflections.getSubTypesOf(UserGroupCallbackTaskCommand.class);
               taskCmdSubTypes.addAll(userGrpTaskCmdSubTypes);

               Class [] exclTaskCmds = {
                      UserGroupCallbackTaskCommand.class,
                      CompositeCommand.class,
                      GetCurrentTxTasksCommand.class
               };
               taskCmdSubTypes.removeAll(Arrays.asList(exclTaskCmds));
               for( XmlElement xmlElemAnno : xmlElemsAnno.value() ) {
                  Class xmlElemAnnoType = xmlElemAnno.type();
                  assertTrue( xmlElemAnnoType.getName() + " does not extend the " + TaskCommand.class.getSimpleName() + " class!",
                          taskCmdSubTypes.contains(xmlElemAnnoType));
               }
               for( XmlElement xmlElemAnno : xmlElemsAnno.value() ) {
                  Class xmlElemAnnoType = xmlElemAnno.type();
                  taskCmdSubTypes.remove(xmlElemAnnoType);
               }
               if( ! taskCmdSubTypes.isEmpty() ) {
            	   System.out.println("##### " + taskCmdSubTypes.iterator().next().getCanonicalName());
                  fail( "(" + taskCmdSubTypes.iterator().next().getSimpleName() + ") Not all " + TaskCommand.class.getSimpleName() + " sub types have been added to the @XmlElements in the CompositeCommand." + field.getName() + " field.");
               }
           } else {
               assertFalse( "TaskCommand fields need to be annotated with @XmlElements annotations!", TaskCommand.class.equals(field.getType()) );
               if( field.getType().isArray() ) {
                   Class arrElemType = field.getType().getComponentType();
                   if( arrElemType != null ) {
                       assertFalse( "TaskCommand fields (CompositeCommand." + field.getName() + ") need to be annotated with @XmlElements annotations!", TaskCommand.class.equals(arrElemType) );
                   }
               } else if( Collection.class.isAssignableFrom(field.getType()) ) {
                   ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
                   Type listType = fieldGenericType.getActualTypeArguments()[0];
                   if( listType != null ) {
                       assertFalse( "TaskCommand fields (CompositeCommand." + field.getName() + ") need to be annotated with @XmlElements annotations!", TaskCommand.class.equals(listType) );
                   }

               }
           }

       }
    }

    public String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();

        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();

        return output;
    }

    public Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());

        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);

        return jaxbObj;
    }

    public Object convertStringToJaxbElement(String xmlStr, Class actualClass) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());

        Object jaxbObj = unmarshaller.unmarshal(new StreamSource(xmlStrInputStream), actualClass);
        return jaxbObj;
    }

    @Override
    public void addClassesToSerializationContext(Class<?>... extraClass) {
        List<Class<?>> newJaxbClasses = new ArrayList<Class<?>>();
        newJaxbClasses.addAll(Arrays.asList(jaxbClasses));
        newJaxbClasses.addAll(Arrays.asList(extraClass));

        jaxbClasses = newJaxbClasses.toArray(new Class[newJaxbClasses.size()]);
    }

    @Test
    public void uniqueRootElementTest() throws Exception {
        Set<String> idSet = new HashSet<String>();
        HashMap<String, Class> idClassMap = new HashMap<String, Class>();
        for (Class<?> jaxbClass : reflections.getTypesAnnotatedWith(XmlRootElement.class)) {
            XmlRootElement rootElemAnno = jaxbClass.getAnnotation(XmlRootElement.class);
            String id = rootElemAnno.name();
            if ("##default".equals(id)) {
                continue;
            }
            String otherClass = (idClassMap.get(id) == null ? "null" : idClassMap.get(id).getName());
            assertTrue("ID '" + id + "' used in both " + jaxbClass.getName() + " and " + otherClass, idSet.add(id));
            idClassMap.put(id, jaxbClass);

            String className = jaxbClass.getSimpleName();
            if( ! className.endsWith("Command") ) {
               continue;
            }
            String idName = id.replace("-", "");
            assertEquals( "XML root element name should match class name in " + jaxbClass.getName() + "!", className.toLowerCase(), idName.toLowerCase());
        }
    }

    @Test
    public void setTaskPropertyCommandTest() throws Exception {
       SetTaskPropertyCommand cmd;

       int taskId = 1;
       String userId = "user";

       FaultDataImpl faultData = new FaultDataImpl();
       faultData.setAccessType(AccessType.Inline);
       faultData.setContent("skinned shins".getBytes());
       faultData.setFaultName("Whoops!");
       faultData.setType("skates");

       List<I18NText> textList = new ArrayList<I18NText>();
       I18NText text = new I18NTextImpl("nl-NL", "Stroopwafel!");
       textList.add(text);

       Object [][] testData = {
               { FAULT_PROPERTY, faultData },
               { OUTPUT_PROPERTY, new Object() },
               { PRIORITY_PROPERTY, 23 },
               { TASK_NAMES_PROPERTY, textList },
               { EXPIRATION_DATE_PROPERTY, new Date() },
               { DESCRIPTION_PROPERTY, new ArrayList<I18NText>() },
               { SKIPPABLE_PROPERTY, false },
               { SUB_TASK_STRATEGY_PROPERTY, SubTasksStrategy.EndParentOnAllSubTasksEnd }
       };

       TaskContext mockContext = mock(TaskContext.class);
       TaskInstanceService mockTaskService = mock(TaskInstanceService.class);
       UserGroupCallback mockUserGroupCallback = mock(UserGroupCallback.class);
       when(mockContext.getTaskInstanceService()).thenReturn(mockTaskService);
       when(mockContext.getUserGroupCallback()).thenReturn(mockUserGroupCallback);
       when(mockUserGroupCallback.existsUser(anyString())).thenReturn(false);

       for( Object [] data : testData ) {
           int property = (Integer) data[0];
           cmd = new SetTaskPropertyCommand(taskId, userId, property, data[1]);
           try {
               cmd.execute(mockContext);
           } catch (IllegalArgumentException e) {
               // expected for fault and output
               assertTrue(e.getMessage().startsWith("User user was not found in callback "));
           }
       }
    }

    @Test
    public void contentCommandTest() throws Exception {
        addClassesToSerializationContext(AddContentFromUserCommand.class);

       AddContentFromUserCommand cmd = new AddContentFromUserCommand(23l, "user");
       cmd.getOutputContentMap().put("one", "two");
       cmd.getOutputContentMap().put("thr", new Integer(4));

       AddContentFromUserCommand copyCmd = testRoundTrip(cmd);
       assertEquals( "task id", cmd.getTaskId(), copyCmd.getTaskId());
       assertEquals( "user id", cmd.getUserId(), copyCmd.getUserId());
       for( Entry<String, Object> entry : cmd.getOutputContentMap().entrySet() ) {
           String key = entry.getKey();
           Object copyVal = copyCmd.getOutputContentMap().get(key);
           assertEquals( "entry: " + key, entry.getValue(), copyVal );
       }
    }
}
