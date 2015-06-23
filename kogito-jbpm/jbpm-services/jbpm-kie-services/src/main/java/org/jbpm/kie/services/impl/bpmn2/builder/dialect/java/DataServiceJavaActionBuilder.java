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

package org.jbpm.kie.services.impl.bpmn2.builder.dialect.java;

import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescRepoHelper;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.process.builder.dialect.java.JavaActionBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;

class DataServiceJavaActionBuilder extends JavaActionBuilder implements DataServiceExpressionBuilder {

    private static final ThreadLocal<ProcessDescRepoHelper> threadLocalHelper 
        = new ThreadLocal<ProcessDescRepoHelper>();

    @Override
    public void setProcessHelperForThread( ProcessDescRepoHelper helper ) {
       threadLocalHelper.set(helper);
    }

    @Override
    public ProcessDescRepoHelper getProcessHelperForThread() {
       return threadLocalHelper.get();
    }
    
    @Override
    public void build( PackageBuildContext context, 
                       DroolsAction action, 
                       ActionDescr actionDescr, 
                       ContextResolver contextResolver ) {
       
        AnalysisResult analysis = getAnalysis(context, actionDescr);
    
        JavaAnalysisResult javaAnalysis = (JavaAnalysisResult) analysis;
        LOCAL_VAR: for( JavaLocalDeclarationDescr localDeclDescr : javaAnalysis.getLocalVariablesMap().values() ) { 
            String type = localDeclDescr.getRawType();
            Set<String> referencedTypes = getProcessHelperForThread().getReferencedClasses(); 
            if( type.contains(".") ) { 
                referencedTypes.add(type);
            } else { 
                for( String alreadyRefdType : referencedTypes ) { 
                    String alreadyRefdSimpleName = alreadyRefdType.substring(alreadyRefdType.lastIndexOf(".") + 1);
                   if( type.equals(alreadyRefdSimpleName) ) { 
                       continue LOCAL_VAR;
                   }
                }
                getProcessHelperForThread().getUnqualifiedClasses().add(type);
            }
        }
       
        // if this should actually compile the expression, see the parent class build(..) method for what should go here
    }
  
}
