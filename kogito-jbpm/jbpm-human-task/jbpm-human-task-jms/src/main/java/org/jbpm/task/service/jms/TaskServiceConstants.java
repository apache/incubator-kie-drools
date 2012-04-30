package org.jbpm.task.service.jms;

public interface TaskServiceConstants {

	String SELECTOR_NAME = "taskClientId";
	
	String TASK_SERVER_RESPONSE_QUEUE_NAME_NAME = "JMSTaskServer.responseQueueName";
	String TASK_SERVER_QUEUE_NAME_NAME = "JMSTaskServer.queueName";
	String TASK_SERVER_ACKNOWLEDGE_MODE_NAME = "JMSTaskServer.acknowledgeMode";
	String TASK_SERVER_TRANSACTED_NAME = "JMSTaskServer.transacted";
	String TASK_SERVER_CONNECTION_FACTORY_NAME = "JMSTaskServer.connectionFactory";
	
	String TASK_CLIENT_RESPONSE_QUEUE_NAME_NAME = "JMSTaskClient.responseQueueName";
	String TASK_CLIENT_QUEUE_NAME_NAME = "JMSTaskClient.queueName";
	String TASK_CLIENT_ACKNOWLEDGE_MODE_NAME = "JMSTaskClient.acknowledgeMode";
	String TASK_CLIENT_TRANSACTED_QUEUE_NAME = "JMSTaskClient.transactedQueue";
	String TASK_CLIENT_CONNECTION_FACTORY_NAME = "JMSTaskClient.connectionFactory";
	
	String NAMING_FACTORY_INITIAL_NAME = "java.naming.factory.initial";
	String NAMING_PROVIDER_URL_NAME = "java.naming.provider.url";
	String NAMING_FACTORY_URL_PKGS_NAME = "java.naming.factory.url.pkgs";
}
