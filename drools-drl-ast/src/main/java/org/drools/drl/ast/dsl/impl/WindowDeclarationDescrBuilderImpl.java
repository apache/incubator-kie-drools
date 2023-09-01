package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.WindowDeclarationDescrBuilder;
import org.drools.drl.ast.descr.WindowDeclarationDescr;

public class WindowDeclarationDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, WindowDeclarationDescr>
    implements
    WindowDeclarationDescrBuilder {

    protected WindowDeclarationDescrBuilderImpl(PackageDescrBuilder parent) {
        super( parent,
               new WindowDeclarationDescr() );
    }

    public WindowDeclarationDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public AnnotationDescrBuilder<WindowDeclarationDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<WindowDeclarationDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this,
                                                                                                                                          name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern( String type ) {
        PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern = new PatternDescrBuilderImpl<>( this,
                                                                                                                                 type );
        descr.setPattern( pattern.getDescr() );
        return pattern;
    }

    public PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern() {
        PatternDescrBuilder<WindowDeclarationDescrBuilder> pattern = new PatternDescrBuilderImpl<>( this );
        descr.setPattern( pattern.getDescr() );
        return pattern;
    }

}
