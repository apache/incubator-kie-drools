package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.EnumLiteralDescr;


public interface EnumLiteralDescrBuilder
    extends
    AnnotatedDescrBuilder<EnumLiteralDescrBuilder>,
    DescrBuilder<EnumDeclarationDescrBuilder, EnumLiteralDescr> {

    public EnumLiteralDescrBuilder index( int index );

    public EnumLiteralDescrBuilder name( String name );

    public EnumLiteralDescrBuilder constructorArg( String arg );


}
