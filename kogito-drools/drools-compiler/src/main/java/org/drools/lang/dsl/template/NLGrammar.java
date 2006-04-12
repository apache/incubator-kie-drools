package org.drools.lang.dsl.template;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    private static final Pattern tokenPattern = Pattern.compile( "\\{(\\w*)\\}" ); 
    private static final Pattern invalidPattern1 = Pattern.compile( "\\{\\w*(\\z|[^\\}\\w])" );
    private static final Pattern invalidPattern2 = Pattern.compile( "[^\\{\\w]\\w*\\}" );
    
    private String description;

    public NLGrammar() {
    }
    
    public void addNLItem(NLMappingItem item) {
        this.mappings.add(item);
    }
    
    public List getMappings() {        
        return mappings;
    }
    
    /** Get the human readable description of this language definition. This should just be a comment. */
    public String getDescription() {
        return description;
    }

    /** Set the human readable description of this language definition. This should just be a comment. */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Will remove the mapping from the grammar.
     */
    public void removeMapping(NLMappingItem item) {
        this.mappings.remove( item );
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
    public void load(Reader reader) {
        BufferedReader buf = new BufferedReader(reader);
        try {
            String line = null;
            while ((line = buf.readLine())  != null) {
                while (line.endsWith( "\\") ) {
                    line = line.substring( 0, line.length() - 1 ) + buf.readLine();
                }
                line = line.trim();
                if (line.startsWith( "#" )) {
                    this.description = line.substring( 1 );
                } else if (line.equals( "" )) {
                    //ignore
                } else {                        
                    this.mappings.add( parseLine( line ) );
                }
            }
            
        } catch ( IOException e ) {
            throw new IllegalArgumentException("Unable to read DSL configuration.");
        }
    }
    
    /** Save out the grammar configuration */
    public void save(Writer writer) {
        BufferedWriter buffer = new BufferedWriter(writer);
        try {
            buffer.write( "#" + this.description + "\n");
            for ( Iterator iter = this.mappings.iterator(); iter.hasNext(); ) {
                NLMappingItem item = (NLMappingItem) iter.next();
                if (item.getScope().equals( "*" )) {
                    buffer.write( item.getNaturalTemplate() + "=" + item.getTargetTemplate() + "\n");
                } else {
                    buffer.write( "[" + item.getScope() + "]" + item.getNaturalTemplate() + "=" + item.getTargetTemplate() + "\n");
                }
            }
            buffer.flush();
        } catch ( IOException e ) {
            throw new IllegalStateException("Unable to save DSL configuration.");
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
    
    /**
     * Validades the mapping returning a list of errors found 
     * or an empty list in case of no errors
     * 
     * @return a List of MappingError's found or an empty list in case no one was found
     */
    public List validateMapping(NLMappingItem item) {
        List errors = this.validateTokenUsage(item);
        errors.addAll( this.validateUnmatchingBraces( item ) );
        return errors;
    }
    
    
    /**
     * Checks for tokens declared in the natural expression but not used in the 
     * mapping and tokens used in the mapping but not declared in the natural 
     * expression
     * 
     * @param item
     * @return
     */
    private List validateTokenUsage(NLMappingItem item) {
        List result = new ArrayList();
        Matcher natural = tokenPattern.matcher( item.getNaturalTemplate() );
        Matcher target = tokenPattern.matcher( item.getTargetTemplate() );
        Set naturalSet = new HashSet();
        Set targetSet = new HashSet();
        while(natural.find()) {
            naturalSet.add( natural.group() );
        }
        while(target.find()) {
            targetSet.add( target.group() );
        }
        if( ! naturalSet.equals( targetSet )) {
            Set aux = new HashSet(naturalSet);
            naturalSet.removeAll( targetSet );
            targetSet.removeAll( aux );
            
            for(Iterator i = naturalSet.iterator(); i.hasNext() ; ) {
                String token = (String) i.next();
                result.add( new MappingError(MappingError.ERROR_UNUSED_TOKEN,
                                             MappingError.TEMPLATE_NATURAL,
                                             item.getNaturalTemplate().indexOf( token ), 
                                             token) );
            }
            for(Iterator i = targetSet.iterator(); i.hasNext() ; ) {
                String token = (String) i.next();
                result.add( new MappingError(MappingError.ERROR_UNDECLARED_TOKEN,
                                             MappingError.TEMPLATE_TARGET,
                                             item.getTargetTemplate().indexOf( token ),
                                             token ) );
            }
        }
        return result;
    }
    
    /**
     * Checks for unmatched brackets and invalid tokens
     * 
     * @param item
     * @return
     */
    private List validateUnmatchingBraces(NLMappingItem item) {
        List result = new ArrayList();
        Matcher natural1 = invalidPattern1.matcher( item.getNaturalTemplate() );
        Matcher natural2 = invalidPattern2.matcher( item.getNaturalTemplate() );
        Matcher target1  = invalidPattern1.matcher( item.getTargetTemplate() );
        Matcher target2  = invalidPattern2.matcher( item.getTargetTemplate() );
        
        while(natural1.find()) {
            String token = natural1.group();
            result.add( new MappingError( MappingError.ERROR_INVALID_TOKEN,
                                          MappingError.TEMPLATE_NATURAL,
                                          natural1.start(),
                                          token));
        }
        
        while(natural2.find()) {
            String token = natural2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_NATURAL,
                                          natural2.start(),
                                          token));
        }
        
        while(target1.find()) {
            String token = target1.group();
            result.add( new MappingError( MappingError.ERROR_INVALID_TOKEN,
                                          MappingError.TEMPLATE_TARGET,
                                          target1.start(),
                                          token));
        }
        
        while(target2.find()) {
            String token = target2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_TARGET,
                                          target2.start(),
                                          token));
        }
        
        return result;
    }
    
}
