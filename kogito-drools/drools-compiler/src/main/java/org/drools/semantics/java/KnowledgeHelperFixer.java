package org.drools.semantics.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KnowledgeHelperFixer {

    static String KNOWLEDGE_HELPER_PFX = ""; //could also be: "drools\\." for "classic" mode.
    static Pattern MODIFY = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "modify\\s*\\(([^)]+)\\)(.*)", Pattern.DOTALL);
    static Pattern ASSERT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "assert\\s*\\(([^)]+)\\)(.*)", Pattern.DOTALL);
    static Pattern RETRACT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "retract\\s*\\(([^)]+)\\)(.*)", Pattern.DOTALL);   
    
    /**
     * This takes a raw consequence, and fixes up the KnowledegeHelper references 
     * to be what SMF requires.
     *
     * eg: modify( myObject ); --> drools.modify( myObjectHandle, myObject );
     * 
     * (can adjust the PREFIX if needed).
     * 
     * Uses some non-tail recursion to ensure that all parts are "expanded". 
     */    
    public String fix(String raw) {
        String result = fix(raw, MODIFY, "modifyObject");
        result = fix(result, ASSERT, "assertObject");
        result = fix(result, RETRACT, "retractObject");
        return result;
    }
    

    /**
     * Recursively apply the pattern, replace the guts of what is matched.
     */
    public String fix(String raw, Pattern pattern, String action) {
        if (raw == null) return null;
        Matcher matcher = pattern.matcher(raw);
        
        if (matcher.matches()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                pre = fix(pre, pattern, action);
            }
            String obj = matcher.group(2).trim();
            String post = matcher.group(3);
            if (post != null) {
                post = fix(post, pattern, action);
            }
            
            return pre + matcher.replaceAll("drools." + action + "(" + obj + "__Handle__, " + obj + ")") + post;
        }
        return raw;
    }
}
