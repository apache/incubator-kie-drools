package org.jbpm.process.workitem.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

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
		ksession.startProcess("com.sample.bpmn.java.list", params);
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
		kbuilder.add(ResourceFactory.newClassPathResource("JavaInvokerListResult.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
	}
}
