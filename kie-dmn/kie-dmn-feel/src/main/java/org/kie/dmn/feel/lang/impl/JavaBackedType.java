package org.kie.dmn.feel.lang.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.CustomType;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Property;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ParserHelper;

public class JavaBackedType implements CustomType {
    private static Map<Class<?>, JavaBackedType> cache = new HashMap<>();
    
    private Class<?> wrapped;
    private Map<String, Property> properties = new HashMap<>();

    private JavaBackedType(Class<?> class1) {
        this.wrapped = class1;
        Stream.of( class1.getMethods() )
            .filter( m -> m.getAnnotation(FEELProperty.class) != null )
            .forEach( m -> properties.put( m.getAnnotation(FEELProperty.class).value() , new PropertyImpl( m.getAnnotation(FEELProperty.class).value() , ParserHelper.determineTypeFromClass(m.getReturnType()) ) ) );
            ;
    }
    
    /**
     * If clazz can be represented as a JavaBackedType, returns a JavaBackedType for representing clazz.
     * If clazz can not be represented as a JavaBackedType, returns BuiltInType.UNKNOWN.
     * This method performs memoization when necessary.
     * @param clazz the class to be represented as JavaBackedType
     * @return JavaBackedType representing clazz or BuiltInType.UNKNOWN
     */
    public static Type of(Class<?> clazz) {
        return Optional.ofNullable( (Type) cache.computeIfAbsent( clazz, JavaBackedType::createIfAnnotated ) ).orElse( BuiltInType.UNKNOWN );
    }
    
    /**
     * For internal use, returns a new JavaBackedType if clazz can be represented as such, returns null otherwise.
     */
    private static JavaBackedType createIfAnnotated(Class<?> clazz) {
        if (Stream.of(clazz.getMethods()).anyMatch(m->m.getAnnotation(FEELProperty.class)!=null)) {
            return new JavaBackedType(clazz) ;
        }
        return null;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Object fromString(String value) {
        return null;
    }

    @Override
    public String toString(Object value) {
        return null;
    }

    public Class<?> getWrapped() {
        return wrapped;
    }

    @Override
    public Map<String, Property> getProperties() {
        return this.properties;
    }
}
