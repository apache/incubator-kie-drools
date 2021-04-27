package org.drools.mvelcompiler.context;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.core.addon.TypeResolver;
import org.drools.mvelcompiler.MvelCompilerException;

import static org.drools.core.util.StringUtils.isEmpty;

public class MvelCompilerContext {

    private final Map<String, Declaration> declarations = new HashMap<>();
    private final TypeResolver typeResolver;
    private final String scopeSuffix;

    // Used in ConstraintParser
    private Optional<Type> rootPattern = Optional.empty();
    private String rootTypePrefix;

    public MvelCompilerContext(TypeResolver typeResolver) {
        this(typeResolver, null);
    }

    public MvelCompilerContext(TypeResolver typeResolver, String scopeSuffix ) {
        this.typeResolver = typeResolver;
        this.scopeSuffix = isEmpty( scopeSuffix ) ? null : scopeSuffix;
    }

    public MvelCompilerContext addDeclaration(String name, Class<?> clazz) {
        declarations.put(name, new Declaration(name, clazz));
        return this;
    }

    public Optional<Declaration> findDeclarations(String name) {
        Declaration d = declarations.get(name);
        if (d == null && scopeSuffix != null) {
            d = declarations.get( name + scopeSuffix );
        }
        return Optional.ofNullable(d);
    }

    public Optional<Class<?>> findEnum(String name) {
        try {
            return Optional.of(typeResolver.resolveType(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public Class<?> resolveType(String name) {
        try {
            return typeResolver.resolveType(name);
        } catch (ClassNotFoundException e) {
            throw new MvelCompilerException(e);
        }
    }

    public void setRootPattern(Type rootPattern) {
        this.rootPattern = Optional.of(rootPattern);
    }

    public Optional<Type> getRootPattern() {
        return rootPattern;
    }

    public String getRootTypePrefix() {
        return rootTypePrefix;
    }

    public void setRootTypePrefix(String rootTypePrefix) {
        this.rootTypePrefix = rootTypePrefix;
    }
}
