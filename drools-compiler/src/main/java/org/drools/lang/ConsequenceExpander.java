package org.drools.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a utility class for dealing with consequence block specific stuff.
 * It is pre compilation thing.
 * 
 * This should be used before and non built in expanders are called.
 * 
 * @author Michael Neale
 */
class ConsequenceExpander implements Expander {

    static String KNOWLEDGE_HELPER_PFX = ""; //could also be: "drools\\." for "classic" mode.
    static Pattern MODIFY = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "modify\\s*\\(([^\\)]+)\\)(.*)");
    static Pattern ASSERT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "assert\\s*\\(([^\\)]+)\\)(.*)");
    static Pattern RETRACT = Pattern.compile("(.*)\\b" + KNOWLEDGE_HELPER_PFX + "retract\\s*\\(([^\\)]+)\\)(.*)");
    
    
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
    String knowledgeHelperFixer(String raw) {
        String result = knowledgeHelperFixer(raw, MODIFY, "modify");
        result = knowledgeHelperFixer(result, ASSERT, "assert");
        result = knowledgeHelperFixer(result, RETRACT, "retract");
        return result;
    }
    

    /**
     * Recursively apply the pattern, replace the guts of what is matched.
     */
    String knowledgeHelperFixer(String raw, Pattern pattern, String action) {
        Matcher matcher = pattern.matcher(raw);
        
        if (matcher.matches()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                pre = knowledgeHelperFixer(pre, pattern, action);
            }
            String obj = matcher.group(2).trim();
            String post = matcher.group(3);
            if (post != null) {
                post = knowledgeHelperFixer(post, pattern, action);
            }
            
            return pre + matcher.replaceAll("drools." + action + "(" + obj + "Handle, " + obj + ")") + post;
        }
        return raw;
    }


    /** Expand out the knowledge helper stuff */
    public String expand(String pattern,
                         RuleParser context) {

        return this.knowledgeHelperFixer(pattern);
        
    }

}
