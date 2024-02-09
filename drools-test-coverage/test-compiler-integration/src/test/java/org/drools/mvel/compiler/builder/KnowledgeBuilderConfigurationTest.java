/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.builder;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.core.base.accumulators.MaxAccumulateFunction;
import org.drools.mvel.evaluators.AfterEvaluatorDefinition;
import org.drools.mvel.evaluators.BeforeEvaluatorDefinition;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.AccumulateFunctionOption;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.DefaultPackageNameOption;
import org.kie.internal.builder.conf.DumpDirOption;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.ProcessStringEscapesOption;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBuilderConfigurationTest {

    private KnowledgeBuilderConfiguration config;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
    }

    @Test
    public void testDefaultDialectConfiguration() {
        // setting the default dialect using the type safe method
        config.setOption( DefaultDialectOption.get( "mvel" ) );

        // checking the type safe getOption() method
        assertThat(config.getOption(DefaultDialectOption.KEY)).isEqualTo(DefaultDialectOption.get("mvel"));
        // checking string conversion
        assertThat(config.getOption(DefaultDialectOption.KEY).dialectName()).isEqualTo("mvel");
        // checking the string based getProperty() method
        assertThat(config.getProperty(DefaultDialectOption.PROPERTY_NAME)).isEqualTo("mvel");

        // setting the default dialect using the string based setProperty() method
        config.setProperty( DefaultDialectOption.PROPERTY_NAME,
                            "java" );

        // checking the type safe getOption() method
        assertThat(config.getOption(DefaultDialectOption.KEY)).isEqualTo(DefaultDialectOption.get("java"));
        assertThat(config.getOption(DefaultDialectOption.KEY).toString()).isEqualTo("DefaultDialectOption( name=java )");
        // checking string conversion
        assertThat(config.getOption(DefaultDialectOption.KEY).dialectName()).isEqualTo("java");
        // checking the string based getProperty() method
        assertThat(config.getProperty(DefaultDialectOption.PROPERTY_NAME)).isEqualTo("java");
    }
    
    @Test
    public void testLanguageLevelConfiguration() {
        // setting the language level using the type safe method
        config.setOption( LanguageLevelOption.DRL5 );

        // checking the type safe getOption() method
        assertThat(config.getOption(LanguageLevelOption.KEY)).isEqualTo(LanguageLevelOption.DRL5);
        // checking string conversion
        assertThat(config.getOption(LanguageLevelOption.KEY)).isEqualTo(LanguageLevelOption.DRL5);
        // checking the string based getProperty() method
        assertThat(config.getProperty(LanguageLevelOption.PROPERTY_NAME)).isEqualTo("DRL5");

        // setting the default dialect using the string based setProperty() method
        config.setProperty( LanguageLevelOption.PROPERTY_NAME,
                            "DRL6" );

        // checking the type safe getOption() method
        assertThat(config.getOption(LanguageLevelOption.KEY)).isEqualTo(LanguageLevelOption.DRL6);
        assertThat(config.getOption(LanguageLevelOption.KEY).toString()).isEqualTo("DRL6");
        // checking string conversion
        assertThat(config.getOption(LanguageLevelOption.KEY)).isEqualTo(LanguageLevelOption.DRL6);
        // checking the string based getProperty() method
        assertThat(config.getProperty(LanguageLevelOption.PROPERTY_NAME)).isEqualTo("DRL6");
    }

    @Test
    public void testAccumulateFunctionConfiguration() {
        Set<String> keySet = new HashSet<String>();
        // in this use case, the application already has the instance of the accumulate function
        AccumulateFunction function = new AverageAccumulateFunction();
        
        // creating the option and storing in a local var just to make test easier
        AccumulateFunctionOption option = AccumulateFunctionOption.get( "avg", function );
        
        // wiring the accumulate function using the type safe method
        config.setOption( option );

        // checking the type safe getOption() method
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "avg")).isEqualTo(option);
        // checking string conversion
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "avg").getName()).isEqualTo("avg");
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "avg").getFunction()).isEqualTo(function);
        // checking the string based getProperty() method
        assertThat(config.getProperty(AccumulateFunctionOption.PROPERTY_NAME + "avg")).isEqualTo(AverageAccumulateFunction.class.getName());
        // check the key set
        keySet.add( "avg" );
        assertThat(config.getOptionSubKeys(AccumulateFunctionOption.KEY).contains("avg")).isTrue();

        // wiring the accumulate function using the string based setProperty() method
        config.setProperty( AccumulateFunctionOption.PROPERTY_NAME+"maximum",
                            MaxAccumulateFunction.class.getName() );
        
        MaxAccumulateFunction max = new MaxAccumulateFunction();
        // checking the type safe getOption() method
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "maximum")).isEqualTo(AccumulateFunctionOption.get("maximum", max));
        // checking string conversion
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "maximum").getName()).isEqualTo("maximum");
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "maximum").getFunction().getClass().getName()).isEqualTo(max.getClass().getName());
        // checking the string based getProperty() method
        assertThat(config.getProperty(AccumulateFunctionOption.PROPERTY_NAME + "maximum")).isEqualTo(MaxAccumulateFunction.class.getName());
        keySet.add( "avg" );
        
        // wiring the inner class accumulate function using the string based setProperty() method
        config.setProperty( AccumulateFunctionOption.PROPERTY_NAME+"inner",
                            InnerAccumulateFunction.class.getName());
        
        InnerAccumulateFunction inner = new InnerAccumulateFunction();
        // checking the type safe getOption() method
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "inner")).isEqualTo(AccumulateFunctionOption.get("inner", inner));
        // checking string conversion
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "inner").getName()).isEqualTo("inner");
        assertThat(config.getOption(AccumulateFunctionOption.KEY, "inner").getFunction().getClass().getName()).isEqualTo(inner.getClass().getName());
        // checking the string based getProperty() method
        assertThat(config.getProperty(AccumulateFunctionOption.PROPERTY_NAME + "inner")).isEqualTo(InnerAccumulateFunction.class.getName());
        keySet.add( "avg" );

        assertThat(config.getOptionSubKeys(AccumulateFunctionOption.KEY).containsAll(keySet)).isTrue();
