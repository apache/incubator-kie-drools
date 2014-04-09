package org.jbpm.runtime.manager.impl.deploy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject;
import org.junit.Test;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;

public class MVELObjectModelResolverTest {

	@Test
	public void testSimpleNoArgObjectModel() {
		ObjectModel model = new ObjectModel("new org.jbpm.runtime.manager.impl.deploy.testobject.SimpleCustomObject()");
		
		ObjectModelResolver resolver = new MVELObjectModelResolver();
		
		Object result = resolver.getInstance(model, this.getClass().getClassLoader(), new HashMap<String, Object>());
		assertNotNull(result);
		assertTrue(result instanceof SimpleCustomObject);
		assertEquals("default", ((SimpleCustomObject) result).getName());
	}
	
	@Test
	public void testSimpleStaticObjectModel() {
		ObjectModel model = new ObjectModel("Integer.parseInt(\"10\")");
		
		ObjectModelResolver resolver = new MVELObjectModelResolver();
		
		Object result = resolver.getInstance(model, this.getClass().getClassLoader(), new HashMap<String, Object>());
		assertNotNull(result);
		assertTrue(result instanceof Integer);
		assertEquals(10, ((Integer) result).intValue());
	}
	
	@Test
	public void testComplexStaticObjectModel() {
		ObjectModel model = new ObjectModel("String.valueOf(10).substring(1)");
		
		ObjectModelResolver resolver = new MVELObjectModelResolver();
		
		Object result = resolver.getInstance(model, this.getClass().getClassLoader(), new HashMap<String, Object>());
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals("0", ((String) result));
	}
}
