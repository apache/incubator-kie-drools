package org.drools.lang.dsl;

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

import java.io.Reader;

import org.drools.lang.Expander;
import org.drools.lang.dsl.template.NLExpressionCompiler;
import org.drools.lang.dsl.template.NLGrammar;

/** 
 * The default expander uses String templates to provide pseudo natural language,
 * as well as general DSLs.
 * 
 * For most people, this should do the job just fine. 
 */
public class DefaultExpander
    implements
    Expander {

    private NLExpressionCompiler compiler;

    public String expand(final String scope,
                         final String pattern) {

        return this.compiler.compile( pattern,
                                 scope );
    }

    /**
     * Use {0} style notation to place "holes" where data will be parsed from the natural text input.
     * 
     * @see org.drools.lang.dsl.template.NLExpressionCompiler for details.
     */
    public DefaultExpander(final Reader reader) {
        final NLGrammar grammar = new NLGrammar();
        grammar.load( reader );
        this.compiler = new NLExpressionCompiler( grammar );
    }

}