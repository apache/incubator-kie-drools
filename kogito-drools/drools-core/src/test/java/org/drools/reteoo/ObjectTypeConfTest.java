package org.drools.reteoo;

import org.drools.reteoo.Rete.ObjectTypeConf;

import junit.framework.TestCase;

public class ObjectTypeConfTest extends TestCase {

	public void testGetPackageName() {
		assertEquals("org.drools.reteoo", ObjectTypeConf.getPackageName(this.getClass(), null));
		Package thispkg = this.getClass().getPackage();
		assertNotNull(thispkg);
		assertEquals("org.drools.reteoo", ObjectTypeConf.getPackageName(this.getClass(), thispkg));
	}
	
}
