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
 * 
 * @author Michael Neale
 *
 */
public interface Expander {

    /**
     * The parser should call this on an expression/line that potentially needs expanding 
     * BEFORE it parses that line (as the line may change radically as the result of expansion).
     * 
     * Expands the expression Just-In-Time for the parser.
     * If the expression is not meant to be expanded, or if no
     * appropriate expander is found, it will echo back the same 
     * expression.
     * 
     * @param scope The current scope of the expansion (eg "when" for LHS)
     * @param expression The line of text to be expanded.
     * @return A correct expression for the parser to reparse.
     */
    public String expand(String scope, String pattern);
    
    
}