package org.kie.dmn.typesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.kie.dmn.api.core.DMNType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class DMNDeclaredField implements FieldDefinition {

    private String fieldName;
    private DMNType fieldType;
    private List<AnnotationDefinition> annotations = new ArrayList<>();

    DMNDeclaredField(Map.Entry<String, DMNType> dmnType) {
        this.fieldName = dmnType.getKey();
        this.fieldType = dmnType.getValue();
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getObjectType() {
        if (fieldType.isCollection()) {
            String typeName = fieldType.getBaseType().getName();
            return String.format("java.util.Collection<%s>", StringUtils.ucFirst(typeName));
        }
        return StringUtils.ucFirst(fieldType.getName());
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
        if (fieldType.isCollection()) {
            return replaceTemplate(collectionsPropertyBlock, fieldType.getBaseType().getName());
        } else if (fieldType.isComposite()) {
            return replaceTemplate(pojoPropertyBlock, fieldType.getName());
        } else {
            return replaceTemplate(simplePropertyBlock, fieldType.getName());
        }
    }


    private BlockStmt replaceTemplate(BlockStmt pojoPropertyBlock, String objectType) {
        BlockStmt clone = pojoPropertyBlock.clone();
        clone.removeComment();

        clone.findAll(NameExpr.class, this::propertyPlaceHolder)
                .forEach(n -> n.replace(new NameExpr(fieldName)));

        clone.findAll(StringLiteralExpr.class, this::propertyPlaceHolder)
                .forEach(n -> n.replace(new StringLiteralExpr(fieldName)));

        clone.findAll(ClassOrInterfaceType.class, this::propertyTypePlaceHolder)
                .forEach(n -> n.replace(parseClassOrInterfaceType(StringUtils.ucFirst(objectType))));

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
