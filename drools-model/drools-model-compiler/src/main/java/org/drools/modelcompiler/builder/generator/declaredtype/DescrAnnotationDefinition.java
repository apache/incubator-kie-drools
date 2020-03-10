package org.drools.modelcompiler.builder.generator.declaredtype;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

public class DescrAnnotationDefinition implements AnnotationDefinition {

    static final String VALUE = "value";

    private static final Map<String, Class<?>> annotationMapping = new HashMap<>();

    private boolean shouldAddAnnotation = true;

    static {
        annotationMapping.put("role", Role.class);
        annotationMapping.put("duration", Duration.class);
        annotationMapping.put("expires", Expires.class);
        annotationMapping.put("timestamp", Timestamp.class);
        annotationMapping.put("key", Key.class);
        annotationMapping.put("position", Position.class);
    }

    private String name;
    private Map<String, String> values = new HashMap<>();
    private AnnotationDescr ann;

    public DescrAnnotationDefinition(String name, Map<String, String> values) {
        this.name = name;
        this.values = values;
    }

    public DescrAnnotationDefinition(String name, String singleValue) {
        this(name, singletonMap(VALUE, singleValue));
    }

    public DescrAnnotationDefinition(String name) {
        this(name, Collections.emptyMap());
    }

    public DescrAnnotationDefinition(AnnotationDescr ann) {
        this.ann = ann;
        Optional<Class<?>> optAnnotationClass = Optional.ofNullable(annotationMapping.get(ann.getName()));

        optAnnotationClass.ifPresent(this::setAnnotationValues);

        if (!optAnnotationClass.isPresent()) {
            shouldAddAnnotation = false;
        }
        this.name = optAnnotationClass.map(Class::getName).orElse(ann.getName());
    }

    private void setAnnotationValues(Class<?> annotationClass) {
        this.values = ann.getValueMap().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> parseValue(annotationClass, e.getKey(), e.getValue())));
    }

    private String parseValue(Class<?> annotationClass, String valueName, Object valueObject) {
        final String parsedValue = parseAnnotationValue(valueObject);
        if (annotationClass.equals(Role.class)) {
            return Role.Type.class.getCanonicalName() + "." + parsedValue.toUpperCase();
        } else if (annotationClass.equals(Expires.class)) {
            if (VALUE.equals(valueName)) {
                return quote(parsedValue);
            } else if ("policy".equals(valueName)) {
                return org.kie.api.definition.type.Expires.Policy.class.getCanonicalName() + "." + parsedValue.toUpperCase();
            } else {
                throw new UnsupportedOperationException("Unrecognized annotation value for Expires: " + valueName);
            }
        }
        return parsedValue;
    }

    private static String parseAnnotationValue(Object value) {
        if (value instanceof Class<?>) {
            return ((Class<?>) value).getName() + ".class";
        }
        if (value.getClass().isArray()) {
            String valueString = Stream.of((Object[]) value)
                    .map(Object::toString)
                    .collect(joining(",", "{", "}"));

            return valueString
                    .replace('[', '{')
                    .replace(']', '}');
        }
        return value.toString();
    }

    public static AnnotationDefinition createPositionAnnotation(int position) {
        return new DescrAnnotationDefinition(Position.class.getName(),
                                             singletonMap(VALUE, String.valueOf(position)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getValueMap() {
        return values;
    }

    @Override
    public boolean shouldAddAnnotation() {
        return shouldAddAnnotation;
    }

    public boolean isKey() {
        return isDroolsAnnotation(Key.class);
    }

    public boolean isPosition() {
        return isDroolsAnnotation(Position.class);
    }

    public boolean isClassLevelAnnotation() {
        return isDroolsAnnotation(Duration.class) ||
                isDroolsAnnotation(Expires.class) ||
                isDroolsAnnotation(Timestamp.class);
    }

    private boolean isDroolsAnnotation(Class<?> key) {
        return name.equals(key.getName());
    }

    @Override
    public String toString() {
        return "DescrAnnotationDefinition{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
