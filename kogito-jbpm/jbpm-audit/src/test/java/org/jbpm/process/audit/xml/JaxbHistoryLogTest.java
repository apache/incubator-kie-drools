package org.jbpm.process.audit.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;

public class JaxbHistoryLogTest extends AbstractBaseTest {

    private static Random random = new Random();
    
    @Test
    public void roundTripXmlAndTestEqualsProcessInstanceLog() throws Exception {
        ProcessInstanceLog origLog = new ProcessInstanceLog(54, "org.hospital.patient.triage");
        origLog.setDuration(65l);
        origLog.setDuration(234l);
        origLog.setEnd(new Date((new Date()).getTime() + 1000));
        origLog.setExternalId("testDomainId");
        origLog.setIdentity("identityNotMemory");
        
        // nullable
        origLog.setStatus(2);
        origLog.setOutcome("descriptiveErrorCodeOfAnError");
        origLog.setParentProcessInstanceId(65l);
        
        origLog.setProcessName("org.process.not.technical");
        origLog.setProcessVersion("v3.14");
        
        JaxbProcessInstanceLog xmlLog = new JaxbProcessInstanceLog(origLog);
        String xmlStr = convertJaxbObjectToString(xmlLog);
        JaxbProcessInstanceLog newXmlLog = (JaxbProcessInstanceLog) convertStringToJaxbObject(xmlStr);
        compareOrig(xmlLog, newXmlLog, JaxbProcessInstanceLog.class);
        
        ProcessInstanceLog newLog = newXmlLog.createEntityInstance();
        compareOrig(origLog, newLog, ProcessInstanceLog.class);
    }
    
    @Test
    public void roundTripXmlAndTestEqualsProcessInstanceLogNillable() throws Exception {
        ProcessInstanceLog origLog = new ProcessInstanceLog(54, "org.hospital.patient.triage");
        origLog.setDuration(65l);
        origLog.setEnd(new Date((new Date()).getTime() + 1000));
        origLog.setExternalId("testDomainId");
        origLog.setIdentity("identityNotMemory");
        
        // nullable/nillable
        // origLog.setStatus(2);
        // origLog.setOutcome("descriptiveErrorCodeOfAnError");
        // origLog.setParentProcessInstanceId(65l);
        
        origLog.setProcessName("org.process.not.technical");
        origLog.setProcessVersion("v3.14");
        
        JaxbProcessInstanceLog xmlLog = new JaxbProcessInstanceLog(origLog);
        String xmlStr = convertJaxbObjectToString(xmlLog);
        JaxbProcessInstanceLog newXmlLog = (JaxbProcessInstanceLog) convertStringToJaxbObject(xmlStr);
        
        assertEquals( xmlLog.getProcessInstanceId(), newXmlLog.getProcessInstanceId() );
        assertEquals( xmlLog.getProcessId(), newXmlLog.getProcessId() );
        
        assertEquals( xmlLog.getDuration(), newXmlLog.getDuration() );
        assertEquals( xmlLog.getEnd(), newXmlLog.getEnd() );
        assertEquals( xmlLog.getExternalId(), newXmlLog.getExternalId() );
        assertEquals( xmlLog.getIdentity(), newXmlLog.getIdentity() );
        
        assertEquals( xmlLog.getStatus(), newXmlLog.getStatus() );
        assertEquals( xmlLog.getOutcome(), newXmlLog.getOutcome() );
        assertEquals( xmlLog.getParentProcessInstanceId(), newXmlLog.getParentProcessInstanceId() );
        
        assertEquals( xmlLog.getProcessName(), newXmlLog.getProcessName() );
        assertEquals( xmlLog.getProcessVersion(), newXmlLog.getProcessVersion() );
    }
    
    @Test
    public void roundTripXmlAndTestEqualsNodeInstanceLog() throws Exception {
        int type = 0;
        long processInstanceId = 23;
        String processId = "org.hospital.doctor.review";
        String nodeInstanceId = "1-1";
        String nodeId = "1";
        String nodeName = "notification";
        
        NodeInstanceLog origLog = new NodeInstanceLog(type, processInstanceId, processId, nodeInstanceId, nodeId, nodeName);
        
        origLog.setWorkItemId(78l);
        origLog.setConnection("link");
        origLog.setExternalId("not-internal-num");
        origLog.setNodeType("the-sort-of-point");
        
        JaxbNodeInstanceLog xmlLog = new JaxbNodeInstanceLog(origLog);
        String xmlStr = convertJaxbObjectToString(xmlLog);
        JaxbNodeInstanceLog newXmlLog = (JaxbNodeInstanceLog) convertStringToJaxbObject(xmlStr);
        compareOrig(xmlLog, newXmlLog, JaxbNodeInstanceLog.class);
        
        NodeInstanceLog newLog = newXmlLog.createEntityInstance();
        compareOrig(origLog, newLog, NodeInstanceLog.class);
    }
    
    @Test
    public void roundTripXmlAndTestEqualsVariableInstanceLog() throws Exception {
        long processInstanceId = 23;
        String processId = "org.hospital.intern.rounds";
        String variableInstanceId = "patientNum-1";
        String variableId = "patientNum";
        String value = "33";
        String oldValue = "32";
        
        VariableInstanceLog origLog 
            = new VariableInstanceLog(processInstanceId, processId, variableInstanceId, variableId, value, oldValue);
        
        origLog.setExternalId("outside-identity-representation");
        origLog.setOldValue("previous-data-that-this-variable-contains");
        origLog.setValue("the-new-data-that-has-been-put-in-this-variable");
        origLog.setVariableId("shortend-representation-of-this-representation");
        origLog.setVariableInstanceId("id-instance-variable");
       
        JaxbVariableInstanceLog xmlLog = new JaxbVariableInstanceLog(origLog);
        String xmlStr = convertJaxbObjectToString(xmlLog);
        JaxbVariableInstanceLog newXmlLog = (JaxbVariableInstanceLog) convertStringToJaxbObject(xmlStr);
        compareOrig(xmlLog, newXmlLog, JaxbVariableInstanceLog.class);
        
        VariableInstanceLog newLog = newXmlLog.createEntityInstance();
        compareOrig(origLog, newLog, VariableInstanceLog.class);
    }

    private void compareOrig(Object origObj, Object newObj, Class objClass) { 

        ComparePair compare = new ComparePair(origObj, newObj, objClass);
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


    private static Class[] jaxbClasses = { JaxbProcessInstanceLog.class, JaxbNodeInstanceLog.class, JaxbVariableInstanceLog.class };

    private static String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();

        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();

        return output;
    }

    private static Object convertStringToJaxbObject(String xmlStr) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(jaxbClasses).createUnmarshaller();
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlStr.getBytes());

        Object jaxbObj = unmarshaller.unmarshal(xmlStrInputStream);

        return jaxbObj;
    }
}