//        for( String key: config.getOptionKeys(AccumulateFunctionOption.KEY ) ){
//            System.out.println( key + "->" + config.getOption(AccumulateFunctionOption.KEY, key).getClass().getName() );
//        }
    }
    
    @Test
    public void testDumpDirectoryConfiguration() {
        File dumpDir = new File("target");
        // setting the dump directory using the type safe method
        config.setOption( DumpDirOption.get( dumpDir ) );

        // checking the type safe getOption() method
        assertThat(config.getOption(DumpDirOption.KEY)).isEqualTo(DumpDirOption.get(dumpDir));
        // checking string conversion
        assertThat(config.getOption(DumpDirOption.KEY).getDirectory()).isEqualTo(dumpDir);
        // checking the string based getProperty() method
        assertThat(config.getProperty(DumpDirOption.PROPERTY_NAME)).isEqualTo(dumpDir.toString());

        // setting the dump dir using the string based setProperty() method
        dumpDir = new File( System.getProperty( "java.io.tmpdir" ) );
        config.setProperty( DumpDirOption.PROPERTY_NAME,
                            System.getProperty( "java.io.tmpdir" ) );

        // checking the type safe getOption() method
        assertThat(config.getOption(DumpDirOption.KEY)).isEqualTo(DumpDirOption.get(dumpDir));
        // checking string conversion
        assertThat(config.getOption(DumpDirOption.KEY).getDirectory()).isEqualTo(dumpDir);
        // checking the string based getProperty() method
        assertThat(config.getProperty(DumpDirOption.PROPERTY_NAME)).isEqualTo(dumpDir.toString());
    }
    
    @Test
    public void testEvaluatorConfiguration() {
        // in this use case, the application already has the instance of the evaluator definition
        EvaluatorDefinition afterDef = new AfterEvaluatorDefinition();
        assertThat(afterDef).isNotNull();
        
        // creating the option and storing in a local var just to make test easier
        EvaluatorOption option = EvaluatorOption.get( "after", afterDef );
        
        // wiring the evaluator definition using the type safe method
        config.setOption( option );

        // checking the type safe getOption() method
        assertThat(config.getOption(EvaluatorOption.KEY, "after")).isEqualTo(option);
        // checking string conversion
        assertThat(config.getOption(EvaluatorOption.KEY, "after").getName()).isEqualTo("after");
        assertThat(config.getOption(EvaluatorOption.KEY, "after").getEvaluatorDefinition()).isEqualTo(afterDef);
        // checking the string based getProperty() method
        assertThat(config.getProperty(EvaluatorOption.PROPERTY_NAME + "after")).isEqualTo(AfterEvaluatorDefinition.class.getName());

        // wiring the evaluator definition using the string based setProperty() method
        config.setProperty( EvaluatorOption.PROPERTY_NAME+"before",
                            BeforeEvaluatorDefinition.class.getName() );
        
        BeforeEvaluatorDefinition beforeDef = new BeforeEvaluatorDefinition();
        // checking the type safe getOption() method
        assertThat(config.getOption(EvaluatorOption.KEY, "before")).isEqualTo(EvaluatorOption.get("before", beforeDef));
        // checking string conversion
        assertThat(config.getOption(EvaluatorOption.KEY, "before").getName()).isEqualTo("before");
        assertThat(config.getOption(EvaluatorOption.KEY, "before").getEvaluatorDefinition().getClass().getName()).isEqualTo(beforeDef.getClass().getName());
        // checking the string based getProperty() method
        assertThat(config.getProperty(EvaluatorOption.PROPERTY_NAME + "before")).isEqualTo(beforeDef.getClass().getName());
    }
    
    @Test
    public void testProcessStringEscapesConfiguration() {
        // setting the process string escapes option using the type safe method
        config.setOption( ProcessStringEscapesOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(ProcessStringEscapesOption.KEY)).isEqualTo(ProcessStringEscapesOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ProcessStringEscapesOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the default dialect using the string based setProperty() method
        config.setProperty( ProcessStringEscapesOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(ProcessStringEscapesOption.KEY)).isEqualTo(ProcessStringEscapesOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ProcessStringEscapesOption.PROPERTY_NAME)).isEqualTo("false");
    }

    @Test
    public void testDefaultPackageNameConfiguration() {
        // setting the default dialect using the type safe method
        config.setOption( DefaultPackageNameOption.get( "org.drools.mvel.compiler.test" ) );

        // checking the type safe getOption() method
        assertThat(config.getOption(DefaultPackageNameOption.KEY)).isEqualTo(DefaultPackageNameOption.get("org.drools.mvel.compiler.test"));
        // checking string conversion
        assertThat(config.getOption(DefaultPackageNameOption.KEY).getPackageName()).isEqualTo("org.drools.mvel.compiler.test");
        // checking the string based getProperty() method
        assertThat(config.getProperty(DefaultPackageNameOption.PROPERTY_NAME)).isEqualTo("org.drools.mvel.compiler.test");

        // setting the default dialect using the string based setProperty() method
        config.setProperty( DefaultPackageNameOption.PROPERTY_NAME,
                            "org.drools" );

        // checking the type safe getOption() method
        assertThat(config.getOption(DefaultPackageNameOption.KEY)).isEqualTo(DefaultPackageNameOption.get("org.drools"));
        // checking string conversion
        assertThat(config.getOption(DefaultPackageNameOption.KEY).getPackageName()).isEqualTo("org.drools");
        // checking the string based getProperty() method
        assertThat(config.getProperty(DefaultPackageNameOption.PROPERTY_NAME)).isEqualTo("org.drools");
    }
    
    /**
     * an accumulate function implemented as an inner class
     */
    public static class InnerAccumulateFunction implements AccumulateFunction {

        public void accumulate(Serializable context,
                               Object value) {
        }
        public Serializable createContext() {
            return null;
        }
        public Object getResult(Serializable context) throws Exception {
            return null;
        }
        public void init(Serializable context) throws Exception {
        }
        public void reverse(Serializable context,
                            Object value) throws Exception {
        }
        public boolean supportsReverse() {
            return false;
        }
        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            // TODO Auto-generated method stub

        }
        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }
        @Override
        public Class< ? > getResultType() {
            return Object.class;
        }
    }

}
