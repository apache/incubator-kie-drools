package org.jbpm.process.workitem.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.impl.EnvironmentFactory;
import org.kie.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.java.JavaInvocationWorkItemHandler;

public class JavaInvokerTest extends TestCase {

	public void testStaticMethod1() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "staticMethod1");
		params.put("Object", new MyJavaClass());
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void testStaticMethod2() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "staticMethod2");
		params.put("Object", new MyJavaClass());
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		params.put("Parameters", parameters);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void testMyFirstMethod1() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "myFirstMethod");
		params.put("Object", new MyJavaClass());
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		parameters.add(32);
		params.put("Parameters", parameters);
		List<String> parameterTypes = new ArrayList<String>();
		parameterTypes.add("java.lang.String");
		parameterTypes.add("java.lang.Integer");
		params.put("ParameterTypes", parameterTypes);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void testMyFirstMethod2() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "myFirstMethod");
		params.put("Object", new MyJavaClass());
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		parameters.add("32");
		params.put("Parameters", parameters);
		List<String> parameterTypes = new ArrayList<String>();
		parameterTypes.add("java.lang.String");
		parameterTypes.add("java.lang.String");
		params.put("ParameterTypes", parameterTypes);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void testMyFirstMethod3() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "myFirstMethod");
		params.put("Object", new MyJavaClass());
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		parameters.add(32);
		parameters.add("male");
		params.put("Parameters", parameters);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void testMySecondMethod() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "mySecondMethod");
		params.put("Object", new MyJavaClass());
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		List<String> children = new ArrayList<String>();
		children.add("Arne");
		parameters.add(children);
		params.put("Parameters", parameters);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	public void failingtestHello() throws Exception {
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = createSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Java", new JavaInvocationWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Class", "org.jbpm.process.workitem.java.MyJavaClass");
		params.put("Method", "writeHello");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("krisv");
		params.put("Parameters", parameters);
		ksession.startProcess("com.sample.bpmn.java", params);
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("JavaInvoker.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
	}
}