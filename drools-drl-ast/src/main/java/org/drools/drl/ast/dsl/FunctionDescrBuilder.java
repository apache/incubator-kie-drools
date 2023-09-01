package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.FunctionDescr;

/**
 *  A descriptor builder for functions
 */
public interface FunctionDescrBuilder
    extends
    DescrBuilder<PackageDescrBuilder, FunctionDescr>,
    ParameterSupportBuilder<FunctionDescrBuilder> {

    /**
     * Sets an alternate namespace for the function
     * 
     * @param namespace
     * 
     * @return itself
     */
    public FunctionDescrBuilder namespace( String namespace );

    /**
     * Sets function name
     * 
     * @param name
     * 
     * @return itself
     */
    public FunctionDescrBuilder name( String name );

    /**
     * Sets function return type
     * 
     * @param type
     * 
     * @return itself
     */
    public FunctionDescrBuilder returnType( String type );

    /**
     * Sets the function body
     * 
     * @param body
     * 
     * @return itself
     */
    public FunctionDescrBuilder body( String body );

    /**
     * Sets the function dialect. Default is java.
     * 
     * @param dialect
     * 
     * @return itself
     */
    public FunctionDescrBuilder dialect( String dialect );
}
