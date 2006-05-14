package org.drools.lang.dsl.template;
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



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This contains a single mapping from psuedo NL to a grammarTemplate.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class NLMappingItem
    implements
    Serializable {


    private static final long serialVersionUID = 7185580607729787497L;
    private static final Pattern tokenPattern = Pattern.compile( "\\{(\\w*)\\}" ); 
    private static final Pattern invalidPattern1 = Pattern.compile( "\\{\\w*(\\z|[^\\}\\w])" );
    private static final Pattern invalidPattern2 = Pattern.compile( "[^\\{\\w]\\w*\\}" );

    
    private String naturalTemplate;
    private String targetTemplate;
    private String scope;
    

    
    public void setNaturalTemplate(String naturalTemplate) {
        this.naturalTemplate = naturalTemplate.replaceAll( "\\s*,\\s*", " , " );
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTargetTemplate(String targetTemplate) {
        this.targetTemplate = targetTemplate;
    }

    public NLMappingItem(String naturalTemplate,
                         String targetTemplate,
                         String scope) {
        this.setNaturalTemplate( naturalTemplate );
        this.setTargetTemplate( targetTemplate );        
        this.setScope( scope );        
    }
    
    public String getNaturalTemplate() {
        return naturalTemplate;
    }

    public String getTargetTemplate() {
        return targetTemplate;
    }


    public String getScope() {
        return this.scope;
    }

    /**
     * Checks for tokens declared in the natural expression but not used in the 
     * mapping and tokens used in the mapping but not declared in the natural 
     * expression
     * 
     * @param item
     * @return
     */
    public List validateTokenUsage() {
        NLMappingItem item = this;
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
                                             token, this.naturalTemplate) );
            }
            for(Iterator i = targetSet.iterator(); i.hasNext() ; ) {
                String token = (String) i.next();
                result.add( new MappingError(MappingError.ERROR_UNDECLARED_TOKEN,
                                             MappingError.TEMPLATE_TARGET,
                                             item.getTargetTemplate().indexOf( token ),
                                             token, this.naturalTemplate ) );
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
    public List validateUnmatchingBraces() {
        NLMappingItem item = this;
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
                                          token, this.naturalTemplate));
        }
        
        while(natural2.find()) {
            String token = natural2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_NATURAL,
                                          natural2.start(),
                                          token, this.naturalTemplate));
        }
        
        while(target1.find()) {
            String token = target1.group();
            result.add( new MappingError( MappingError.ERROR_INVALID_TOKEN,
                                          MappingError.TEMPLATE_TARGET,
                                          target1.start(),
                                          token, this.naturalTemplate));
        }
        
        while(target2.find()) {
            String token = target2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_TARGET,
                                          target2.start(),
                                          token, this.naturalTemplate));
        }
        
        return result;
    }    
    
}