package org.jbpm.process.workitem.java;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyJavaClass {
    
    private static final Logger logger = LoggerFactory.getLogger(MyJavaClass.class);
	
	public MyJavaClass() {
	}
	
	public static String staticMethod1() {
		return "Hello World";
	}
	
	public static String staticMethod2(String name) {
		return "Hello " + name;
	}
	
	public String myFirstMethod(String name, Integer age) {
		return "Hello " + name + ", age " + age;
	}
	
	public String myFirstMethod(String name, String age) {
		return "Hello " + name + ", age " + age;
	}
	
	public String myFirstMethod(String name, Integer age, String gender) {
		return "Hello " + name + ", age " + age + ", gender " + gender;
	}
	
	public List<String> mySecondMethod(String name, List<String> children) {
		List<String> result = new ArrayList<String>();
		for (String child: children) {
			result.add("Hello " + child);
		}
		return result;
	}
	
	public void writeHello(String name) {
		logger.info("Hello {}", name);
	}

}