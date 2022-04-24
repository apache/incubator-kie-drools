/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.GenListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.prototype;

public class CompileEvaluateTest {
    private static final Logger LOG = LoggerFactory.getLogger(CompileEvaluateTest.class);
    private static final FEEL feel = FEEL.newInstance();
    private List<FEELEvent> errors;
    private FEELEventListener errorsCountingListener;
    
    static {
        feel.addListener(evt -> { 
            if (evt.getSeverity() == FEELEvent.Severity.ERROR) { 
                LOG.error("{}", evt);
                if ( evt.getSourceException().getCause() != null ) {
                    Throwable c = evt.getSourceException().getCause();
                    while (c != null) {
                        LOG.error(" caused by: {} {}", c.getClass(), c.getMessage() != null ? c.getMessage() : "");
                        c = c.getCause();
                    }
                    LOG.error(" [stacktraces omitted.]");
                }
            } else if (evt.getSeverity() == FEELEvent.Severity.WARN) { 
                LOG.warn("{}", evt);
            }
        } );
    }
    
    @Before
    public void before() {
        errors = new ArrayList<>();
        errorsCountingListener = evt -> { if ( evt.getSeverity() == Severity.ERROR ) { errors.add(evt); } };
        feel.addListener( errorsCountingListener );
    }
    
    @After
    public void after() {
        feel.removeListener( errorsCountingListener );
    }
    
    @Test
    public void test_isDynamicResolution() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "Person List", BuiltInType.LIST);
        
        CompiledExpression compiledExpression = feel.compile( "Person List[My Variable 1 = \"A\"]", ctx );
        
        assertThat(errors).as(errors.toString()).hasSize(0);

        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, ?>> pList = new ArrayList<>();
        inputs.put("Person List", pList);
        pList.add(prototype(entry("Full Name",    "Edson Tirelli"),
                            entry("My Variable 1","A"))
                );
        pList.add(prototype(entry("Full Name",    "Matteo Mortari"),
                            entry("My Variable 1","B"))
                );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        assertThat(result).isInstanceOf(List.class);
        assertThat((List<?>) result).hasSize(1);
        assertThat(((Map<?, ?>) ((List<?>) result).get(0)).get("Full Name")).isEqualTo("Edson Tirelli");
    }        
    
    @Test
    public void test2() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "MyPerson", new MapBackedType().addField( "FullName", BuiltInType.STRING ) );
        
        CompiledExpression compiledExpression = feel.compile( "MyPerson.fullName", ctx );
        
        assertThat(errors).as(errors.toString()).hasSize(1);
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put( "MyPerson", prototype(entry("FullName", "John Doe")) );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        assertThat(result).isNull();
    }

    @Test
    public void test2OK() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "MyPerson", new MapBackedType().addField( "FullName", BuiltInType.STRING ) );
        
        CompiledExpression compiledExpression = feel.compile( "MyPerson.FullName", ctx );
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put( "MyPerson", prototype(entry("FullName", "John Doe")) );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        assertThat(result).isEqualTo("John Doe");
    }
    
    @Test
    public void testHyphenInProperty() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "input", new MapBackedType().addField( "Primary-Key", BuiltInType.STRING ).addField( "Value", BuiltInType.STRING ) );
        CompiledExpression compiledExpression = feel.compile( "input.Primary-Key", ctx );
        assertTrue(errors.isEmpty());
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put( "input", prototype(entry("Primary-Key", "k987")) );
        Object result = feel.evaluate(compiledExpression, inputs);
        assertThat(result).isEqualTo("k987");
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void testHyphenInPropertyOfCollectionForProjection() {
        MapBackedType compositeType = new MapBackedType().addField( "Primary-Key", BuiltInType.STRING ).addField( "Value", BuiltInType.STRING );
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "input", new GenListType(compositeType) );
        CompiledExpression compiledExpression = feel.compile( "input.Primary-Key", ctx );
        assertTrue(errors.isEmpty());
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put( "input", Arrays.asList(prototype(entry("Primary-Key", "k987"))) );
        Object result = feel.evaluate(compiledExpression, inputs);
        assertThat(result).asList().containsExactly("k987");
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void testHyphenInPropertyOfCollectionForAccessor() {
        MapBackedType compositeType = new MapBackedType().addField( "Primary-Key", BuiltInType.STRING ).addField( "Value", BuiltInType.STRING );
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "my input", new GenListType(compositeType) );
        CompiledExpression compiledExpression = feel.compile( "my input[1].Primary-Key", ctx );
        assertTrue(errors.isEmpty());
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put( "my input", Arrays.asList(prototype(entry("Primary-Key", "k987"))) );
        Object result = feel.evaluate(compiledExpression, inputs);
        assertThat(result).isEqualTo("k987");
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void testExternalFnMissingClass() {
        CompiledExpression compiledExpression = feel.compile( "{ maximum : function( v1, v2 ) external { java : { class : \"java.lang.Meth\", method signature: \"max(long,long)\" } }, the max : maximum( 10, 20 ) }.the max", feel.newCompilerContext() );
        Object result = feel.evaluate(compiledExpression, new HashMap<>());
        
        assertThat(errors).anyMatch(fe -> fe.getMessage().contains("java.lang.Meth"));
    }
    
    @Test
    public void testExternalFnMissingMethod() {
        CompiledExpression compiledExpression = feel.compile( "{ maximum : function( v1, v2 ) external { java : { class : \""+Math.class.getCanonicalName()+"\", method signature: \"max(int,long)\" } }, the max : maximum( 10, 20 ) }.the max", feel.newCompilerContext() );
        Object result = feel.evaluate(compiledExpression, new HashMap<>());
        
        assertThat(errors).anyMatch(fe -> fe.getMessage().contains("max(int,int)") && fe.getMessage().contains("max(long,long)"));
    }    
    
    @Test
    public void testExternalFnMissingMethodString() {
        CompiledExpression compiledExpression = feel.compile( "{ fn : function( p1 ) external { java : { class : \""+SomeTestUtilClass.class.getCanonicalName()+"\", method signature: \"greet(String)\" } }, r : fn( \"John Doe\" ) }.r", feel.newCompilerContext() );
        Object result = feel.evaluate(compiledExpression, new HashMap<>());
        
        assertThat(errors).anyMatch(fe -> fe.getMessage().contains("greet(java.lang.String)"));
    }
}
