package org.drools.testing.core.main;

import java.io.FileWriter;

import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.TestSuite;
import org.drools.xml.XmlDumper;


public class SchemaBuilder {

	public static final void main(final String[] args) {
		SchemaBuilder schemaBuilder = new SchemaBuilder();
		//schemaBuilder.buildSchema();
		schemaBuilder.buildTestSuite();
    }
	
	public void buildSchema () {
		try {
			PackageDescr packageDescr = TransformerService.parseDrl("/org/drools/testing/core/resources/drl/test.drl");
			XmlDumper xmlDumper = new XmlDumper();
			FileWriter writer = new FileWriter("drl.xml");
			writer.write(xmlDumper.dump(packageDescr));
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildTestSuite () {
		try {
			TransformerService trService = new TransformerService();
			TestSuite testSuite = trService.generateTestSuite("/org/drools/testing/core/resources/drl/test.drl");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
