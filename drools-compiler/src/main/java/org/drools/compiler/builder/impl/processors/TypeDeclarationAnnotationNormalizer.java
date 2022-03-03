package org.drools.compiler.builder.impl.processors;

import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

public class TypeDeclarationAnnotationNormalizer implements CompilationPhase {
    private final AnnotationNormalizer annotationNormalizer;
    private final PackageDescr packageDescr;

    public TypeDeclarationAnnotationNormalizer(
            AnnotationNormalizer annotationNormalizer,
            PackageDescr packageDescr) {
        this.annotationNormalizer = annotationNormalizer;
        this.packageDescr = packageDescr;
    }

    public void process() {
        for (TypeDeclarationDescr typeDeclarationDescr : packageDescr.getTypeDeclarations()) {
            annotationNormalizer.normalize(typeDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : typeDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }

        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            annotationNormalizer.normalize(enumDeclarationDescr);
            for (TypeFieldDescr typeFieldDescr : enumDeclarationDescr.getFields().values()) {
                annotationNormalizer.normalize(typeFieldDescr);
            }
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return annotationNormalizer.getResults();
    }

}
