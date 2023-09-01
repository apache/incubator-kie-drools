package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.PatternDescr;

/**
 *  A descriptor builder for Pattern sources
 */
public interface SourceDescrBuilder<P extends PatternDescrBuilder<?>>
    extends
    DescrBuilder<P, PatternDescr> {

    /**
     * Defines the pattern source as being an expression result
     * 
     * @param expression the expression
     * 
     * @return parent descriptor builder
     */
    P expression( String expression );

    /**
     * Defines the pattern source as being an entry point
     * 
     * @param entryPoint the entry point identifier
     * 
     * @return parent descriptor builder
     */
    P entryPoint( String entryPoint );

    /**
     * Defines the pattern source as a collection 
     * 
     * @return the collect descriptor builder
     */
    CollectDescrBuilder<P> collect();

    /**
     * Defines the pattern source as being an accumulation
     * 
     * @return the accumulate descriptor builder
     */
    AccumulateDescrBuilder<P> accumulate();

    GroupByDescrBuilder<P> groupBy();

    /**
     * Defines the pattern source as being a declared window
     * 
     * @param window the declared window identifier
     * 
     * @return parent descriptor builder
     */
    P window( String window );

}
