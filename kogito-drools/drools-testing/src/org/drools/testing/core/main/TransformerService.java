package org.drools.testing.core.main;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.exception.TransformerServiceException;
import org.drools.testing.core.rules.RuleSetTest;
import org.drools.xml.XmlDumper;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

public final class TransformerService {

	public TransformerService () {
	}
	
	/**
	 * parseDrl uses the drools drl parser to read the drl and return object description
	 */
	public final static PackageDescr parseDrl (String file) throws RuleTestLanguageException {
		
		DrlParser drlParser = new DrlParser();
		try {
			Reader reader = new InputStreamReader( RuleSetTest.class.getResourceAsStream( file ) );
			return drlParser.parse(reader);
		}catch (Exception e) {
			throw new TransformerServiceException("Exception ocurred ",e);
		}
	}
	
	/**
	 * unMarshallDocument takes a drools PackageDescr and uses castor to
	 * unmarshall the document to a data transfer object indicative of an RTL file
	 */
	
	public Object unMarshallDocument (PackageDescr packageDescr) throws RuleTestLanguageException {
		
		Mapping mapping = new Mapping();
		try {
			InputSource inputSource = new InputSource(getClass().getResourceAsStream("/org/drools/testing/core/resources/castor/castor-mapping.xml"));
			mapping.loadMapping(inputSource);
			Unmarshaller unmarshaller = new Unmarshaller(mapping);
			XmlDumper xmlDumper = new XmlDumper();
			Object test = (Object) unmarshaller.unmarshal(new InputSource(new StringReader(xmlDumper.dump(packageDescr))));
			return test;
		}catch (Exception e) {
			throw new TransformerServiceException("Exception ocurred ",e);
		}
	}
	
	/**
	 * generateTestSuite
	 * takes a drl, unmarshalls it to a TestSuite object.
	 * 
	 * This method can be overloaded so that the document being parsed and mapped
	 * can be any structure, for the moment I am just handling a mapping 
	 * for drools drl files.
	 */
	public TestSuite generateTestSuite (String file) throws RuleTestLanguageException {
		TestSuite testSuite=null;
		try {
			testSuite = (TestSuite) unMarshallDocument(parseDrl(file));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return testSuite;
	}
	
}
