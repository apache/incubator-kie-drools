package org.drools.lang.api;

import org.drools.lang.descr.AnnotationDescr;

public class AnnotationDescrBuilderImpl extends BaseDescrBuilderImpl
    implements
    AnnotationDescrBuilder {

    protected AnnotationDescrBuilderImpl( String name ) {
        super( new AnnotationDescr( name ) );
    }

    public AnnotationDescr getDescr() {
        return (AnnotationDescr) descr;
    }

    public AnnotationDescrBuilder value( String value ) {
        ((AnnotationDescr) descr).setValue( value );
        return this;
    }

    public AnnotationDescrBuilder keyValue( String key,
                                            String value ) {
        ((AnnotationDescr) descr).setKeyValue( key, value );
        return this;
    }


}
