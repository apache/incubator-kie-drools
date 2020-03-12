package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.impl.DMNModelImpl;

public class DMNInputSetGenerator {

    private DMNModelImpl dmnModel;

    private Map<String, TypeDefinition> types = new HashMap<>();

    public DMNInputSetGenerator(DMNModel dmnModel) {
        this.dmnModel = (DMNModelImpl) dmnModel;
        processTypes();
    }

    private void processTypes() {

        for (InputDataNode n : dmnModel.getInputs()) {
            DMNDeclaredType dmnDeclaredType = new DMNDeclaredType(n.getType());
            types.put(dmnDeclaredType.getTypeName(), dmnDeclaredType);
        }
    }

    public String getType(String key) {

        TypeDefinition typeDefinition = types.get(key);

        ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(typeDefinition,
                                                                                   t -> Optional.empty(),
                                                                                   Collections.emptyList()).toClassDeclaration();

        return generatedClass.toString();
    }
}
