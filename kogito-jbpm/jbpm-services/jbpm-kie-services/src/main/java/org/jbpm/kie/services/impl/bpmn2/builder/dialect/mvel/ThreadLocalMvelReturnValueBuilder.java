/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.kie.services.impl.bpmn2.builder.dialect.mvel;

import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.kie.services.impl.bpmn2.builder.GenericDefaultThreadLocal;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.ThreadLocalAbstractBuilderFacade;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.mvel.MVELReturnValueEvaluatorBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;

/**
 * @see {@link ThreadLocalAbstractBuilderFacade}
 */
public class ThreadLocalMvelReturnValueBuilder extends ThreadLocalAbstractBuilderFacade<ReturnValueEvaluatorBuilder> implements ReturnValueEvaluatorBuilder {

    private final static ReturnValueEvaluatorBuilder defaultBuilderInstance = new MVELReturnValueEvaluatorBuilder();
    private final static ThreadLocal<ReturnValueEvaluatorBuilder> threadLocalBuilder 
        = new GenericDefaultThreadLocal<ReturnValueEvaluatorBuilder>(defaultBuilderInstance);
    private final static DataServiceExpressionBuilder dataServiceBuilderInstance 
        = new DataServiceMvelReturnValueEvaluatorBuilder();

    @Override
    public ReturnValueEvaluatorBuilder getThreadLocalBuilder() {
        return threadLocalBuilder.get();
    }

    @Override
    public void setThreadLocalBuilder( ReturnValueEvaluatorBuilder actionBuilder ) {
       threadLocalBuilder.set(actionBuilder); 
    }

    @Override
    public DataServiceExpressionBuilder getDataServiceBuilderInstance() {
        return dataServiceBuilderInstance;
    }

    @Override
    public ReturnValueEvaluatorBuilder getDefaultBuilderInstance() {
        return defaultBuilderInstance;
    }

    @Override
    public void build( PackageBuildContext context, ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
            ReturnValueDescr returnValueDescr, ContextResolver contextResolver ) {
        getThreadLocalBuilder().build(context, returnValueConstraintEvaluator, returnValueDescr, contextResolver);
    }
  
}