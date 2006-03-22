package org.drools.semantics.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionFixer {
    
    static Pattern FUNCTION = Pattern.compile("(.*)\\b([^.][\\S]+)\\s*\\(([^)]*)\\)(.*)", Pattern.DOTALL);
            
    public String fix(String raw) {        
        return fix(raw, FUNCTION);
    }
    
    public String fix(String raw, Pattern pattern) {
        if (raw == null) return null;
        Matcher matcher = pattern.matcher(raw);
        
        if (matcher.matches()) {
            String pre = matcher.group(1);
            if (matcher.group(1) != null) {
                pre = fix(pre, pattern);
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
