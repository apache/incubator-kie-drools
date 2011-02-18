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

package org.drools.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.drools.lang.dsl.DSLMapping;

/**
 * Expanders are extension points for expanding 
 * expressions in DRL at parse time.
 * This is just-in-time translation, or macro expansion, or
 * whatever you want.
 * 
 * The important thing is that it happens at the last possible moment, 
 * so any errors in expansion are included in the parsers errors.
 * 
 * Just-in-time expansions may include complex pre-compilers, 
 * or just macros, and everything in between.
 * 
 * Expanders should ideally not make presumptions on any embedded semantic 
 * language. For instance, java aware pre processing should be done in
 * drools-java semantic module, not in the parser itself. Expanders should 
 * be reusable across semantic languages. 
 */
public interface Expander {

    /**
     * Expands (process) the expression Just-In-Time for the parser.
     * If the source is not meant to be expanded, or if no
     * appropriate match was found for expansion, it will echo back 
     * the same expression.
     * 
     * @param drl the source code to be pre-processed
     * @return source code after running pre-processors
     */
    public String expand(Reader drl) throws IOException;

    /**
     * Expands (process) the expression Just-In-Time for the parser.
     * If the source is not meant to be expanded, or if no
     * appropriate match was found for expansion, it will echo back 
     * the same expression.
     * 
     * @param source the source code to be expanded
     * @return source code after running pre-processors
     */
    public String expand(String source);

    /**
     * Add the new mapping to this expander.
     * @param mapping
     */
    public void addDSLMapping(DSLMapping mapping);

    /**
     * Returns the list of errors from the last expansion made
     * @return A list of <code>ExpanderException</code>
     */
    public List<ExpanderException> getErrors();

    /**
     * Returns true in case the last expansion had any errors
     * @return
     */
    public boolean hasErrors();

}
