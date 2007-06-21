/**
 * 
 */
package org.drools.lang.descr;

/**
 * @author fernandomeyer
 */

public interface PatternProcessorCeDescr
    extends
    ConditionalElementDescr {

    void setResultPattern(PatternDescr patternDescr);
    void setSourcePattern(PatternDescr patternDescr);
}
