/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
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

package org.drools.jsr94.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.rules.RuleServiceProvider;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Base class for all drools JSR94 test cases.
 */
public abstract class RuleEngineTestBase {
    protected StatefulRuleSession     statefulSession;

    protected StatelessRuleSession    statelessSession;

    protected ExampleRuleEngineFacade engine;

    protected String                  bindUri         = "sisters.drl";
    protected String                  bindUri_drl     = "sisters_expander.drl";
    protected String                  bindUri_dsl     = "sisters_expander.dsl";
    protected String                  bindUri_globals = "sisters_globals.drl";

    protected RuleServiceProvider     ruleServiceProvider;

    /**
     * Setup the test case.
     */
    @Before
    public void setUp() throws Exception {
        this.engine = new ExampleRuleEngineFacade();
        this.engine.addRuleExecutionSet( this.bindUri,
                                         RuleEngineTestBase.class.getResourceAsStream( this.bindUri ) );

        final Map map = new HashMap();
        final Reader reader = new InputStreamReader( RuleEngineTestBase.class.getResourceAsStream( this.bindUri_dsl ) );

        map.put( "dsl",
                 this.getDSLText( reader ).toString() );
        this.engine.addRuleExecutionSet( this.bindUri_drl,
                                         RuleEngineTestBase.class.getResourceAsStream( this.bindUri_drl ),
                                         map );

        this.engine.addRuleExecutionSet( this.bindUri_globals,
                                         RuleEngineTestBase.class.getResourceAsStream( this.bindUri_globals ) );

        this.ruleServiceProvider = this.engine.getRuleServiceProvider();
        //        this.statelessSession = engine.getStatelessRuleSession( bindUri );
        //        this.statefulSession = engine.getStatefulRuleSession( bindUri );

    }

    /*
     * Taken from DRLParser
     */
    private StringBuffer getDSLText(final Reader reader) throws IOException {
        final StringBuffer text = new StringBuffer();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text;
    }

    /**
     * Get the requested resource from the ClassLoader.
     *
     * @see ClassLoader#getResource
     */
    protected URL getResource(final String res) {
        return getClass().getClassLoader().getResource( res );
    }

    /**
     * Get the requested resource from the ClassLoader.
     *
     * @see ClassLoader#getResourceAsStream
     */
    protected InputStream getResourceAsStream(final String res) {
        return getClass().getClassLoader().getResourceAsStream( res );
    }
}
