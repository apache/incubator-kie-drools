package org.drools.lang;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.HashSet;
import java.util.Set;

public class MockExpander
    implements
    Expander {

    private int timesCalled = 0;
    public Set  patterns    = new HashSet();

    public String expand(final String scope,
                         final String pattern) {

        this.patterns.add( scope + "," + pattern );

        final int grist = (++this.timesCalled);
        return "foo" + grist + " : Bar(a==" + grist + ")";
    }

    public boolean checkPattern(final String pat) {
        return this.patterns.contains( pat );
    }

}