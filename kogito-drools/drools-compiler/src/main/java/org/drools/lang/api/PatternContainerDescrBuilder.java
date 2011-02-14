package org.drools.lang.api;

import org.drools.lang.descr.BaseDescr;

/**
 * A helper interface for all builders that can encapsulate patterns
 */
public interface PatternContainerDescrBuilder<P extends DescrBuilder<?>, T extends BaseDescr> extends DescrBuilder<T> {

    public PatternDescrBuilder<P> pattern( String type );
    public PatternDescrBuilder<P> pattern();

}