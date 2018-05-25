package org.kie.pmml.pmml_4_2.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kie.pmml.pmml_4_2.model.ExternalBeanRef.ModelUsage;

public class ExternalBeanRefTest {
	private static final String pkgName = "org.drools.scorecard.example";
	private static final String clsName = "Attribute";

	@Test
	public void test() {
		ExternalBeanRef ref = new ExternalBeanRef("attribute","org.drools.scorecard.example.Attribute",ModelUsage.MINING);
		assertNotNull(ref);
		assertEquals(pkgName,ref.getBeanPackageName());
		assertEquals(clsName,ref.getBeanName());
		System.out.println(ref);
	}

}
