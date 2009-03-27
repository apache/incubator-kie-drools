package org.drools.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceType {

    private String                                 name;

    private String                                 description;

    private static final Map<String, ResourceType> CACHE = Collections.synchronizedMap( new HashMap<String, ResourceType>() );

    public ResourceType(String name,
                        String description) {
        this.name = name;
        this.description = description;
    }

    public static ResourceType addResourceTypeToRegistry(final String resourceType,
                                                         String description) {
        ResourceType resource = new ResourceType( resourceType,
                                                  description );
        CACHE.put( resourceType,
                   resource );
        return resource;
    }

    /** Drools Rule Language */
    public static final ResourceType DRL        = addResourceTypeToRegistry( "DRL",
                                                                             "Drools Rule Language" );

    /** Drools XML Rule Language */
    public static final ResourceType XDRL       = addResourceTypeToRegistry( "XDRL",
                                                                             "Drools XML Rule Language" );

    /** Drools DSL */
    public static final ResourceType DSL        = addResourceTypeToRegistry( "DSL",
                                                                             "Drools DSL" );

    /** Drools DSL Rule */
    public static final ResourceType DSLR       = addResourceTypeToRegistry( "DSLR",
                                                                             "Drools DSL Rule" );

    /** Drools Rule Flow Language */
    public static final ResourceType DRF        = addResourceTypeToRegistry( "DRF",
                                                                             "Drools Rule Flow Language" );

    /** Decision Table */
    public static final ResourceType DTABLE     = addResourceTypeToRegistry( "DTABLE",
                                                                             "Decision Table" );

    /** Binary Package */
    public static final ResourceType PKG        = addResourceTypeToRegistry( "PKG",
                                                                             "Binary Package" );

    /** Drools Business Rule Language */
    public static final ResourceType BRL        = addResourceTypeToRegistry( "BRL",
                                                                             "Drools Business Rule Language" );

    /** Change Set */
    public static final ResourceType CHANGE_SET = addResourceTypeToRegistry( "CHANGE_SET",
                                                                             "Change Set" );

    public static ResourceType getResourceType(final String resourceType) {
        ResourceType resource = CACHE.get( resourceType );
        if ( resource == null ) {
            throw new RuntimeException( "Unable to determine resource type " + resourceType );
        }
        return resource;
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
