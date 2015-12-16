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

/*
 * $Id: JSR94TestBase.java,v 1.7 2004/11/17 03:09:50 dbarnett Exp $
 *
 * Copyright 2002-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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
