package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.ArrayList;
import java.util.List;

public class MethodWithStringBody implements MethodDefinition {

    private final String methodName;
    private final String returnType;

    private final String body;

    private final List<MethodParameter> parameters = new ArrayList<>();

    private List<AnnotationDefinition> annotations = new ArrayList<>();

    public MethodWithStringBody(String methodName, String returnType, String body) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    public MethodWithStringBody addParameter(String key, String value) {
        parameters.add(new MethodParameter(key, value));
        return this;
    }

    @Override
    public List<MethodParameter> parameters() {
        return parameters;
    }

    public MethodWithStringBody addAnnotation(String name) {
        annotations.add(new SimpleAnnotationDefinition(name));
        return this;
    }

    @Override
    public List<AnnotationDefinition> getAnnotations() {
        return annotations;
    }
}