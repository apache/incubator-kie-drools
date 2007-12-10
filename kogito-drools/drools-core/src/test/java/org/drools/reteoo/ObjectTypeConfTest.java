package org.drools.reteoo;


import junit.framework.TestCase;

public class ObjectTypeConfTest extends TestCase {

	public void testGetPackageName() {
		assertEquals("org.drools.reteoo", ClassObjectTypeConf.getPackageName(this.getClass(), null));
		Package thispkg = this.getClass().getPackage();
		assertNotNull(thispkg);
		assertEquals("org.drools.reteoo", ClassObjectTypeConf.getPackageName(this.getClass(), thispkg));
	}
	
}
