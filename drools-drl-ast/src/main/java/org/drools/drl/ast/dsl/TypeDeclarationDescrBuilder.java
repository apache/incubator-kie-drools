package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.TypeDeclarationDescr;

/**
 *  A descriptor builder for declared types
 */
public interface TypeDeclarationDescrBuilder
    extends
    AnnotatedDescrBuilder<TypeDeclarationDescrBuilder>,
    AbstractClassTypeDeclarationBuilder<TypeDeclarationDescr> {


    /**
     * Defines the type name
     *
     * @param type the type name
     *
     * @return itself
     */
    public TypeDeclarationDescrBuilder name( String type );

    /**
     * Defines the super type of this type. For POJOs, this is the
     * super class it will extend
     * 
     * @param type the super type for this type
     * 
     * @return itself
     */
    public TypeDeclarationDescrBuilder superType( String type );

    public TypeDeclarationDescrBuilder setTrait( boolean trait );
}
