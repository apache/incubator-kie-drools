package org.drools.builder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceType implements Serializable {

    private String                                 name;

    private String                                 description;
    
    private String                                 defaultExtension;

    private static final Map<String, ResourceType> CACHE = Collections.synchronizedMap( new HashMap<String, ResourceType>() );

    public ResourceType(String name,
                        String description,
                        String defaultExtension ) {
        this.name = name;
        this.description = description;
        this.defaultExtension = defaultExtension;
    }

    public static ResourceType addResourceTypeToRegistry(final String resourceType,
                                                         final String description,
                                                         final String defaultExtension ) {
        ResourceType resource = new ResourceType( resourceType,
                                                  description,
                                                  defaultExtension );
        CACHE.put( resourceType,
                   resource );
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

    /** Drools BPMN2 Language */
    public static final ResourceType BPMN2      = addResourceTypeToRegistry( "BPMN2",
                                                                             "Drools BPMN2 Language",
                                                                             "bpmn" );

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
        return resourceName != null && resourceName.endsWith( "."+defaultExtension );
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
