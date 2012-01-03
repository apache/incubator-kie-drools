package org.jbpm.examples.quickstarts;

public class HelloService {
	private static final HelloService INSTANCE = new HelloService();
	public static HelloService getInstance() {
		return INSTANCE;
	}
	public void sayHello(String name) {
		System.out.println("Hello " + name);
	}
}
