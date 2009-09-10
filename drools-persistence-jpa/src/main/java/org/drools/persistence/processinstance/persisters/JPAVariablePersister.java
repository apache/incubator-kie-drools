package org.drools.persistence.processinstance.persisters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.drools.persistence.processinstance.variabletypes.JPAPersistedVariable;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
public class JPAVariablePersister implements VariablePersister {

	public VariableInstanceInfo persistExternalVariable(String name, Object o,
			VariableInstanceInfo oldValue, Environment env) {
                if(o == null || (oldValue != null && oldValue.getPersister().equals(""))){
                    return null;
                }
		try {
			boolean newVariable = false;
                        EntityManager em = (EntityManager) env.get(EnvironmentName.ENTITY_MANAGER);
			JPAPersistedVariable result = null;
			if (oldValue instanceof JPAPersistedVariable) {
				result = (JPAPersistedVariable) oldValue;
			}
			if (result == null) {
				result = new JPAPersistedVariable();
				
				newVariable = true;
			}
			Long idValue = getClassIdValue(o);
			if (idValue != null) {
                                System.out.println("Variable "+name +" -> Updating external Entity = "+o);
				em.merge(o);
			} else {
                                System.out.println("Variable "+name +" -> Persisting external Entity for the first time ="+o);
				em.persist(o);
				idValue = getClassIdValue(o);
			}
			result.setPersister(this.getClass().getName());
                        result.setName(name);
                        // entity might have changed, updating info
			result.setEntityId(idValue);
			result.setEntity(o);
			result.setEntityClass(o.getClass().getCanonicalName());
                        if(newVariable){
                            em.persist(result);
                        }else{
                            em.merge(result);
                        }
			System.out.println("Saving JPAPersistedVariable id=" + result.getId() + " entityId=" + result.getEntityId() + " class=" + result.getEntityClass() + " value=" + result.getEntity());
			return result;
		} catch (Throwable t) {
			Logger.getLogger(JPAVariablePersister.class.getName())
				.log(Level.SEVERE, null, t);
			throw new RuntimeException("Could not persist external variable", t);
		}
	}

	public Object getExternalPersistedVariable(
			VariableInstanceInfo variableInstanceInfo, Environment env) {
		EntityManager em = (EntityManager) env.get(EnvironmentName.ENTITY_MANAGER);
                if(((JPAPersistedVariable) variableInstanceInfo) == null || ((JPAPersistedVariable) variableInstanceInfo).getEntityId() == null){
                    return null;
                }
		System.out.println("Restoring JPAPersistedVariable id=" + ((JPAPersistedVariable) variableInstanceInfo).getId() + " entityId=" + ((JPAPersistedVariable) variableInstanceInfo).getEntityId() + " class=" + ((JPAPersistedVariable) variableInstanceInfo).getEntityClass() + " value=" + ((JPAPersistedVariable) variableInstanceInfo).getEntity());
		try {
			String varType = ((JPAPersistedVariable) variableInstanceInfo).getEntityClass();
			return em.find(Class.forName(varType),
				((JPAPersistedVariable) variableInstanceInfo).getEntityId());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(JPAVariablePersister.class.getName())
				.log(Level.SEVERE, null, ex);
			throw new RuntimeException("Could not restore external variable", ex);
		}
	}

	private Long getClassIdValue(Object o) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, IllegalArgumentException {
		Field[] fields = o.getClass().getDeclaredFields();
		Long idValue = null;
		for (int i = 0; i < fields.length; i++) {
			Id id = fields[i].getAnnotation(Id.class);
			if (id != null) {
				idValue = (Long) o.getClass().getMethod("get"
					+ Character.toUpperCase(fields[i].getName().charAt(0))
					+ fields[i].getName().substring(1),
					new Class<?>[] {}).invoke(o, new Object[] {});
				break;
			}
		}
		return idValue;
	}
}
