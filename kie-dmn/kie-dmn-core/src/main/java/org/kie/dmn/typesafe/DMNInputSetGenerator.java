package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.kie.dmn.api.core.DMNModel;

public class DMNInputSetGenerator {

    private DMNModel dmnModel;

    public DMNInputSetGenerator(DMNModel dmnModel) {
        this.dmnModel = dmnModel;

    }

    public String getType(String tPerson) {



        TypeDefinition person = new TypeDefinition() {
            @Override
            public String getTypeName() {
                return "TPerson";
            }

            @Override
            public List<FieldDefinition> getFields() {
                FieldDefinition field = new FieldDefinition() {
                    @Override
                    public String getFieldName() {
                        return "name";
                    }

                    @Override
                    public String getObjectType() {
                        return "String";
                    }

                    @Override
                    public String getInitExpr() {
                        return null;
                    }

                    @Override
                    public List<AnnotationDefinition> getAnnotations() {
                        return Collections.emptyList();
                    }

                    @Override
                    public boolean isKeyField() {
                        return false;
                    }

                    @Override
                    public boolean createAccessors() {
                        return false;
                    }

                    @Override
                    public boolean isStatic() {
                        return false;
                    }

                    @Override
                    public boolean isFinal() {
                        return false;
                    }
                };

                return Collections.singletonList(field);
            }

            @Override
            public List<FieldDefinition> getKeyFields() {
                return Collections.emptyList();
            }

            @Override
            public Optional<String> getSuperTypeName() {
                return Optional.empty();
            }

            @Override
            public List<AnnotationDefinition> getAnnotationsToBeAdded() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<FieldDefinition> findInheritedDeclaredFields() {
                return Collections.emptyList();
            }
        };

        ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(person,
                                                                                   t -> Optional.empty(),
                                                                                   Collections.emptyList()).toClassDeclaration();

        return generatedClass.toString();
    }
}
