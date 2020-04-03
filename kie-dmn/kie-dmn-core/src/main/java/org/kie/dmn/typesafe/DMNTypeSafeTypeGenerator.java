package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.impl.DMNModelImpl;

import static org.kie.dmn.typesafe.DMNClassNamespaceTypeIndex.namespace;

public class DMNTypeSafeTypeGenerator {

    private final String packageName;
    private DMNClassNamespaceTypeIndex allNamespaces;
    private DMNModelImpl dmnModel;

    private Map<String, TypeDefinition> types = new HashMap<>();

    public DMNTypeSafeTypeGenerator(DMNModel dmnModel, DMNClassNamespaceTypeIndex index) {
        this.dmnModel = (DMNModelImpl) dmnModel;
        this.packageName = namespace(dmnModel);
        this.allNamespaces = index;
        processTypes();
    }

    private void processTypes() {
        Set<InputDataNode> inputs = dmnModel.getInputs();
        DMNInputSetType inputSetType = new DMNInputSetType(allNamespaces);
        for (InputDataNode i : inputs) {
            inputSetType.addField(i.getName(), i.getType());
        }
        inputSetType.initFields();

        types.put(inputSetType.getTypeName(), inputSetType);

        Set<DMNType> itemDefinitions = dmnModel.getItemDefinitions()
                .stream()
                .map(ItemDefNode::getType)
                .collect(Collectors.toSet());

        for (DMNType type : itemDefinitions) {
            DMNDeclaredType dmnDeclaredType = new DMNDeclaredType(allNamespaces, type);
            types.put(dmnDeclaredType.getTypeName(), dmnDeclaredType);
            if (type.isComposite()) {
                // need a way here to discriminate whether we should generate this or not
//                for (DMNType innerType : type.getFields().values()) {
//                    DMNDeclaredType dmnDeclaredInnerType = new DMNDeclaredType(allNamespaces, innerType);
//                    types.put(dmnDeclaredInnerType.getTypeName(), dmnDeclaredInnerType);
//                }
            }
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
