package org.jbpm.kie.services.impl.bpmn2.builder.dialect;

import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescRepoHelper;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.java.DataServiceJavaProcessDialect;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.builder.dialect.java.JavaActionBuilder;
import org.kie.internal.builder.KnowledgeBuilder;

/**
 * For <b>every</b> thread in which the {@link BPMN2DataServiceImpl} instance collects process information, 
 * we use the <b>same</b> {@link DataServiceExpressionBuilder} instance. This happens because of the {@link ThreadLocal}&lt;DataServiceExpressionBuilder&gt;
 * field in the implementations of this class.
 * <p>
 * However, the {@link DataServiceExpressionBuilder} instance <b>also</b> contains
 * a {@link ThreadLocal} variable to store {@link ProcessDescRepoHelper} instances. 
 * That way we can use a particular thread local {@link ProcessDescRepoHelper} instance
 * per thread to save all the information for the particular process being built: see
 * {@link ThreadLocalAbstractBuilderFacade#useDataServiceBuilder(ProcessDescRepoHelper)}.
 * </p><p>
 * The main reason for this architecture is that the jbpm-flow/jbpm-bpmn2 infrastructure calls
 * {@link ProcessDialectRegistry#getDialect(String)} directly -- while the data service
 * only needs to replace the result of the {@link ProcessDialect#getActionBuilder()} and similar methods.
 * <p>
 * The logic looks like this:<ul>
 * <li>The {@link BPMN2DataServiceImpl} sets the thread-local {@link ProcessDescRepoHelper} instance on the various builders
 *     (via this class)</li>
 * <li>When {@link KnowledgeBuilder} is processing a definition, {@link ProcessDialectRegistry#getDialect(String)} is called 
 *     when a script or expression is encountered.</li>
 * <li>{@link ProcessDialectRegistry#getDialect(String)} returns a data service implemenation of a dialect, 
 * for example: {@link DataServiceJavaProcessDialect}.</li>
 * <li>{@link ProcessDialect#getActionBuilder()} returns the (singleton) {@link ThreadLocalAbstractBuilderFacade} implementation
 * of the {@link ActionBuilder} instance</li>
 * <li>When the build(...) method on the {@link ActionBuilder} instance is called, it collects information into the thread-local
 * {@link ProcessDescRepoHelper} instance</li>
 * <li>After the process definition has been compiled, the thread-local {@link ActionBuilder} field in the 
 * {@link ThreadLocalAbstractBuilderFacade} implementation is reset to the defaul {@link JavaActionBuilder}.
 * </ul>
 * @param <T> The Builder type, one of the following: {@link ActionBuilder}, {@link ReturnValueEvaluatorBuilder}, {@link AssignmentBuilder} or
 * {@link ProcessClassBuilder}.
 */
public abstract class ThreadLocalAbstractBuilderFacade<T> {

    public abstract T getThreadLocalBuilder();
    public abstract void setThreadLocalBuilder(T actionBuilder);
    public abstract DataServiceExpressionBuilder getDataServiceBuilderInstance();
    public abstract T getDefaultBuilderInstance();
    
    @SuppressWarnings("unchecked")
    public void useDataServiceBuilder(ProcessDescRepoHelper helper) { 
        DataServiceExpressionBuilder dataServiceBuilderInstance = getDataServiceBuilderInstance();
        dataServiceBuilderInstance.setProcessHelperForThread(helper);
        setThreadLocalBuilder((T) dataServiceBuilderInstance);
    }
    
    public void resetThreadLocalBuilder() { 
        setThreadLocalBuilder(getDefaultBuilderInstance());
    }
}
