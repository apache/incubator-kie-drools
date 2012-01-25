/*
 * Copyright 2011 JBoss Inc
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

package org.drools.fluent.impl;

import org.drools.fluent.InternalSimulation;
import org.drools.fluent.test.ReflectiveMatcherAssert;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertThat;

@Ignore
public class AbstractFluentTest<P> {
/*
    private InternalSimulation sim;
    
    public AbstractFluentTest() {
    }
      
    public InternalSimulation getSim() {
        return sim;
    }

    public void setSim(InternalSimulation sim) {
        this.sim = sim;
    }

    public <T> P test(String reason,
                      T actual,
                      Matcher<T> matcher) {
        assertThat(reason, actual, matcher);
        return (P) this;
    }

    public <T> P test(T actual,
                      Matcher<T> matcher) {
        assertThat( actual, matcher);
        return (P) this;
    }

    public <T> P test(String text) {
        MVELTestCommand testCmd = new MVELTestCommand();
        testCmd.setText( text );
        
        sim.addCommand( testCmd );
        return (P) this;
    }

    public <T> P test(ReflectiveMatcherAssert matcherAssert) {
        ReflectiveMatcherAssertCommand matcherCmd = new ReflectiveMatcherAssertCommand( matcherAssert );

        sim.addCommand( matcherCmd );
        return (P) this;
    }
*/
}
