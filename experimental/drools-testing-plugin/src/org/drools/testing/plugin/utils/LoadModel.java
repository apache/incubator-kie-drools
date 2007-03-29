package org.drools.testing.plugin.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.plugin.exception.DroolsPluginException;
import org.drools.testing.plugin.exception.LoadTestSuiteException;
import org.eclipse.ui.part.FileEditorInput;
import org.exolab.castor.xml.Unmarshaller;

public class LoadModel {

	public LoadModel () {
		
	}
	
	public static TestSuite loadTestSuite (FileEditorInput fileEditorInput) throws DroolsPluginException {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileEditorInput.getFile().getName()));
			Unmarshaller unmarshaller = new Unmarshaller(TestSuite.class);
			TestSuite testSuite = (TestSuite) unmarshaller.unmarshal(br);
			return testSuite;
		}catch (Exception e) {
			throw new LoadTestSuiteException("Exception ocurred",e);
		}
	}
	
	public static TestSuite loadTestSuite (String content) throws DroolsPluginException {
		
		try {
			BufferedReader br = new BufferedReader(new StringReader(content));
			Unmarshaller unmarshaller = new Unmarshaller(TestSuite.class);
			TestSuite testSuite = (TestSuite) unmarshaller.unmarshal(br);
			return testSuite;
		}catch (Exception e) {
			throw new LoadTestSuiteException("Exception ocurred",e);
		}
	}
}
