package org.drools.lang.api;

import org.drools.lang.descr.AnnotationDescr;

public class AnnotationDescrBuilderImpl extends BaseDescrBuilderImpl<AnnotationDescr>
    implements
    AnnotationDescrBuilder {

    protected AnnotationDescrBuilderImpl(String name) {
        super( new AnnotationDescr( name ) );
    }

    public AnnotationDescrBuilder value( String value ) {
        descr.setValue( value );
        return this;
    }

    public AnnotationDescrBuilder keyValue( String key,
                                            String value ) {
        descr.setKeyValue( key,
                           value );
        return this;
    }

}
