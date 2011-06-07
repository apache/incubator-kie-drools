package org.drools.lang.api;

import org.drools.lang.descr.AttributeDescr;

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

    /**
     * Adds a new attribute with the given name and value
     * 
     * @param name the name of the attribute to be added
     * @param value the value of the attribute to be added
     * @return the container builder
     */
    public P attribute( String name,
                        String value );

    /**
     * Adds a new attribute with the given name and value
     * 
     * @param name the name of the attribute to be added
     * @param value the value of the attribute to be added
     * @param type the type of the value of the attribute. See {@link AttributeDescr.Type}
     * @return the container builder
     */
    public P attribute( String name,
                        String value,
                        AttributeDescr.Type type );

}
