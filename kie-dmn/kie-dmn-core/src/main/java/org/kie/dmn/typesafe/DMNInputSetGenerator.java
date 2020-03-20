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
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.impl.DMNModelImpl;

public class DMNInputSetGenerator {

    private final String packageName;
    private DMNModelImpl dmnModel;

    private Map<String, TypeDefinition> types = new HashMap<>();

    public DMNInputSetGenerator(DMNModel dmnModel, String packageName) {
        this.dmnModel = (DMNModelImpl) dmnModel;
        this.packageName = packageName;
        processTypes();
    }

    private void processTypes() {

        //  gli InputDataNode sono n (ognuno un campo) ha un tipo
        Set<InputDataNode> inputs = dmnModel.getInputs();
        DMNInputSetType inputSetType = new DMNInputSetType();
        for(InputDataNode i : inputs) {
            inputSetType.addField(i.getName(), i.getType());
        }
        inputSetType.initFields();

        types.put(inputSetType.getTypeName(), inputSetType);

        Set<ItemDefNode> itemDefinitions = dmnModel.getItemDefinitions();
        for (ItemDefNode i : itemDefinitions) {
            DMNDeclaredType dmnDeclaredType = new DMNDeclaredType(i.getType());
            types.put(dmnDeclaredType.getTypeName(), dmnDeclaredType);
        }
    }

    public Map<String, String> generateSourceCodeOfAllTypes() {
        Map<String, String> allSources = new HashMap<>();
        String packageDeclaration = this.packageName;
        for (Map.Entry<String, TypeDefinition> kv : types.entrySet()) {
            ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(kv.getValue(),
                                                                                       Collections.emptyList()).toClassDeclaration();

            CompilationUnit cu = new CompilationUnit(packageDeclaration);
            cu.addType(generatedClass);

            allSources.put(packageDeclaration + "." + kv.getKey(), cu.toString());
        }
        return allSources;
    }
}
