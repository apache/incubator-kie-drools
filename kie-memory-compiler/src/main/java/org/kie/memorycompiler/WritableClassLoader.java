package org.kie.memorycompiler;

public interface WritableClassLoader {
    Class<?> writeClass(String name, byte[] bytecode);

    default ClassLoader asClassLoader() {
        return (ClassLoader) this;
    }

    static WritableClassLoader asWritableClassLoader(ClassLoader classLoader) {
        return classLoader instanceof WritableClassLoader ? ( WritableClassLoader ) classLoader : new WritableClassLoaderImpl( classLoader );
    }

    class WritableClassLoaderImpl extends ClassLoader implements WritableClassLoader {

        WritableClassLoaderImpl(ClassLoader classLoader) {
            super(classLoader);
        }

        public Class<?> writeClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

        @Override
        public ClassLoader asClassLoader() {
            return getParent();
        }
    }
}
