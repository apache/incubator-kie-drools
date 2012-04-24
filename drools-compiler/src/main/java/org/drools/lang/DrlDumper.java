/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.lang;

import java.util.HashMap;
import java.util.Map;

import org.drools.lang.descr.PackageDescr;
import org.drools.rule.builder.DroolsCompilerComponentFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;

/**
 * Dumps a PackageDescr into a DRL String
 */
public class DrlDumper  {

    protected final TemplateRegistry REPORT_REGISTRY = new SimpleTemplateRegistry();
    
    private ExpressionRewriter mvel = DroolsCompilerComponentFactory.getExpressionProcessor();

    public DrlDumper() {
        OptimizerFactory.setDefaultOptimizer( "reflective" );

        REPORT_REGISTRY.addNamedTemplate( "drl",
                                          TemplateCompiler.compileTemplate( DrlDumper.class.getResourceAsStream( "drl.mvel" ),
                                                                            (Map<String, Class< ? extends Node>>) null ) );

        /**
         * Process these templates
         */
        TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( "drl" ),
                                 null,
                                 REPORT_REGISTRY );
    }

    public String dump( final PackageDescr pkg ) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put( "pkg",
                     pkg );
        context.put( "mvel",
                     mvel );

        return (String) TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( "drl" ),
                                                 null,
                                                 new MapVariableResolverFactory( context ),
                                                 REPORT_REGISTRY );
    }

}
