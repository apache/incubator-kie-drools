/*
 * Copyright 2010 JBoss Inc
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

package org.drools.builder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceType implements Serializable {

    private String                                 name;

    private String                                 description;
    
    private String                                 defaultExtension;
    
    private String[]                               otherExtensions;

    private static final Map<String, ResourceType> CACHE = Collections.synchronizedMap( new HashMap<String, ResourceType>() );

    public ResourceType(String name,
                        String description,
                        String defaultExtension,
                        String... otherExtensions ) {
        this.name = name;
        this.description = description;
        this.defaultExtension = defaultExtension;
        this.otherExtensions = otherExtensions;
    }

    public static ResourceType addResourceTypeToRegistry(final String resourceType,
                                                         final String description,
                                                         final String defaultExtension,
                                                         final String ...otherExtensions) {
    	
    	ResourceType resource = new ResourceType( resourceType,
                                                description,
                                                defaultExtension,
                                                otherExtensions);
        CACHE.put( resourceType, resource );    	
        return resource;
    }

    /** Drools Rule Language */
    public static final ResourceType DRL        = addResourceTypeToRegistry( "DRL",
                                                                             "Drools Rule Language",
                                                                             "drl");

    /** Drools XML Rule Language */
    public static final ResourceType XDRL       = addResourceTypeToRegistry( "XDRL",
                                                                             "Drools XML Rule Language",
                                                                             "xdrl" );

    /** Drools DSL */
    public static final ResourceType DSL        = addResourceTypeToRegistry( "DSL",
                                                                             "Drools DSL",
                                                                             "dsl");

    /** Drools DSL Rule */
    public static final ResourceType DSLR       = addResourceTypeToRegistry( "DSLR",
                                                                             "Drools DSL Rule",
                                                                             "dslr");

    /** Drools Rule Flow Language */
    public static final ResourceType DRF        = addResourceTypeToRegistry( "DRF",
                                                                             "Drools Rule Flow Language",
                                                                             "rf" );

    /** jBPM BPMN2 Language */
    public static final ResourceType BPMN2      = addResourceTypeToRegistry( "BPMN2",
                                                                             "jBPM BPMN2 Language",
                                                                             "bpmn", "bpmn2" );

    /** Decision Table */
    public static final ResourceType DTABLE     = addResourceTypeToRegistry( "DTABLE",
                                                                             "Decision Table",
                                                                             "xls" );

    /** Binary Package */
    public static final ResourceType PKG        = addResourceTypeToRegistry( "PKG",
                                                                             "Binary Package",
                                                                             "pkg" );

    /** Drools Business Rule Language */
    public static final ResourceType BRL        = addResourceTypeToRegistry( "BRL",
                                                                             "Drools Business Rule Language",
                                                                             "brl" );

    /** Change Set */
    public static final ResourceType CHANGE_SET = addResourceTypeToRegistry( "CHANGE_SET",
                                                                             "Change Set",
                                                                             "xcs" );
    
    /** XSD */
    public static final ResourceType XSD        = addResourceTypeToRegistry( "XSD",
                                                                             "XSD",
                                                                             "xsd" );

    /** PMML */
    public static final ResourceType PMML       = addResourceTypeToRegistry( "PMML",
                                                                             "Predictive Model Markup Language",
                                                                             "pmml" );

    /** DESCR */
    public static final ResourceType DESCR      = addResourceTypeToRegistry( "DESCR",
                                                                             "Knowledge Descriptor",
                                                                             "descr" );





    public static ResourceType getResourceType(final String resourceType) {
        ResourceType resource = CACHE.get( resourceType );
        if ( resource == null ) {
            throw new RuntimeException( "Unable to determine resource type " + resourceType );
        }
        return resource;
    }
    
    public static ResourceType determineResourceType( final String resourceName ) {
        for( ResourceType type : CACHE.values() ) {
            if( type.matchesExtension( resourceName ) ) {
                return type;
            }
        }
        return null;
    }
    
    public boolean matchesExtension( String resourceName ) {    	
    	if (resourceName != null) {
    		
    		if (resourceName.endsWith( "." + defaultExtension)) {
    			return true;
    		}
    		for (String extension: otherExtensions) {
    			if (resourceName.endsWith( "." + extension )) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    public String getDefaultExtension() {
        return defaultExtension;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return "ResourceType = '" + this.description + "'";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ResourceType other = (ResourceType) obj;
        if ( !name.equals( other.name ) ) return false;
        return true;
    }

}
