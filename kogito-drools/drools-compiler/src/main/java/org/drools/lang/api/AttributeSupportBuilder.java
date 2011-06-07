package org.drools.lang.api;


/**
 * An interface for all builders of statements that support attributes
 */
public interface AttributeSupportBuilder<P extends DescrBuilder< ? , ? >> {

    /**
     * Adds a new attribute to the statement
     * 
     * @param name the attribute name
     * @return the AttributeDescrBuilder to set the attribute value
     */
    public AttributeDescrBuilder<P> attribute( String name );

}
