package org.drools.lang.api;

import org.drools.lang.descr.BaseDescr;

/**
 * An interface for all builders of statements that support attributes
 */
public interface AttributeSupportBuilder<T extends BaseDescr> extends DescrBuilder<T> {

    /**
     * Adds a new attribute to the statement
     * 
     * @param name the attribute name
     * @return the AttributeDescrBuilder to set the attribute value
     */
    public AttributeDescrBuilder attribute( String name );

}
