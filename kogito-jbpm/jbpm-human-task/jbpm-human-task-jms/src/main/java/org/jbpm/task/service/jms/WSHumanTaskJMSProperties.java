package org.jbpm.task.service.jms;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class WSHumanTaskJMSProperties {

	private static final WSHumanTaskJMSProperties INSTANCE = new WSHumanTaskJMSProperties();
	
	private Properties properties = new Properties(); 
	
	private WSHumanTaskJMSProperties() {
		super();
		ResourceBundle bundle = ResourceBundle.getBundle("jmsht.conf");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);
			this.properties.setProperty(key, value);
		}
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public static WSHumanTaskJMSProperties getInstance() {
		return INSTANCE;
	}
}
