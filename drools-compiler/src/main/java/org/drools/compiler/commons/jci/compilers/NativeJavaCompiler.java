package org.drools.compiler.commons.jci.compilers;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.commons.jci.problems.CompilationProblemHandler;
import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.util.ClassUtils;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.regex.Pattern;

import static org.drools.core.util.ClassUtils.convertResourceToClassName;

public class NativeJavaCompiler extends AbstractJavaCompiler {

    private final JavaCompilerSettings settings = new JavaCompilerSettings();

    public JavaCompilerSettings createDefaultSettings() {
        return this.settings;
    }

    private static class InternalClassLoader extends ClassLoader {

        InternalClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    @Override
    public CompilationResult compile(String[] pResourcePaths,
                                     ResourceReader pReader,
                                     ResourceStore pStore,
                                     ClassLoader pClassLoader,
                                     JavaCompilerSettings pSettings) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        MemoryFileManager fileManager = new MemoryFileManager(compiler.getStandardFileManager(diagnostics, null, null), pClassLoader);

        final List<JavaFileObject> units = new ArrayList<JavaFileObject>();
        for (final String sourcePath : pResourcePaths) {
            units.add(new CompilationUnit(sourcePath, pReader));
        }

        if (compiler.getTask(null, fileManager, diagnostics, null, null, units).call()) {
            for (CompilationOutput compilationOutput : fileManager.getOutputs()) {
                pStore.write(compilationOutput.getBinaryName().replace('.', '/') + ".class", compilationOutput.toByteArray());
            }
            return new CompilationResult(new CompilationProblem[0]);
        }

        List<Diagnostic<? extends JavaFileObject>> problems = diagnostics.getDiagnostics();
        CompilationProblem[] result = new CompilationProblem[problems.size()];
        for (int i = 0; i < problems.size(); i++) {
            result[i] = new NativeCompilationProblem((Diagnostic<JavaFileObject>)problems.get(i));
        }
        return new CompilationResult(result);
    }

    private static class CompilationUnit extends SimpleJavaFileObject {
        private final String content;
        private final String name;

        CompilationUnit(String name, String content) {
            super(URI.create("memo:///" + name), Kind.SOURCE);
            this.content = content;
            this.name = name;
        }

        CompilationUnit(String name, ResourceReader pReader) {
            this(name, new String(pReader.getBytes(name)));
        }

        @Override
        public CharSequence getCharContent(boolean encodingErrors) throws IOException {
            return content;
        }
    }

    private interface DroolsJavaFileObject extends JavaFileObject {
        String getBinaryName();
    }

    private static class CompilationOutput extends SimpleJavaFileObject implements DroolsJavaFileObject {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final String binaryName;

        CompilationOutput(String name, Kind kind) {
            super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
            this.binaryName = name;
        }

        byte[] toByteArray() {
            return baos.toByteArray();
        }

        public String getBinaryName() {
            return binaryName;
        }

        String getPackageName() {
            int lastDot = binaryName.lastIndexOf('.');
            return lastDot > 0 ? binaryName.substring(0, lastDot) : binaryName;
        }

        @Override
        public ByteArrayOutputStream openOutputStream() {
            return baos;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(toByteArray());
        }
    }

    private static class CompilationInput extends SimpleJavaFileObject implements DroolsJavaFileObject {
        private final String binaryName;
        private final InputStream is;

        protected CompilationInput(String name, InputStream is) {
            super(URI.create("memo:///" + name), Kind.CLASS);
            this.binaryName = name.replace('/', '.').substring(0, name.length()-6);
            this.is = is;
        }

        public String getBinaryName() {
            return binaryName;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return is;
        }
    }

    private static class CustomJavaFileObject implements DroolsJavaFileObject {
        private final String binaryName;
        private final URI uri;
        private final String name;

        public CustomJavaFileObject(String binaryName, URI uri) {
            this.uri = uri;
            this.binaryName = binaryName;
            // for FS based URI the path is not null, for JAR URI the scheme specific part is not null
            name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath();
        }

        public URI toUri() {
            return uri;
        }

        public InputStream openInputStream() throws IOException {
            return uri.toURL().openStream(); // easy way to handle any URI!
        }

        public OutputStream openOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        public String getName() {
            return name;
        }

        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException();
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            throw new UnsupportedOperationException();
        }

        public Writer openWriter() throws IOException {
            throw new UnsupportedOperationException();
        }

        public long getLastModified() {
            return 0;
        }

        public boolean delete() {
            throw new UnsupportedOperationException();
        }

        public Kind getKind() {
            return Kind.CLASS;
        }

        public boolean isNameCompatible(String simpleName, Kind kind) {
            String baseName = simpleName + kind.extension;
            return kind.equals(getKind())
                   && (baseName.equals(getName())
                       || getName().endsWith("/" + baseName));
        }

        public NestingKind getNestingKind() {
            throw new UnsupportedOperationException();
        }

        public Modifier getAccessLevel() {
            throw new UnsupportedOperationException();
        }

        public String getBinaryName() {
            return binaryName;
        }


