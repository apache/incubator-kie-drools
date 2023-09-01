package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.AttributeDescr;

/**
 *  A descriptor builder for attributes
 */
public interface AttributeDescrBuilder<P extends DescrBuilder<?,?>>
    extends
    DescrBuilder<P, AttributeDescr> {

    /**
     * Sets the attribute value
     * 
     * @param value
     * @return itself
     */
    public AttributeDescrBuilder<P> value( String value );
    
    /**
     * Sets the attribute value type
     * 
     * @param type see {@link AttributeDescr.Type}
     * 
     * @return itself
     */
    public AttributeDescrBuilder<P> type( AttributeDescr.Type type );

}
