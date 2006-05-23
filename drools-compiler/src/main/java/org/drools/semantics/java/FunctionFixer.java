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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This horrific utility adds in the function class name (which is the same as the functions method name)
 * into the RHS guts of a rule. It has to tip toe around method calls, new declarations and other 
 * stuff like that.
 * A better future solution is to use a static import as found in Java 5, then ALL THIS can 
 * disappear. Oh Happy day.
 * @author Michael Neale (sadly..)
 *
 */
public class FunctionFixer {
    
    static Pattern FUNCTION = Pattern.compile("(\\S*\\s*|\\.\\s*)\\b([\\S&&[^\\.]]+)\\s*\\(([^)]*)\\)", Pattern.DOTALL);
    static final Set KEYWORDS = getJavaKeywords();
    public String fix(String raw) {
        //return raw;
        return fix(raw, FUNCTION);
    }
    
    public String fix(String raw, Pattern pattern) {
        if (raw == null) return null;
        StringBuffer buf = new StringBuffer();
        int startIndex = 0, lastIndex = 0;
        
            Matcher matcher = pattern.matcher(raw);
            while (matcher.find(startIndex)) {
                  startIndex = getStartIndex(matcher);
                  
                String pre = matcher.group(1);
                if (matcher.group(1) != null) {
                    String trimmedPre = pre.trim();
                    if (trimmedPre.endsWith( "." ) || trimmedPre.endsWith( "new" )) {
                        //leave alone
                        continue;
                     }
                }
                String function = matcher.group(2).trim();
                //if we have a reserve  d work, DO NOT TOUCH !
                if (KEYWORDS.contains( function )) continue;
                
                String params = matcher.group(3).trim();
                
                String target = ucFirst(function) + "." + function + "(" + params + ")";
                
                buf.append( raw.substring( lastIndex, matcher.start( 2 ) ) );
                buf.append( target );
  
                lastIndex = matcher.end();
            }

        buf.append( raw.substring( lastIndex ) );
        return buf.toString();
    }
    
    private int getStartIndex(Matcher matcher) {
        return matcher.start(3) <= 0 ? matcher.end() + 1 : matcher.start(3); 
    }
    
    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }    
    
    /**
     * This list was obtained from http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
     */
    private static Set getJavaKeywords() {
        Set keys = new HashSet();
        keys.add("abstract");   
        keys.add("continue");   
        keys.add("for");
        keys.add("new");
        keys.add("switch");
        keys.add("assert"); 
        keys.add("default");    
        keys.add("goto");
        keys.add("package");
        keys.add("synchronized");
        keys.add("boolean");
        keys.add("do");
        keys.add("if");
        keys.add("private");
        keys.add("this");
        keys.add("break");
        keys.add("double");     
        keys.add("implements");     
        keys.add("protected");  
        keys.add("throw");
        keys.add("byte");   
        keys.add("else");   
        keys.add("import");     
        keys.add("public");     
        keys.add("throws");
        keys.add("case");   
        keys.add("enum");   
        keys.add("instanceof");     
        keys.add("return");     
        keys.add("transient");
        keys.add("catch");  
        keys.add("extends");    
        keys.add("int");    
        keys.add("short");  
        keys.add("try");
        keys.add("char");   
        keys.add("final");  
        keys.add("interface");  
        keys.add("static");     
        keys.add("void");
        keys.add("class");  
        keys.add("finally");    
        keys.add("long");   
        keys.add("strictfp");   
        keys.add("volatile");
        keys.add("const");  
        keys.add("float");  
        keys.add("native");     
        keys.add("super");  
        keys.add("while");
        return keys;
    }
    
}