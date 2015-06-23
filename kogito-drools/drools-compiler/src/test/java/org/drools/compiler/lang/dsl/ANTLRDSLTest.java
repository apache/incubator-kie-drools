/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang.dsl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import org.drools.compiler.lang.dsl.DSLMappingEntry;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.junit.Test;

public class ANTLRDSLTest {

    @Test
    public void testMe() throws Exception{
        DSLTokenizedMappingFile tokenizedFile = null;
        final String filename = "test_antlr.dsl";
        final Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( filename ) );
        tokenizedFile = new DSLTokenizedMappingFile();
        tokenizedFile.parseAndLoad( reader );
        reader.close();
        for (Iterator it = tokenizedFile.getMapping().getEntries().iterator(); it.hasNext();) {
            DSLMappingEntry entry = (DSLMappingEntry) it.next();
//            System.out.println("ENTRY: " + entry.getKeyPattern() + "   :::::   " + entry.getValuePattern());
        }
        
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping( tokenizedFile.getMapping() );
        
        System.err.println(ex.expand( "rule 'x' \n when \n address is present where name is \"foo\" and age is \"32\" \n then \n end" ));
    }

    @Test
    public void testSimple() throws Exception{
        String input = "u : User() and exists (a: Address( where name is \"foo\" and age is \"32\" ) from u.addresses)";
        String pattern = "(\\W|^)where\\s+([\\S]+)\\s+is \"(.*?)\"(\\W|$)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(input);
        System.out.println("SIMPLE MATCHER matches: " + m.matches());
    }

}
