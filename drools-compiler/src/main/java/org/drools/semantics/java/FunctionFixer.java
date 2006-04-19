package org.drools.semantics.java;
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



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionFixer {
    
    static Pattern FUNCTION = Pattern.compile("(\\S*\\s*|\\.\\s*)\\b([\\S&&[^\\.]]+)\\s*\\(([^)]*)\\)", Pattern.DOTALL);
            
    public String fix(String raw) {
        //return raw;
        return fix(raw, FUNCTION);
    }
    
    public String fix(String raw, Pattern pattern) {
        if (raw == null) return null;
        StringBuffer buf = new StringBuffer();
        int lastIndex = 0;
        
        Matcher matcher = pattern.matcher(raw);
        
        while(matcher.find()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                String trimmedPre = pre.trim();
                if (trimmedPre.endsWith( "." ) || trimmedPre.endsWith( "new" )) {
                    //leave alone
                    continue;
                 }
            }
            String function = matcher.group(2).trim();
            
            String params = matcher.group(3).trim();
            
            String target = ucFirst(function) + "." + function + "(" + params + ")";
            
            buf.append( raw.substring( lastIndex, matcher.start( 2 ) ) );
            buf.append( KnowledgeHelperFixer.replace( target, "$", "\\$", 128 ) );
            lastIndex = matcher.end();
        }
        buf.append( raw.substring( lastIndex ) );
        return buf.toString();
    }
    
    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }    
}