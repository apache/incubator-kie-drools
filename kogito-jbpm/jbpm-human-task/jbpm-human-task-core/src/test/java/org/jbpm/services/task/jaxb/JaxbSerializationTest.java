package org.jbpm.services.task.jaxb;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.impl.model.xml.JaxbTask;
import org.junit.Test;

public class JaxbSerializationTest extends AbstractSerializationTest {

    private Class<?>[] jaxbClasses = { JaxbTask.class };
    
    public TestType getType() {
        return TestType.YAML;
    }
    
    @Override
    public Object testRoundTrip(Object input) throws Exception {
        String xmlStr = convertJaxbObjectToString(input);
        System.out.println(xmlStr);
        return convertStringToJaxbObject(xmlStr);
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
        }
    }
}
