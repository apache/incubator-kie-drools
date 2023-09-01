package org.drools.drl.ast.dsl;


import org.drools.drl.ast.descr.EnumDeclarationDescr;

public interface EnumDeclarationDescrBuilder extends
    AnnotatedDescrBuilder<EnumDeclarationDescrBuilder>,
    AbstractClassTypeDeclarationBuilder<EnumDeclarationDescr> {


    /**
     * Defines the type name
     *
     * @param type the type name
     *
     * @return itself
     */
    public EnumDeclarationDescrBuilder name( String type );

    /**
     * Adds an enum literal
     *
     * @param lit
     * @return
     */
    public EnumLiteralDescrBuilder newEnumLiteral( String lit );
}
