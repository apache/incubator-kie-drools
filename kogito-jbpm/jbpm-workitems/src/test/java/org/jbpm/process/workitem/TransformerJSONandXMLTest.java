package org.jbpm.process.workitem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.workitem.rest.Person;
import org.junit.Test;
import org.kie.api.runtime.process.DataTransformer;

public class TransformerJSONandXMLTest {

	@Test
	public void testJSONTransformer() {
		String expectedJson = "{\"name\":\"john\",\"age\":34}";
		DataTransformer transformer = DataTransformerRegistry.get().find("http://www.mvel.org/2.0");
		
		String expressionR = "new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, clazz)";
		
		String expressionW = "new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(employee)";
		
		Object compiled = transformer.compile(expressionW, new HashMap<String, Object>());
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Person person = new Person();
		person.setAge(34);
		person.setName("john");
		parameters.put("employee", person);
		
		Object result = transformer.transform(compiled, parameters);
		
		System.out.println(result);
		assertEquals(expectedJson, result);
		
		Object compiledR = transformer.compile(expressionR, new HashMap<String, Object>());
		
		parameters.clear();
		parameters.put("json", result);
		parameters.put("clazz", Person.class);
		
		Object resultr = transformer.transform(compiledR, parameters);
		
		System.out.println(resultr);
		assertNotNull(resultr);
		assertTrue(resultr instanceof Person);
		assertEquals("john", ((Person) resultr).getName());
		assertEquals(34, ((Person) resultr).getAge().intValue());
	}
	
	@Test
	public void testJAXBTransformer() {
		String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><age>34</age><name>john</name></person>";
		DataTransformer transformer = DataTransformerRegistry.get().find("http://www.mvel.org/2.0");
		
		String expressionR = "java.io.StringReader result = new java.io.StringReader(xml);"
				+ "return javax.xml.bind.JAXBContext.newInstance(classes).createUnmarshaller().unmarshal(result);";
		
		String expressionW = "java.io.StringWriter result = new java.io.StringWriter();"
				+ "javax.xml.bind.JAXBContext.newInstance(classes).createMarshaller().marshal(employee, result);"
				+ "return result.toString();";
		
		Object compiled = transformer.compile(expressionW, new HashMap<String, Object>());
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Person person = new Person();
		person.setAge(34);
		person.setName("john");
		parameters.put("employee", person);
		parameters.put("classes", Person.class);
		Object result = transformer.transform(compiled, parameters);
		
		System.out.println(result);
		assertEquals(expectedXml, result);
		
		Object compiledR = transformer.compile(expressionR, new HashMap<String, Object>());
		
		parameters.clear();
		parameters.put("classes", Person.class);
		parameters.put("xml", result);
		
		Object resultr = transformer.transform(compiledR, parameters);
		
		System.out.println(resultr);
		assertNotNull(resultr);
		assertTrue(resultr instanceof Person);
		assertEquals("john", ((Person) resultr).getName());
		assertEquals(34, ((Person) resultr).getAge().intValue());
		
	}
}
