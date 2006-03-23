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
     * refer to the Replacer implementation classes below for the specific replacement patterns.
     * 
     * (can adjust the PREFIX if needed).
     * 
     * Uses some non-tail recursion to ensure that all parts are "expanded". 
     */    
    public String fix(String raw) {
        String result = fix(raw, ModifyReplacer.INSTANCE);
        result = fix(result, AssertReplacer.INSTANCE);
        result = fix(result, RetractReplacer.INSTANCE);
        return result;
    }
    

    /**
     * Recursively apply the pattern, replace the guts of what is matched.
     */
    public String fix(String raw, Replacer replacer) {
        if (raw == null) return null;
        Matcher matcher = replacer.getPattern().matcher(raw);
        
        if (matcher.matches()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                pre = fix(pre, replacer);
            }
            String obj = matcher.group(2).trim();
            String post = matcher.group(3);
            if (post != null) {
                post = fix(post, replacer);
            }
            
            return pre + matcher.replaceAll(replacer.getReplacement( obj )) + post;
        }
        return raw;
    }

    static interface Replacer {
        Pattern getPattern();
        String getReplacement(String guts);
    }
    
    static class AssertReplacer implements Replacer {
        
        static Replacer INSTANCE = new AssertReplacer();
        
        public Pattern getPattern() {
            return ASSERT;
        }

        public String getReplacement(String guts) {
            return "drools.assertObject(" + guts + ")";
        }
        
    }
    
    static class ModifyReplacer implements Replacer {
        
        static Replacer INSTANCE = new ModifyReplacer();
        
        public Pattern getPattern() {
            return MODIFY;
        }

        public String getReplacement(String guts) {
            return "drools.modifyObject(" + guts.trim() + "__Handle__, " + guts + ")";
        }
        
    }   
    
    static class RetractReplacer implements Replacer {
        
        static Replacer INSTANCE = new RetractReplacer();
        
        public Pattern getPattern() {
            return RETRACT;
        }

        public String getReplacement(String guts) {
            return "drools.retractObject(" + guts.trim() + "__Handle__)";
        }
        
    }        
    
}
