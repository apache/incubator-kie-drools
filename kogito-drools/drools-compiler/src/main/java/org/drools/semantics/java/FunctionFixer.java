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
    
    static Pattern FUNCTION = Pattern.compile("(.*)\\b([^.][\\S]+)\\s*\\(([^)]*)\\)(.*)", Pattern.DOTALL);
            
    public String fix(String raw) {
        //return raw;
        return fix(raw, FUNCTION);
    }
    
    public String fix(String raw, Pattern pattern) {
        if (raw == null) return null;
        Matcher matcher = pattern.matcher(raw);
        
        if (matcher.matches()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                String trimmedPre = pre.trim();
                if (trimmedPre.endsWith( "." ) || trimmedPre.endsWith( "new" )) {
                    //leave alone
                    return raw;
                 } else {
                     //recurse
                    pre = fix(pre, pattern);
                 }                
                 //pre = fix(pre, pattern);
            }
            
            String function = matcher.group(2).trim();
            
            String params = matcher.group(3).trim();
            
            String post = matcher.group(4);
            if (post != null) {
                post = fix(post);
            }
            
            return pre + matcher.replaceAll(ucFirst(function) + "." + function + "(" + params + ")" ) + post;
        }
        return raw;
    }
    
    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }    
}