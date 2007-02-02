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

    private static final long    serialVersionUID = 7185580607729787497L;
    private static final Pattern tokenPattern     = Pattern.compile( "\\{(\\w*)\\}" );
    private static final Pattern invalidPattern1  = Pattern.compile( "\\{\\w*(\\z|[^\\}\\w])" );
    private static final Pattern invalidPattern2  = Pattern.compile( "[^\\{\\w]\\w*\\}" );

    private String               naturalTemplate;
    private String               targetTemplate;
    private String               scope;
    private String               objectName;

    public void setObjectName(String name) {
        this.objectName = name;
    }
    
    public void setNaturalTemplate(final String naturalTemplate) {
        this.naturalTemplate = naturalTemplate.replaceAll( "\\s*,\\s*",
                                                           " , " );
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public void setTargetTemplate(final String targetTemplate) {
        this.targetTemplate = targetTemplate;
    }

    public NLMappingItem(final String naturalTemplate,
                         final String targetTemplate,
                         final String scope,
                         final String objectName) {
        this.setNaturalTemplate( naturalTemplate );
        this.setTargetTemplate( targetTemplate );
        this.setScope( scope );
        this.setObjectName(objectName);
    }

    public NLMappingItem(final String naturalTemplate,
            final String targetTemplate,
            final String scope) {
    	this.setNaturalTemplate( naturalTemplate );
    	this.setTargetTemplate( targetTemplate );
    	this.setScope( scope );
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public String getNaturalTemplate() {
        return this.naturalTemplate;
    }

    public String getTargetTemplate() {
        return this.targetTemplate;
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
        final NLMappingItem item = this;
        final List result = new ArrayList();
        final Matcher natural = NLMappingItem.tokenPattern.matcher( item.getNaturalTemplate() );
        final Matcher target = NLMappingItem.tokenPattern.matcher( item.getTargetTemplate() );
        final Set naturalSet = new HashSet();
        final Set targetSet = new HashSet();
        while ( natural.find() ) {
            naturalSet.add( natural.group() );
        }
        while ( target.find() ) {
            targetSet.add( target.group() );
        }
        if ( !naturalSet.equals( targetSet ) ) {
            final Set aux = new HashSet( naturalSet );
            naturalSet.removeAll( targetSet );
            targetSet.removeAll( aux );

            for ( final Iterator i = naturalSet.iterator(); i.hasNext(); ) {
                final String token = (String) i.next();
                result.add( new MappingError( MappingError.ERROR_UNUSED_TOKEN,
                                              MappingError.TEMPLATE_NATURAL,
                                              item.getNaturalTemplate().indexOf( token ),
                                              token,
                                              this.naturalTemplate ) );
            }
            for ( final Iterator i = targetSet.iterator(); i.hasNext(); ) {
                final String token = (String) i.next();
                result.add( new MappingError( MappingError.ERROR_UNDECLARED_TOKEN,
                                              MappingError.TEMPLATE_TARGET,
                                              item.getTargetTemplate().indexOf( token ),
                                              token,
                                              this.naturalTemplate ) );
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
        final NLMappingItem item = this;
        final List result = new ArrayList();
        final Matcher natural1 = NLMappingItem.invalidPattern1.matcher( item.getNaturalTemplate() );
        final Matcher natural2 = NLMappingItem.invalidPattern2.matcher( item.getNaturalTemplate() );
        final Matcher target1 = NLMappingItem.invalidPattern1.matcher( item.getTargetTemplate() );
        final Matcher target2 = NLMappingItem.invalidPattern2.matcher( item.getTargetTemplate() );

        while ( natural1.find() ) {
            final String token = natural1.group();
            result.add( new MappingError( MappingError.ERROR_INVALID_TOKEN,
                                          MappingError.TEMPLATE_NATURAL,
                                          natural1.start(),
                                          token,
                                          this.naturalTemplate ) );
        }

        while ( natural2.find() ) {
            final String token = natural2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_NATURAL,
                                          natural2.start(),
                                          token,
                                          this.naturalTemplate ) );
        }

        while ( target1.find() ) {
            final String token = target1.group();
            result.add( new MappingError( MappingError.ERROR_INVALID_TOKEN,
                                          MappingError.TEMPLATE_TARGET,
                                          target1.start(),
                                          token,
                                          this.naturalTemplate ) );
        }

        while ( target2.find() ) {
            final String token = target2.group();
            result.add( new MappingError( MappingError.ERROR_UNMATCHED_BRACES,
                                          MappingError.TEMPLATE_TARGET,
                                          target2.start(),
                                          token,
                                          this.naturalTemplate ) );
        }

        return result;
    }

}