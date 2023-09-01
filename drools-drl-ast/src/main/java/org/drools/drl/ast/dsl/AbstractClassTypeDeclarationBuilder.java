package org.drools.drl.ast.dsl;


import org.drools.drl.ast.descr.BaseDescr;

public interface AbstractClassTypeDeclarationBuilder<T extends BaseDescr>
    extends
    DescrBuilder<PackageDescrBuilder, T> {



    /**
     * Adds a field to this type declaration
     *
     * @param name the name of the field
     *
     * @return a descriptor builder for the field
     */
    public FieldDescrBuilder<AbstractClassTypeDeclarationBuilder<T>> newField( String name );
}
