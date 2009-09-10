package org.drools.persistence.processinstance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.persistence.processinstance.persisters.VariablePersister;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
public class VariablePersistenceStrategy {

	// map of variable persisters per type
	private Map<String, String> types = new HashMap<String, String>();
	// cache of already instantiated variable persisters
	private Map<String, VariablePersister> variablePersisters =
		new HashMap<String, VariablePersister>();
	
	public void setPersister(String type, String persisterClassname) {
		types.put(type, persisterClassname);
	}
	
	public boolean isEnabled() {
		return !types.isEmpty();
	}
	
	public VariableInstanceInfo persistVariable(String name, Object o,
			VariableInstanceInfo oldValue, Environment env) {
		VariablePersister persister = getVariablePersister(o);
		VariableInstanceInfo variable = null;
		if (persister != null) {
			variable = persister.persistExternalVariable(name, o, oldValue, env);
		}
		return variable;
	}

	@SuppressWarnings("unchecked")
	private VariablePersister getVariablePersister(Object o) {
		VariablePersister persister = null;
		String persisterFQN = getVariablePersistenceType(o);
		if (persisterFQN != null && !persisterFQN.equals("")) {
			Class<VariablePersister> persisterClass = null;
			persister = variablePersisters.get(persisterFQN);
			if (persister != null) {
				return persister;
			}
			try {
				persisterClass = (Class<VariablePersister>) Class.forName(persisterFQN);
				Constructor<VariablePersister> constructor = persisterClass.getConstructor();
				persister = (VariablePersister) constructor.newInstance();
				variablePersisters.put(persisterFQN, persister);
				return persister;
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(VariablePersistenceStrategy.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getVariable(VariableInstanceInfo variableInfo, Environment env) {
		try {
			String persisterFQN = variableInfo.getPersister();
			VariablePersister persister = variablePersisters.get(persisterFQN);
			if (persister == null) {
				Class<VariablePersister> clazz = (Class<VariablePersister>) Class.forName(persisterFQN);
				Constructor<VariablePersister> constructor = clazz.getDeclaredConstructor();
				persister = (VariablePersister) constructor.newInstance();
				variablePersisters.put(persisterFQN, persister);
			}
			return persister.getExternalPersistedVariable(variableInfo, env);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(VariablePersistenceStrategy.class.getName())
				.log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private String getVariablePersistenceType(Object o) {
		if (o == null) {
			return null;
		}
		Annotation[] annotations = o.getClass().getDeclaredAnnotations();
		if (annotations != null) {
			// First annotations, because annotations have more precedence
			for (Annotation annotation : annotations) {
				String persisterFQN = types.get(annotation.annotationType().getName());
				if (persisterFQN != null && !persisterFQN.equals("")) {
					return persisterFQN;
				}
			}
		}
		// Then interfaces
		Class<?>[] interfaces = o.getClass().getInterfaces();
		if (interfaces != null) {
			for (Class<?> clazz : interfaces) {
				String persisterFQN = types.get(clazz.getName());
				if (persisterFQN != null && !persisterFQN.equals("")) {
					return persisterFQN;
				}
			}
		}
		return null;
	}

}
