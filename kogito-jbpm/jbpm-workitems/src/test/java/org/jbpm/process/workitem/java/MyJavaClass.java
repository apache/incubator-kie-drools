package org.jbpm.process.workitem.java;

import java.util.ArrayList;
import java.util.List;

public class MyJavaClass {
	
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
		System.out.println("Hello " + name);
	}

}