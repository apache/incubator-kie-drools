package org.drools.persistence.jpa.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.SessionConfiguration;
import org.drools.command.CommandService;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.jpa.JPAKnowledgeServiceProvider;
import org.drools.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.drools.persistence.processinstance.JPASignalManagerFactory;
import org.drools.persistence.processinstance.JPAWorkItemManagerFactory;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.instance.ProcessInstanceManagerFactory;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.process.instance.event.SignalManagerFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public class JPAKnowledgeServiceProviderImpl
		implements
		JPAKnowledgeServiceProvider {

	private Class<? extends CommandService> commandServiceClass;
	private Class<? extends ProcessInstanceManagerFactory> processInstanceManagerFactoryClass;
	private Class<? extends WorkItemManagerFactory> workItemManagerFactoryClass;
	private Class<? extends SignalManagerFactory> processSignalManagerFactoryClass;

	private Properties configProps = new Properties();

	public JPAKnowledgeServiceProviderImpl() {
		setDefaultImplementations();
	}

	protected void setDefaultImplementations() {
		setCommandServiceClass(SingleSessionCommandService.class);
		setProcessInstanceManagerFactoryClass(JPAProcessInstanceManagerFactory.class);
		setWorkItemManagerFactoryClass(JPAWorkItemManagerFactory.class);
		setProcessSignalManagerFactoryClass(JPASignalManagerFactory.class);
	}

	public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
			KnowledgeSessionConfiguration configuration,
			Environment environment) {
		if (configuration == null) {
			configuration = new SessionConfiguration();
		}

		if (environment == null) {
			throw new IllegalArgumentException("Environment cannot be null");
		}

		return new CommandBasedStatefulKnowledgeSession(
				buildCommanService(kbase, mergeConfig(configuration), environment));
	}

	public StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
			KnowledgeBase kbase,
			KnowledgeSessionConfiguration configuration,
			Environment environment) {
		if (configuration == null) {
			configuration = new SessionConfiguration();
		}

		if (environment == null) {
			throw new IllegalArgumentException("Environment cannot be null");
		}

		return new CommandBasedStatefulKnowledgeSession(
				buildCommanService(id, kbase, mergeConfig(configuration), environment));
	}

	private CommandService buildCommanService(int sessionId,
			KnowledgeBase kbase,
			KnowledgeSessionConfiguration conf,
			Environment env) {

		try {
			Class<? extends CommandService> serviceClass = getCommandServiceClass();
			Constructor<? extends CommandService> constructor = serviceClass.getConstructor(int.class,
					KnowledgeBase.class, KnowledgeSessionConfiguration.class, Environment.class);
			return constructor.newInstance(sessionId, kbase, conf, env);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private CommandService buildCommanService(KnowledgeBase kbase,
			KnowledgeSessionConfiguration conf,
			Environment env) {

		Class<? extends CommandService> serviceClass = getCommandServiceClass();
		try {
			Constructor<? extends CommandService> constructor = serviceClass.getConstructor(KnowledgeBase.class,
					KnowledgeSessionConfiguration.class, Environment.class);
			return constructor.newInstance(kbase, conf, env);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private KnowledgeSessionConfiguration mergeConfig(KnowledgeSessionConfiguration configuration) {
		((SessionConfiguration) configuration).addProperties(configProps);
		return configuration;
	}

	public int getStatefulKnowledgeSessionId(StatefulKnowledgeSession ksession) {
		if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
			SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession)
					.getCommandService();
			return commandService.getSessionId();
		}
		throw new IllegalArgumentException("StatefulKnowledgeSession must be an a CommandBasedStatefulKnowledgeSession");
	}

	public void setCommandServiceClass(Class<? extends CommandService> commandServiceClass) {
		if (commandServiceClass != null) {
			this.commandServiceClass = commandServiceClass;
			configProps.put("drools.commandService", commandServiceClass.getName());
		}
	}

	public Class<? extends CommandService> getCommandServiceClass() {
		return commandServiceClass;
	}

	public void setProcessInstanceManagerFactoryClass(
			Class<? extends ProcessInstanceManagerFactory> processInstanceManagerFactoryClass) {
		if (processInstanceManagerFactoryClass != null) {
			this.processInstanceManagerFactoryClass = processInstanceManagerFactoryClass;
			configProps.put("drools.processInstanceManagerFactory", processInstanceManagerFactoryClass.getName());
		}
	}

	public Class<? extends ProcessInstanceManagerFactory> getProcessInstanceManagerFactoryClass() {
		return processInstanceManagerFactoryClass;
	}

	public void setWorkItemManagerFactoryClass(Class<? extends WorkItemManagerFactory> workItemManagerFactoryClass) {
		if (workItemManagerFactoryClass != null) {
			this.workItemManagerFactoryClass = workItemManagerFactoryClass;
			configProps.put("drools.workItemManagerFactory", workItemManagerFactoryClass.getName());
		}
	}

	public Class<? extends WorkItemManagerFactory> getWorkItemManagerFactoryClass() {
		return workItemManagerFactoryClass;
	}

	public void setProcessSignalManagerFactoryClass(
			Class<? extends SignalManagerFactory> processSignalManagerFactoryClass) {
		if (processSignalManagerFactoryClass != null) {
			this.processSignalManagerFactoryClass = processSignalManagerFactoryClass;
			configProps.put("drools.processSignalManagerFactory", processSignalManagerFactoryClass.getName());
		}
	}

	public Class<? extends SignalManagerFactory> getProcessSignalManagerFactoryClass() {
		return processSignalManagerFactoryClass;
	}

}
