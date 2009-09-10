package org.drools.persistence.processinstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
public class VariablePersistenceStrategyFactory {
	
	private static VariablePersistenceStrategy INSTANCE;

	public static VariablePersistenceStrategy getVariablePersistenceStrategy() {
		if (INSTANCE == null) {
			INSTANCE = new VariablePersistenceStrategy();
			loadPersisters();
		}
		return INSTANCE;
	}
	
	private static VariablePersistenceStrategy loadPersisters() {
		Properties props = new Properties();
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("META-INF/PersistenceStrategies.conf");
			if (is != null) {
				props.load(is);
			}
		} catch (IOException ex) {
			Logger.getLogger(VariablePersistenceStrategyFactory.class.getName())
				.log(Level.SEVERE, null, ex);
		}
		if (INSTANCE == null) {
			INSTANCE = new VariablePersistenceStrategy();
		}
		for (Entry<Object, Object> entry : props.entrySet()) {
			INSTANCE.setPersister((String) entry.getKey(), (String) entry.getValue());
		}
		return INSTANCE;
	}
	
}
