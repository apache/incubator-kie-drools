package org.drools.persistence.processinstance.persisters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.drools.persistence.processinstance.variabletypes.SerializablePersistedVariable;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
public class SerializableVariablePersister implements VariablePersister {

	public VariableInstanceInfo persistExternalVariable(String name, Object o,
			VariableInstanceInfo oldValue, Environment env) {
                boolean newVariable = false;
		EntityManager em = (EntityManager) env.get(EnvironmentName.ENTITY_MANAGER);
		SerializablePersistedVariable result = null;
                if(o == null || (oldValue != null && oldValue.getPersister().equals(""))){
                    return null;
                }
		if (oldValue instanceof SerializablePersistedVariable) {
			result = (SerializablePersistedVariable) oldValue;
		}
		if (result == null) {
			result = new SerializablePersistedVariable();
			newVariable = true;
			
		}
                result.setPersister(this.getClass().getName());
                result.setName(name);
		result.setContent(getBytes(o));
                if(newVariable){
                    em.persist(result);
                }else{
                    em.merge(result);
                }
                
		return result;
	}

	public Object getExternalPersistedVariable(
			VariableInstanceInfo variableInstanceInfo, Environment env) {
		ObjectInputStream ois = null;
		byte[] binaryArray = null;
                if(((SerializablePersistedVariable) variableInstanceInfo) == null || ((SerializablePersistedVariable) variableInstanceInfo).getContent() == null){
                    return null;
                }
		try {
			binaryArray = ((SerializablePersistedVariable) variableInstanceInfo).getContent();
			if (binaryArray == null) {
				return null;
			}
			ByteArrayInputStream strmBytes = new ByteArrayInputStream(binaryArray);
			ois = new ObjectInputStream(strmBytes);
			return ois.readObject();
		} catch (IOException ex) {
			Logger.getLogger(SerializableVariablePersister.class.getName())
				.log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(JPAVariablePersister.class.getName())
				.log(Level.SEVERE, null, ex);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(SerializableVariablePersister.class.getName())
					.log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	private byte[] getBytes(Object o) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.flush();
			oos.close();
			bos.close();
			byte[] data = bos.toByteArray();
			return data;
		} catch (IOException ex) {
			Logger.getLogger(SerializableVariablePersister.class.getName())
				.log(Level.SEVERE, null, ex);
		} finally {
			try {
				oos.close();
			} catch (IOException ex) {
				Logger.getLogger(SerializableVariablePersister.class.getName())
					.log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}
}
