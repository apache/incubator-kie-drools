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

import java.io.InputStream;
import java.net.URL;

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
public abstract class JSR94TestBase {
    protected StatefulRuleSession     statefulSession;

    protected StatelessRuleSession    statelessSession;

    protected ExampleRuleEngineFacade engine;

    protected String                  bindUri = "sisters.drl";

    protected RuleServiceProvider     ruleServiceProvider;

    /**
     * Setup the test case.
     */
    @Before
    public void setUp() throws Exception {
        this.engine = new ExampleRuleEngineFacade();
        this.engine.addRuleExecutionSet( this.bindUri,
                                         StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri ) );

        this.ruleServiceProvider = this.engine.getRuleServiceProvider();
        this.statelessSession = this.engine.getStatelessRuleSession( this.bindUri );
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );
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
