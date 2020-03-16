package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.impl.DMNModelImpl;

public class DMNInputSetGenerator {

    private DMNModelImpl dmnModel;

    private Map<String, TypeDefinition> types = new HashMap<>();

    public DMNInputSetGenerator(DMNModel dmnModel) {
        this.dmnModel = (DMNModelImpl) dmnModel;
        processTypes();
    }

    private void processTypes() {

        Set<ItemDefNode> itemDefinitions = dmnModel.getItemDefinitions();
        for(ItemDefNode i : itemDefinitions) {
            DMNDeclaredType dmnDeclaredType = new DMNDeclaredType(i.getType());
            types.put(dmnDeclaredType.getTypeName(), dmnDeclaredType);
        }
    }

    public String getType(String key) {

        TypeDefinition typeDefinition = types.get(key);

        ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(typeDefinition,
                                                                                   Collections.emptyList()).toClassDeclaration();

        CompilationUnit cu = new CompilationUnit("org.kie.dmn.typesafe");
        cu.addType(generatedClass);

        return cu.toString();
    }
}
