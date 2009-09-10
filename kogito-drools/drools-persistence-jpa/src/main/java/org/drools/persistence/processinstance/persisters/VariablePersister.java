package org.drools.persistence.processinstance.persisters;

import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;

/**
 *
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author salaboy
 */
public interface VariablePersister {
    
    VariableInstanceInfo persistExternalVariable(
		String name,
		Object o,
		VariableInstanceInfo oldValue,
		Environment env);

    Object getExternalPersistedVariable(
		VariableInstanceInfo variableInstanceInfo,
		Environment env);

}
