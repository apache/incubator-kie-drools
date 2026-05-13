/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.memorycompiler.jdknative;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import org.kie.memorycompiler.AbstractJavaCompiler;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.KieMemoryCompilerException;
import org.kie.memorycompiler.StoreClassLoader;
import org.drools.util.PortablePath;
import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeJavaCompiler extends AbstractJavaCompiler {

    private static final Logger logger = LoggerFactory.getLogger(NativeJavaCompiler.class);

    private JavaCompilerFinder javaCompilerFinder;

	public JavaCompilerSettings createDefaultSettings() {
        return new JavaCompilerSettings();
    }
    
    public NativeJavaCompiler() {
    	this(new NativeJavaCompilerFinder());
    }

    NativeJavaCompiler(JavaCompilerFinder javaCompilerFinder) {
		this.javaCompilerFinder = javaCompilerFinder;
	}

	@Override
    public CompilationResult compile( String[] pResourcePaths,
                                      ResourceReader pReader,
                                      ResourceStore pStore,
                                      ClassLoader pClassLoader,
                                      JavaCompilerSettings pSettings) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = getJavaCompiler();

        if (pResourcePaths == null || pResourcePaths.length == 0) {
            return new CompilationResult( new CompilationProblem[0] );
        }

        try (StandardJavaFileManager jFileManager = compiler.getStandardFileManager(diagnostics, null, Charset.forName(pSettings.getSourceEncoding()))) {
            try {
                jFileManager.setLocation( StandardLocation.CLASS_PATH, pSettings.getClasspathLocations() );
                jFileManager.setLocation( StandardLocation.CLASS_OUTPUT, Collections.singletonList(new File("target/classes")) );
            } catch (IOException e) {
                // ignore if cannot set the classpath
            }

            try (MemoryFileManager fileManager = new MemoryFileManager( jFileManager, pClassLoader )) {
                final List<JavaFileObject> units = new ArrayList<>();
                for (final String sourcePath : pResourcePaths) {
                    units.add( new CompilationUnit( PortablePath.of(sourcePath), pReader ) );
                }

                Iterable<String> options = new NativeJavaCompilerSettings( pSettings ).toOptionsList();

                if ( compiler.getTask( null, fileManager, diagnostics, options, null, units ).call() ) {
                    for (CompilationOutput compilationOutput : fileManager.getOutputs()) {
                        pStore.write( compilationOutput.getBinaryName().replace( '.', '/' ) + ".class", compilationOutput.toByteArray() );
                    }
                    return new CompilationResult( new CompilationProblem[0] );
                }
            }

            List<Diagnostic<? extends JavaFileObject>> problems = diagnostics.getDiagnostics();
            CompilationProblem[] result = new CompilationProblem[problems.size()];
            for (int i = 0; i < problems.size(); i++) {
                result[i] = new NativeCompilationProblem( ( Diagnostic<JavaFileObject> ) problems.get( i ) );
            }

            return new CompilationResult( result );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    private JavaCompiler getJavaCompiler() {
        JavaCompiler compiler = null;
        Throwable cause = null;
        try {
            compiler = javaCompilerFinder.getJavaCompiler();
        } catch (Throwable ex) {
            cause = ex;
        }
        if (compiler != null) {
            return compiler;
        }
        String message = "Cannot find the System's Java compiler. " +
                "Please use JDK instead of JRE or add drools-ecj dependency to use in memory Eclipse compiler";
        if (cause == null) {
            throw new KieMemoryCompilerException(message);
        } else {
            throw new KieMemoryCompilerException(message, cause);
        }
    }

    private static class CompilationUnit extends SimpleJavaFileObject {

        public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

        private final String content;

        CompilationUnit(PortablePath path, String content) {
            super(URI.create("memo:///" + path.asString()), Kind.SOURCE);
            this.content = content;
        }

        CompilationUnit(PortablePath name, ResourceReader pReader) {
            this(name, new String(pReader.getBytes(name), UTF8_CHARSET));
        }

        @Override
        public CharSequence getCharContent(boolean encodingErrors) throws IOException {
            return content;
        }
    }

    private interface DroolsJavaFileObject extends JavaFileObject {
        String getBinaryName();
    }

    /**
     * A class file enumerated from a non-standard classpath URL (e.g. JBoss VFS).
     * Package-private so tests can drive {@link #listVfsChildren(URL, String)} directly.
     */
    static final class VfsClassResource {
        final String resourceName;
        final byte[] bytes;

        VfsClassResource(String resourceName, byte[] bytes) {
            this.resourceName = resourceName;
            this.bytes = bytes;
        }
    }

    /**
     * Reflective handles into JBoss VFS. Resolved lazily, atomically, and cached.
     * {@link #LOAD_FAILED} marks "we tried and the classes are not available" so we
     * don't repeatedly pay the {@link Class#forName} cost on non-JBoss runtimes.
     */
    private static final class VfsAccess {
        static final VfsAccess LOAD_FAILED = new VfsAccess(null, null, null, null);

        final Method getChild;
        final Method getChildren;
        final Method getName;
        final Method openStream;

        VfsAccess(Method getChild, Method getChildren, Method getName, Method openStream) {
            this.getChild = getChild;
            this.getChildren = getChildren;
            this.getName = getName;
            this.openStream = openStream;
        }

        static VfsAccess load() {
            try {
                Class<?> vfsClass = loadJbossClass("org.jboss.vfs.VFS");
                Class<?> vfClass = loadJbossClass("org.jboss.vfs.VirtualFile");
                return new VfsAccess(
                        vfsClass.getMethod("getChild", URI.class),
                        vfClass.getMethod("getChildren"),
                        vfClass.getMethod("getName"),
                        vfClass.getMethod("openStream"));
            } catch (ClassNotFoundException | NoSuchMethodException | LinkageError e) {
                logger.debug("JBoss VFS not available; non-jar/non-file classpath URLs will be skipped", e);
                return LOAD_FAILED;
            }
        }

        private static Class<?> loadJbossClass(String name) throws ClassNotFoundException {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException | LinkageError e) {
                // In modular containers the calling classloader may not see VFS even when
                // the TCCL does; also catch LinkageError because partial visibility can
                // surface as NoClassDefFoundError rather than ClassNotFoundException.
                ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                if (tccl == null || tccl == NativeJavaCompiler.class.getClassLoader()) {
                    if (e instanceof ClassNotFoundException) {
                        throw (ClassNotFoundException) e;
                    }
                    throw new ClassNotFoundException(name, e);
                }
                return Class.forName(name, true, tccl);
            }
        }
    }

    private static volatile VfsAccess vfsAccess;

    private static VfsAccess vfsAccess() {
        VfsAccess access = vfsAccess;
        if (access == null) {
            access = VfsAccess.load();
            vfsAccess = access;
        }
        return access == VfsAccess.LOAD_FAILED ? null : access;
    }

    /**
     * For test reset only — clears the cached VFS access so a unit test that installs
     * test-classpath {@code org.jboss.vfs.*} stubs can force a fresh resolution.
     */
    static void resetVfsAccessForTest() {
        vfsAccess = null;
    }

    /**
     * Enumerate {@code .class} resources from a non-standard classpath URL using JBoss VFS.
     * Returns {@code null} if VFS is not on the classpath, the URL is not a VFS package
     * directory, or enumeration fails. Package-private for direct testability.
     */
    @SuppressWarnings("unchecked")
    static List<VfsClassResource> listVfsChildren(URL packageFolderURL, String packageName) {
        VfsAccess access = vfsAccess();
        if (access == null) {
            return null;
        }
        try {
            Object virtualFile = access.getChild.invoke(null, packageFolderURL.toURI());
            if (virtualFile == null) {
                return null;
            }
            List<Object> children = (List<Object>) access.getChildren.invoke(virtualFile);
            if (children == null || children.isEmpty()) {
                return null;
            }
            List<VfsClassResource> result = new ArrayList<>();
            String resourcePrefix = packageName.replace('.', '/') + "/";
            for (Object child : children) {
                String name = (String) access.getName.invoke(child);
                if (!name.endsWith(".class")) {
                    continue;
                }
                byte[] bytes;
                try (InputStream is = (InputStream) access.openStream.invoke(child)) {
                    bytes = is.readAllBytes();
                }
                result.add(new VfsClassResource(resourcePrefix + name, bytes));
            }
            return result;
        } catch (Exception | LinkageError e) {
            logger.debug("Failed to enumerate VFS children for URL {} in package {}", packageFolderURL, packageName, e);
            return null;
        }
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
        private final List<CompilationOutput> outputs = new ArrayList<>();
        private final ClassLoader classLoader;

        MemoryFileManager(JavaFileManager fileManager, ClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
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
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
            Iterable<JavaFileObject> fileManagerList = super.list(location, packageName, kinds, recurse);
            if (location != StandardLocation.CLASS_PATH || packageName.startsWith("java.") || packageName.equals("java")) {
                return fileManagerList;
            }
            List<JavaFileObject> externalClasses = findCompiledClassInPackage(packageName);
            externalClasses.addAll(findClassesInExternalJars(packageName));
            return externalClasses.isEmpty() ? fileManagerList : new AggregatingIterable<>(fileManagerList, externalClasses);
        }

        private List<JavaFileObject> findCompiledClassInPackage(String packageName) {
            List<JavaFileObject> compiledList = new ArrayList<>();
            if (classLoader instanceof StoreClassLoader ) {
                Map<String, byte[]> store = ((StoreClassLoader) classLoader).getStore();
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
                List<JavaFileObject> result = new ArrayList<>();
                while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
                    URL packageFolderURL = urlEnumeration.nextElement();
                    File dir = toFile(packageFolderURL);
                    if (dir != null && dir.isDirectory()) {
                        List<JavaFileObject> classesInDir = processDirectory(dir, packageName);
                        if (classesInDir != null) {
                            result.addAll(classesInDir);
                        }
                    } else {
                        List<JavaFileObject> classesInJar = processJar(packageFolderURL, packageName);
                        if (classesInJar != null) {
                            result.addAll(classesInJar);
                        }
                    }
                }
                return result;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }

        /**
         * Resolve a classpath URL to a local {@link File}, handling URL-encoded paths
         * (spaces, non-ASCII characters, etc.). Returns {@code null} if the URL is not
         * a {@code file:} URL or cannot be converted (e.g. {@code vfs:} URLs).
         */
        private static File toFile(URL url) {
            if (!"file".equals(url.getProtocol())) {
                return null;
            }
            try {
                return new File(url.toURI());
            } catch (URISyntaxException | IllegalArgumentException e) {
                return new File(url.getFile());
            }
        }

        private List<JavaFileObject> processDirectory(File directory, String packageName) {
            File[] classFiles = directory.listFiles((dir, name) -> name.endsWith(".class"));
            if (classFiles == null || classFiles.length == 0) {
                return null;
            }
            List<JavaFileObject> result = new ArrayList<>();
            for (File classFile : classFiles) {
                String binaryName = packageName + "." + classFile.getName().substring(0, classFile.getName().length() - 6);
                result.add(new CustomJavaFileObject(binaryName, classFile.toURI()));
            }
            return result;
        }

        private List<JavaFileObject> processJar(URL packageFolderURL, String packageName) throws IOException {
            String jarUri = jarUri(packageFolderURL.toExternalForm());

            URLConnection urlConnection = packageFolderURL.openConnection();
            if (!(urlConnection instanceof JarURLConnection)) {
                return processNonStandardUrl(packageFolderURL, packageName);
            }

            JarURLConnection jarConn = (JarURLConnection) urlConnection;
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName.length()+1;

            List<JavaFileObject> result = null;
            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) {
                String name = entryEnum.nextElement().getName();
                if ( name.startsWith( rootEntryName ) && name.indexOf( '/', rootEnd ) == -1 && name.endsWith( ".class" ) ) {
                    URI uri = URI.create( jarUri + "!/" + name );
                    String binaryName = name.substring( 0, name.length() - 6 ).replace( '/', '.' );
                    if ( result == null ) {
                        result = new ArrayList<>();
                    }
                    result.add( new CustomJavaFileObject( binaryName, uri ) );
                }
            }
            return result;
        }

        private List<JavaFileObject> processNonStandardUrl(URL packageFolderURL, String packageName) {
            List<VfsClassResource> resources = listVfsChildren(packageFolderURL, packageName);
            if (resources == null || resources.isEmpty()) {
                return null;
            }
            List<JavaFileObject> result = new ArrayList<>(resources.size());
            for (VfsClassResource resource : resources) {
                result.add(new CompilationInput(resource.resourceName, new ByteArrayInputStream(resource.bytes)));
            }
            return result;
        }

        List<CompilationOutput> getOutputs() {
            return outputs;
        }

        private static String convertResourceToClassName(final String pResourceName) {
            return stripExtension(pResourceName).replace('/', '.');
        }

        private static String stripExtension(final String pResourceName) {
            final int i = pResourceName.lastIndexOf('.');
            return pResourceName.substring( 0, i );
        }
    }

    static String jarUri(String resourceUrl) {
        String jarUri = resourceUrl;
        int separator = jarUri.lastIndexOf('!');
        if (separator >= 0) {
            jarUri = jarUri.substring(0, separator);
        }
        return jarUri;
    }

    public static class AggregatingIterable<T> implements Iterable<T> {

        private final Iterable<T> i1;
        private final Iterable<T> i2;

        public AggregatingIterable(Iterable<T> i1, Iterable<T> i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        public Iterator<T> iterator() {
            return new AggregatingIterator<>(i1 == null ? null : i1.iterator(), i2 == null ? null : i2.iterator());
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
