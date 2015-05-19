package org.jbpm.kie.services.impl.bpmn2.builder;

import org.drools.compiler.compiler.ProcessBuilder;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescRepoHelper;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;

/**
 * This interface defines the method that the data service {@link ActionBuilder}, {@link ReturnValueEvaluatorBuilder}, 
 * {@link AssignmentBuilder} and {@link ProcessBuilder} implementations must implement so that a {@link ProcessDescRepoHelper} 
 * can be (thread-locally) set. 
 * </p>
 * That way, when the process is built, the data service *Builder implementations will store information in the 
 * {@link ProcessDescRepoHelper} instance, primarily about other referenced resources, such as java classes
 * and DRL/rules.
 */
public interface DataServiceExpressionBuilder {

    void setProcessHelperForThread( ProcessDescRepoHelper helper );

    ProcessDescRepoHelper getProcessHelperForThread();
}
