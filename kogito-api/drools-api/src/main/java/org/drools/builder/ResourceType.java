package org.drools.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceType {
	
	private String name;
	
    private static final Map<String, ResourceType> CACHE = Collections.synchronizedMap( new HashMap<String, ResourceType>() );

    public ResourceType(String name) {
		this.name = name;
	}
	
    public static ResourceType addResourceTypeToRegistry(final String resourceType) {
		ResourceType resource = new ResourceType(resourceType);
		CACHE.put(resourceType, resource);
		return resource;
	}

    /** Drools Rule Language */
    public static final ResourceType DRL = addResourceTypeToRegistry("DRL");

    /** Drools XML Rule Language */
    public static final ResourceType XDRL = addResourceTypeToRegistry("XDRL");

    /** Drools DSL */
    public static final ResourceType DSL = addResourceTypeToRegistry("DSL");

    /** Drools DSL Rule */
    public static final ResourceType DSLR = addResourceTypeToRegistry("DSLR");

    /** Drools Rule Flow Language */
    public static final ResourceType DRF = addResourceTypeToRegistry("DRF");

    /** Decision Table */
    public static final ResourceType DTABLE = addResourceTypeToRegistry("DTABLE");

    /** Binary Package */
    public static final ResourceType PKG = addResourceTypeToRegistry("PKG");

    /** ChangeSet */
    public static final ResourceType ChangeSet = addResourceTypeToRegistry("ChangeSet");
    
    public static ResourceType getResourceType(final String resourceType) {
    	ResourceType resource = CACHE.get(resourceType);
        if ( resource == null ) {
            throw new RuntimeException( "Unable to determine resource type " + resourceType );
        }
        return resource;
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
