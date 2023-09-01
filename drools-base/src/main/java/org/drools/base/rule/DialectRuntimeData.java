package org.drools.base.rule;

import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.accessor.ReadAccessor;

public interface DialectRuntimeData extends Cloneable {
    void removeRule( KnowledgePackageImpl pkg, RuleImpl rule );

    void removeFunction( KnowledgePackageImpl pkg, Function function );

    void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData );

    void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData, boolean excludeDeclaredClasses );

    boolean isDirty();

    void setDirty( boolean dirty );

    void reload();

    DialectRuntimeData clone( DialectRuntimeRegistry registry, ClassLoader rootClassLoader);

    DialectRuntimeData clone( DialectRuntimeRegistry registry, ClassLoader rootClassLoader, boolean excludeDeclaredClasses );

    void onAdd( DialectRuntimeRegistry dialectRuntimeRegistry, ClassLoader rootClassLoader );

    void onRemove();

    void onBeforeExecute();

    default void resetParserConfiguration() { }

    default void compile(ReadAccessor reader) {
        throw new UnsupportedOperationException();
    }

    default public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    default boolean remove(String typeClassName) {
        throw new UnsupportedOperationException();
    }

    default ClassLoader getRootClassLoader() {
        throw new UnsupportedOperationException();
    }
}
