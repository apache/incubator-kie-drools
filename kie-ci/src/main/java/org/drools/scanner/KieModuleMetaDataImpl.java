package org.drools.scanner;

import org.kie.builder.GAV;
import org.sonatype.aether.artifact.Artifact;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.scanner.ArtifactResolver.getResolverFor;

public class KieModuleMetaDataImpl implements KieModuleMetaData {

    private final ArtifactResolver artifactResolver;

    private final Map<String, Collection<String>> classes = new HashMap<String, Collection<String>>();

    private final Map<URI, File> jars = new HashMap<URI, File>();

    private URLClassLoader classLoader;

    private GAV gav;

    public KieModuleMetaDataImpl(GAV gav) {
        this.artifactResolver = getResolverFor(gav, false);
        this.gav = gav;
        init();
    }

    public KieModuleMetaDataImpl(File pomFile) {
        this.artifactResolver = getResolverFor(pomFile);
        init();
    }

    public Collection<String> getPackages() {
        return classes.keySet();
    }

    public Collection<String> getClasses(String packageName) {
        Collection<String> classesInPkg = classes.get(packageName);
        return classesInPkg != null ? classesInPkg : Collections.<String>emptyList();
    }

    public Class<?> getClass(String pkgName, String className) {
        try {
            return Class.forName(pkgName + "." + className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private ClassLoader getClassLoader() {
        if (classLoader == null) {
            URL[] urls = new URL[jars.size()];
            int i = 0;
            for (File jar : jars.values()) {
                try {
                    urls[i++] = jar.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            classLoader = new URLClassLoader(urls);
        }
        return classLoader;
    }

    private void init() {
        if (gav != null) {
            addArtifact(artifactResolver.resolveArtifact(gav.toString()));
        }
        for (DependencyDescriptor dep : artifactResolver.getAllDependecies()) {
            addArtifact(artifactResolver.resolveArtifact(dep.toString()));
        }
    }

    private void addArtifact(Artifact artifact) {
        if (artifact != null && artifact.getExtension() != null && artifact.getExtension().equals("jar")) {
            addJar(artifact.getFile());
        }
    }

    private void addJar(File jarFile) {
        URI uri = jarFile.toURI();
        if (!jars.containsKey(uri)) {
            jars.put(uri, jarFile);
            scanJar(jarFile);
        }
    }

    private void scanJar(File jarFile) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jarFile );
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                indexClass(entry.getName());
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    private void indexClass(String pathName) {
        if (!pathName.endsWith(".class")) {
            return;
        }

        int separator = pathName.lastIndexOf( '/' );
        String packageName = separator > 0 ? pathName.substring( 0, separator ).replace('/', '.') : "";
        String className = pathName.substring( separator + 1, pathName.length() - ".class".length() );

        Collection<String> pkg = classes.get(packageName);
        if (pkg == null) {
            pkg = new HashSet<String>();
            classes.put(packageName, pkg);
        }
        pkg.add(className);
    }
}
