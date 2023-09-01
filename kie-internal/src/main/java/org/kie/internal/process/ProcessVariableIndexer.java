package org.kie.internal.process;

import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.utils.VariableIndexer;


/**
 * Dedicated type for custom implementations of VariableIndexer for process variables
 *
 */
public interface ProcessVariableIndexer extends VariableIndexer<VariableInstanceLog> {

}
