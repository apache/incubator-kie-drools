package org.drools.mvelcompiler.context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.util.TypeResolver;
import org.drools.util.StreamUtils;

public class DeclaredFunction {

    private final TypeResolver typeResolver;
    private final String name;
    private final String returnType;
    private final List<String> arguments;

    public DeclaredFunction(TypeResolver typeResolver, String name, String returnType, List<String> arguments) {
        this.typeResolver = typeResolver;
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public Optional<Class<?>> findReturnType() {
        return resolveType(returnType);
    }

    public List<Class<?>> findArgumentsType() {
        return arguments.stream().map(this::resolveType)
                .flatMap(StreamUtils::optionalToStream)
                .collect(Collectors.toList());
    }

    public Optional<Class<?>> resolveType(String name) {
        try {
            return Optional.ofNullable(typeResolver.resolveType(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
