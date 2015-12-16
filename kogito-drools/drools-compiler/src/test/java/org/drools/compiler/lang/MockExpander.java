/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.dsl.DSLMapping;

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

    public void addDSLMapping(final DSLMapping mapping) {
        // TODO Auto-generated method stub

    }

    public String expand(final Reader drl) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public String expand(final String source) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getErrors() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasErrors() {
        // TODO Auto-generated method stub
        return false;
    }

}
