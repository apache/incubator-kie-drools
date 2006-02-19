package org.drools.lang.dsl.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This represents a simple grammar mapping.
 * Order of operations is as stored in the list. 
 * Global expressions are processed first, followed by condition or consequence scoped ones. */
public class NLGrammar
    implements
    Serializable {

    
    private static final long serialVersionUID = 1L;
    private List mappings = new ArrayList();
    private static final Pattern itemPrefix = Pattern.compile( "\\[\\s*(when|then)\\s*\\].*" );
    
    public NLGrammar() {
    }
    


    public void addNLItem(NLMappingItem item) {
        this.mappings.add(item);
    }
    
    public List getMappings() {        
        return mappings;
    }

    /** 
     * This will load from a reader to an appropriate text DSL config.
     * It it roughly equivalent to a properties file.
     * (you can use a properties file actually), 
     * 
     * But you can also prefix it with things like:
     * 
     * [XXX]Expression=Target
     * 
     * Where XXX is either "when" or "then" which indicates which part of the rule the
     * item relates to.
     *
     * If the "XXX" part if left out, then it will apply to the whole rule when looking for a 
     * match to expand.
     */
    public void load(InputStreamReader reader) {
        BufferedReader buf = new BufferedReader(reader);
        try {
            String line = null;
            while ((line = buf.readLine())  != null) {
                while (line.endsWith( "\\") ) {
                    line = line.substring( 0, line.length() - 1 ) + buf.readLine();
                }
                line = line.trim();
                if (line.equals( "" ) || line.startsWith( "#" )) {
                    //ignore comment
                } else {                        
                    this.mappings.add( parseLine( line ) );
                }
            }
            
        } catch ( IOException e ) {
            throw new IllegalArgumentException("Unable to read DSL configuration.", e);
        }
    }
    
    /**
     * Filter the items for the appropriate scope.
     * Will include global ones.
     */
    public List getMappings(String scope) {
        List list = new ArrayList();
        for ( Iterator iter = mappings.iterator(); iter.hasNext(); ) {
            NLMappingItem item = (NLMappingItem) iter.next();
            if (item.getScope().equals( "*" ) || item.getScope().equals( scope )) {
                list.add( item );
            }
        }
        return list;
    }

    /**
     * This will parse a line into a NLMapping item.
     */
    public NLMappingItem parseLine(String line) {
        int split = line.indexOf( "=" );
        String left = line.substring( 0, split ).trim();
        String right = line.substring( split + 1 ).trim();
        
        left = StringUtils.replace( left, "\\", "" );
        
        Matcher matcher = itemPrefix.matcher( left );        
        if (matcher.matches()) {
            //get out priority, association
            String type = matcher.group( 1 );
            left = left.substring( left.indexOf( "]" ) + 1 ).trim();
            return new NLMappingItem(left, right, type);
            
            
        } else {
            return new NLMappingItem(left, right, "*");
            
        }
    }
    
    
}