        @Override
        public String toString() {
            return "CustomJavaFileObject{" +
                   "uri=" + uri +
                   '}';
        }
    }

    private static class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final List<CompilationOutput> outputs = new ArrayList<CompilationOutput>();
        private final ClassLoader classLoader;

        MemoryFileManager(JavaFileManager fileManager, ClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            return file instanceof DroolsJavaFileObject ? ((DroolsJavaFileObject) file).getBinaryName() : super.inferBinaryName(location, file);
        }

        @Override
        public CompilationOutput getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind, FileObject source) {
            CompilationOutput compilationOutput = new CompilationOutput(name, kind);
            outputs.add(compilationOutput);
            return compilationOutput;
        }

        @Override
        public boolean hasLocation(Location location) {
            // we don't care about source and other location types - not needed for compilation
            return location == StandardLocation.CLASS_PATH || location == StandardLocation.PLATFORM_CLASS_PATH;
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
            Iterable<JavaFileObject> fileManagerList = super.list(location, packageName, kinds, recurse);
            if (location != StandardLocation.CLASS_PATH || packageName.startsWith("java.") || packageName.equals("java")) {
                return fileManagerList;
            }
            List<JavaFileObject> externalClasses = findCompiledClassInPackage(packageName);
            externalClasses.addAll(findClassesInExternalJars(packageName));
            return externalClasses.isEmpty() ? fileManagerList : new AggregatingIterable<JavaFileObject>(fileManagerList, externalClasses);
        }

        private List<JavaFileObject> findCompiledClassInPackage(String packageName) {
            List<JavaFileObject> compiledList = new ArrayList<JavaFileObject>();
            if (classLoader instanceof ProjectClassLoader) {
                Map<String, byte[]> store = ((ProjectClassLoader) classLoader).getStore();
                if (store != null) {
                    for (Map.Entry<String, byte[]> entry : store.entrySet()) {
                        String className = convertResourceToClassName(entry.getKey());
                        if (className.startsWith(packageName) && className.substring(packageName.length()+1).indexOf('.') < 0) {
                            compiledList.add(new CompilationInput(entry.getKey(), new ByteArrayInputStream(entry.getValue())));
                        }
                    }
                }
            }
            return compiledList;
        }

        // This workaround is necessary when other jars are loaded in an external class loader
        // and is an optimization of the solution suggested in the following post
        // http://atamur.blogspot.it/2009/10/using-built-in-javacompiler-with-custom.html
        private List<JavaFileObject> findClassesInExternalJars(String packageName) {
            try {
                Enumeration<URL> urlEnumeration = classLoader.getResources(packageName.replace('.', '/'));
                if (!urlEnumeration.hasMoreElements()) {
                    return Collections.emptyList();
                }
                List<JavaFileObject> result = null;
                while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
                    URL packageFolderURL = urlEnumeration.nextElement();
                    if (!new File(packageFolderURL.getFile()).isDirectory()) {
                        if (result == null) {
                            result = processJar(packageFolderURL);
                        } else {
                            List<JavaFileObject> classesInJar = processJar(packageFolderURL);
                            if (classesInJar != null) {
                                result.addAll(classesInJar);
                            }
                        }
                    }
                }
                return result == null ? Collections.<JavaFileObject>emptyList() : result;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }

        private List<JavaFileObject> processJar(URL packageFolderURL) throws IOException {
            String jarUri = packageFolderURL.toExternalForm();
            int separator = jarUri.indexOf('!');
            if (separator >= 0) {
                jarUri = jarUri.substring(0, separator);
            }

            JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName.length()+1;

            List<JavaFileObject> result = null;
            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) {
                String name = entryEnum.nextElement().getName();
                if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(".class")) {
                    URI uri = URI.create(jarUri + "!/" + name);
                    String binaryName = name.substring(0, name.length()-6).replace('/', '.');
                    if (result == null) {
                        result = new ArrayList<JavaFileObject>();
                    }
                    result.add(new CustomJavaFileObject(binaryName, uri));
                }
            }
            return result;
        }

        List<CompilationOutput> getOutputs() {
            return outputs;
        }
    }

    public static class AggregatingIterable<T> implements Iterable<T> {

        private final Iterable<T> i1;
        private final Iterable<T> i2;

        public AggregatingIterable(Iterable<T> i1, Iterable<T> i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        public Iterator<T> iterator() {
            return new AggregatingIterator<T>(i1 == null ? null : i1.iterator(), i2 == null ? null : i2.iterator());
        }
    }

    public static class AggregatingIterator<T> implements Iterator<T> {
        private final Iterator<T> i1;
        private final Iterator<T> i2;
        private boolean iteratingFirst = true;

        public AggregatingIterator(Iterator<T> i1, Iterator<T> i2) {
            this.i1 = i1;
            this.i2 = i2;
            if (i1 == null || !i1.hasNext()) {
                iteratingFirst = false;
            }
        }

        public boolean hasNext() {
            return iteratingFirst ? i1.hasNext() : i2 != null && i2.hasNext();
        }

        public T next() {
            if (iteratingFirst) {
                T next = i1.next();
                iteratingFirst = i1.hasNext();
                return next;
            }
            return i2.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}