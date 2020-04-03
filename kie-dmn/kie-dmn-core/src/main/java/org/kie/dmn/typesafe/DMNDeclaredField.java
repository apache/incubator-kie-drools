package org.kie.dmn.typesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class DMNDeclaredField implements FieldDefinition {

    private final static  String OBJECT_TYPE = "Object";

    private DMNAllTypesIndex index;
    private String fieldName;
    private String originalMapKey;
    private DMNType fieldType;
    private List<AnnotationDefinition> annotations = new ArrayList<>();

    DMNDeclaredField(DMNAllTypesIndex index, Map.Entry<String, DMNType> dmnType) {
        this.index = index;
        this.fieldName = CodegenStringUtil.escapeIdentifier(dmnType.getKey());
        this.originalMapKey = dmnType.getKey();
        this.fieldType = dmnType.getValue();
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getObjectType() {
        if (fieldType.isCollection()) {
            String typeName = getBaseType(fieldType);
            String typeNameWithPackage = withPackage(typeName);
            return String.format("java.util.Collection<%s>", typeNameWithPackage);
        }
        return fieldTypeWithPackage();
    }

    private String fieldTypeWithPackage() {
        return withPackage(getFieldNameWithAnyCheck());
    }

    private String getFieldNameWithAnyCheck() {
        String name = fieldType.getName();
        if("Any".equals(name)) {
            return OBJECT_TYPE;
        } else {
            return name;
        }
    }

    // This returns the generic type i.e. if Collection<String> then String
    private String fieldTypeUnwrapped() {
        if (fieldType.isCollection()) {
            String typeName = getBaseType(fieldType);
            return withPackage(typeName);
        }
        return fieldTypeWithPackage();
    }

    private String withPackage(String typeName) {
        String typeNameUpperCase = StringUtils.ucFirst(typeName);
        Optional<String> packageName = index.namespaceOfClass(typeName);
        return packageName.map(p -> p + "." + typeNameUpperCase).orElse(typeNameUpperCase);
    }

    public static String getBaseType(DMNType fieldType) {
        Optional<DMNType> baseType = Optional.ofNullable(fieldType.getBaseType());
        return baseType.map(DMNType::getName)
                .orElse(OBJECT_TYPE);
    }

    @Override
    public String getInitExpr() {
        return null;
    }

    @Override
    public List<AnnotationDefinition> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isKeyField() {
        return false;
    }

    @Override
    public boolean createAccessors() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    public BlockStmt createFromMapEntry(BlockStmt simplePropertyBlock,
                                        BlockStmt pojoPropertyBlock,
                                        BlockStmt collectionsPropertyBlock) {
        if (fieldType.isCollection() && !fieldIsObject()) {
            return replaceTemplate(collectionsPropertyBlock, fieldTypeUnwrapped());
        } else if (fieldType.isComposite()) {
            return replaceTemplate(pojoPropertyBlock, fieldTypeWithPackage());
        } else if(!fieldIsObject()){
            return replaceTemplate(simplePropertyBlock, fieldTypeWithPackage());
        }  else {
            return new BlockStmt();
        }
    }

    private boolean fieldIsObject() {
        return fieldTypeUnwrapped().equals(OBJECT_TYPE);
    }

    private BlockStmt replaceTemplate(BlockStmt pojoPropertyBlock, String objectType) {
        BlockStmt clone = pojoPropertyBlock.clone();
        clone.removeComment();

        clone.findAll(NameExpr.class, this::propertyPlaceHolder)
                .forEach(n -> n.replace(new NameExpr(fieldName)));

        clone.findAll(StringLiteralExpr.class, this::propertyPlaceHolder)
                .forEach(n -> n.replace(new StringLiteralExpr(originalMapKey)));

        clone.findAll(ClassOrInterfaceType.class, this::propertyTypePlaceHolder)
                .forEach(n -> n.replace(parseClassOrInterfaceType(objectType)));

        return clone;
    }

    private boolean propertyPlaceHolder(NameExpr n) {
        return n.toString().equals("$property$");
    }

    private boolean propertyPlaceHolder(StringLiteralExpr n) {
        return n.asString().equals("$property$");
    }

    private boolean propertyTypePlaceHolder(Object n) {
        return n.toString().equals("PropertyType");
    }
}
