package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

public class DescrAnnotationDefinition implements AnnotationDefinition {

    static final String VALUE = "value";

    private static final Map<String, Class<?>> annotationMapping = new HashMap<>();

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
        this(name, Collections.singletonMap(VALUE, singleValue));
    }

    public DescrAnnotationDefinition(String name) {
        this(name, Collections.emptyMap());
    }

    public DescrAnnotationDefinition(AnnotationDescr ann) {
        this.ann = ann;
        this.name = annotationMapping.get(ann.getName()).getName();
    }

    public static AnnotationDefinition createPositionAnnotation(int position) {
        return new DescrAnnotationDefinition(Position.class.getName(),
                                             Collections.singletonMap(VALUE, String.valueOf(position)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getValueMap() {
        return values;
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
