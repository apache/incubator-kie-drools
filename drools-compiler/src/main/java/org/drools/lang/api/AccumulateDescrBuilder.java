/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;

/**
 *  A descriptor builder for Accumulate
 */
public interface AccumulateDescrBuilder<P extends DescrBuilder< ?, ? >>
    extends
    DescrBuilder<P, AccumulateDescr> {
    
    /**
     * Defines the source CE for the Accumulate CE. It is always
     * an AND descriptor that contains one or more Patterns and
     * other CEs. 
     *  
     * @return the CEDescrBuilder for the source CE
     */
    public CEDescrBuilder<AccumulateDescrBuilder<P>, AndDescr> source();

    /**
     * For accumulate CEs that use accumulate functions, this call
     * instantiate one accumulate function call. Example:
     * 
     * ... accumulate( X(), $sum : sum( $value ) )
     * 
     * Requires the call to this method with parameters:
     * 
     * function( "sum", "$sum", new String[] { "$value" } )
     * 
     * Please note that accumulate supports multiple function calls 
     * and this method should be called for each function call.
     * 
     * Accumulate uses either accumulate functions or the custom
     * code blocks (init/action/reverse/result). It is not possible
     * to mix them.
     * 
     * @param name the name of the function being called. Mandatory non-null parameter.
     * @param bind the name of the bound variable if there is one. Null if no binding should be made.
     * @param parameters the array of parameters to the function. 
     * 
     * @return itself, so that it can be used as a fluent API
     */
    public AccumulateDescrBuilder<P> function( String name, String bind, String[] parameters);
    
    /**
     * For accumulate CEs that use custom code blocks, this call
     * sets the content of the init code block. Please node that the
     * use of custom code blocks is discouraged, as they are usually
     * a bad practice.
     * 
     * Accumulate uses either accumulate functions or the custom
     * code blocks (init/action/reverse/result). It is not possible
     * to mix them.
     * 
     * @param block the code for this block
     * 
     * @return itself, so that it can be used as a fluent API
     */
    public AccumulateDescrBuilder<P> init( String block );

    /**
     * For accumulate CEs that use custom code blocks, this call
     * sets the content of the action code block. Please node that the
     * use of custom code blocks is discouraged, as they are usually
     * a bad practice.
     * 
     * Accumulate uses either accumulate functions or the custom
     * code blocks (init/action/reverse/result). It is not possible
     * to mix them.
     * 
     * @param block the code for this block
     * 
     * @return itself, so that it can be used as a fluent API
     */
    public AccumulateDescrBuilder<P> action( String block );

    /**
     * For accumulate CEs that use custom code blocks, this call
     * sets the content of the reverse code block. Please node that the
     * use of custom code blocks is discouraged, as they are usually
     * a bad practice.
     * 
     * Accumulate uses either accumulate functions or the custom
     * code blocks (init/action/reverse/result). It is not possible
     * to mix them.
     * 
     * @param block the code for this block
     * 
     * @return itself, so that it can be used as a fluent API
     */
    public AccumulateDescrBuilder<P> reverse( String block );
    
    /**
     * For accumulate CEs that use custom code blocks, this call
     * sets the content of the result expression. Please node that the
     * use of custom code blocks is discouraged, as they are usually
     * a bad practice.
     * 
     * Accumulate uses either accumulate functions or the custom
     * code blocks (init/action/reverse/result). It is not possible
     * to mix them.
     * 
     * @param expr the return expr
     * 
     * @return itself, so that it can be used as a fluent API
     */
    public AccumulateDescrBuilder<P> result( String expr );
    public AccumulateDescrBuilder<P> multiFunction( boolean b );
    
    public P end();

}
