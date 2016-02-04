/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.api.builder.helper;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.util.IoUtils;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.MavenRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.kie.scanner.MavenRepository.getMavenRepository;

/**
 * This is the main class where all interfaces and code comes together. 
 */
final class KieModuleDeploymentHelperImpl extends FluentKieModuleDeploymentHelper implements SingleKieModuleDeploymentHelper {
    
    /**
     * package scope: Because users will do very unexpected things.
     */

    private KieModuleDeploymentConfig config;
    
    KieModuleDeploymentHelperImpl() { 
        config = new KieModuleDeploymentConfig();
    }

    /**
     * Fluent API
     */
    
    @Override
    public FluentKieModuleDeploymentHelper setGroupId(String groupId) { 
        assertNotNull( groupId, "groupId");
        this.config.setGroupId(groupId);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper setArtifactId(String artifactId) { 
        assertNotNull( artifactId, "artifactId");
        this.config.setArtifactId(artifactId);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper setVersion(String version) { 
        assertNotNull( version, "version");
        this.config.setVersion(version);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper setKBaseName(String kbaseName) {
        assertNotNull(kbaseName, "kbase name");
        this.config.setKbaseName(kbaseName);
        return this;
    }

    @Override
    public FluentKieModuleDeploymentHelper setKieSessionname(String ksessionName) {
        assertNotNull(ksessionName, "ksession name");
        this.config.setKsessionName(ksessionName);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper setResourceFilePaths(List<String> resourceFilePaths) {
        assertNotNull( resourceFilePaths, "resourceFilePaths");
        this.config.resourceFilePaths.addAll(resourceFilePaths);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper addResourceFilePath(String... resourceFilePath) {
        assertNotNull( resourceFilePath, "resourceFilePath");
        for( int i = 0; i < resourceFilePath.length; ++i ) { 
            assertNotNull( resourceFilePath[i], "resourceFilePath[" + i + "]");
            this.config.resourceFilePaths.add(resourceFilePath[i]);
        }
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper setClasses(List<Class<?>> classesForKjar) { 
        assertNotNull( classesForKjar, "classesForKjar");
        this.config.classes.addAll(classesForKjar);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper addClass(Class<?>... classForKjar) { 
        assertNotNull( classForKjar, "classForKjar");
        for( int i = 0; i < classForKjar.length; ++i ) { 
            assertNotNull( classForKjar[i], "classForKjar[" + i + "]");
            this.config.classes.add(classForKjar[i]);
        }
        return this;
    }

    @Override
    public FluentKieModuleDeploymentHelper setDependencies(List<String> dependencies) { 
        assertNotNull( dependencies, "dependencies");
        this.config.dependencies.addAll(dependencies);
        return this;
    }
    
    @Override
    public FluentKieModuleDeploymentHelper addDependencies(String... dependency) { 
        assertNotNull( dependency, "dependency");
        for( int i = 0; i < dependency.length; ++i ) { 
            assertNotNull( dependency[i], "dependency[" + i + "]");
            this.config.dependencies.add(dependency[i]);
        }
        return this;
    }
    
    @Override
    public KieModuleModel getKieModuleModel() {
        return config.getKieProject();
    }

    @Override
    public FluentKieModuleDeploymentHelper resetHelper() {
        this.config = new KieModuleDeploymentConfig();
        return this;
    }

    @Override
    public KieModule createKieJar() {
        config.checkComplete();
        return internalCreateKieJar(config.getReleaseId(), config.getKbaseName(), config.getKsessionName(), 
                config.resourceFilePaths, config.classes, config.dependencies);
    }

    @Override
    public void createKieJarAndDeployToMaven() {
        config.checkComplete();
        internalCreateAndDeployKjarToMaven(config.getReleaseId(), config.getKbaseName(), config.getKsessionName(), 
                config.resourceFilePaths, config.classes, 
                config.dependencies);
    }
    
    private static void assertNotNull(Object obj, String name) { 
        if( obj == null ) { 
            throw new IllegalArgumentException("Null " + name + " arguments are not accepted!");
        }
    }
    
    /**
     * General/Single API
     */
    
    @Override
    public KieModule createKieJar(String groupId, String artifactId, String version,
            String kbaseName,
            String ksessionName, 
            List<String> resourceFilePaths ) { 
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        return internalCreateKieJar(releaseId, kbaseName, ksessionName, resourceFilePaths, null, null);
    }
    
    @Override
    public KieModule createKieJar(String groupId, String artifactId, String version,
            String kbaseName,
            String ksessionName, 
            List<String> bpmnFilePaths, 
            List<Class<?>> classes) {
        
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        return internalCreateKieJar(releaseId, kbaseName, ksessionName, bpmnFilePaths, classes, null);
    }
    
    @Override
    public KieModule createKieJar(String groupId, String artifactId, String version,
            String kbaseName,
            String ksessionName, 
            List<String> bpmnFilePaths, 
            List<Class<?>> classes, 
            List<String> dependencies) {
        
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        return internalCreateKieJar(releaseId, kbaseName, ksessionName, bpmnFilePaths, classes, dependencies);
    }
    
    @Override
    public void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, String kbaseName,
            String ksessionName, List<String> resourceFilePaths) {
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        internalCreateAndDeployKjarToMaven(releaseId, kbaseName, ksessionName, resourceFilePaths, null, null);
    }

    @Override
    public void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, String kbaseName,
            String ksessionName, List<String> resourceFilePaths, List<Class<?>> classesForKjar) {
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        internalCreateAndDeployKjarToMaven(releaseId, kbaseName, ksessionName, resourceFilePaths, classesForKjar, null);
    }

    @Override
    public void createKieJarAndDeployToMaven(String groupId, String artifactId, String version, String kbaseName,
            String ksessionName, List<String> resourceFilePaths, List<Class<?>> classesForKjar, List<String> dependencies) {
        ReleaseId releaseId = new ReleaseIdImpl(groupId, artifactId, version);
        internalCreateAndDeployKjarToMaven(releaseId, kbaseName, ksessionName, resourceFilePaths, classesForKjar, dependencies);
    }
    
    /**
     * Internal methods
     */

    /**
     * Create a KJar and deploy it to maven.
     * 
     * @param releaseId The {@link ReleaseId} of the jar.
     * @param kbaseName The name to use for the (default) {@link KieBase} in the kmodule.xml.
     * @param ksessionName The name to use for the (default) {@link KieSession} in the kmodule.xml.
     * @param resourceFilePaths A (possibly null) list of files to be added to the kjar.
     * @param classes
     * @param dependencies
     */
    private synchronized void internalCreateAndDeployKjarToMaven(ReleaseId releaseId,
            String kbaseName, 
            String ksessionName,
            List<String> resourceFilePaths,
            List<Class<?>> classes, 
            List<String> dependencies) {
        
        InternalKieModule kjar = (InternalKieModule) internalCreateKieJar(releaseId, 
                kbaseName, ksessionName, 
                resourceFilePaths, classes,
                dependencies);
        
        String pomFileName = MavenRepository.toFileName(releaseId, null) + ".pom";
        File pomFile = new File(System.getProperty("java.io.tmpdir"), pomFileName);
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(config.pomText.getBytes(IoUtils.UTF8_CHARSET));
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to write pom.xml to temporary file : " + ioe.getMessage(), ioe);
        }
    
        MavenRepository repository = getMavenRepository();
        repository.installArtifact(releaseId, kjar, pomFile);
    }

    /**
     * Create a KJar for deployment;
     * 
     * @param releaseId Release (deployment) id.
     * @param resourceFilePaths List of resource file paths
     * @param kbaseName The name of the {@link KieBase}
     * @param ksessionName The name of the {@link KieSession}.
     * @param dependencies List of dependencies to add
     * 
     * @return The {@link InternalKieModule} which represents the KJar.
     */
    private synchronized KieModule internalCreateKieJar(ReleaseId releaseId, 
            String kbaseName,
            String ksessionName,
            List<String> resourceFilePaths, 
            List<Class<?>> classes, 
            List<String> dependencies) {
        
        
        ReleaseId [] releaseIds = { };
        if( dependencies != null && dependencies.size() > 0 ) { 
            List<ReleaseId> depReleaseIds = new ArrayList<ReleaseId>();
            for( String dep : dependencies ) { 
                String [] gav = dep.split(":");
                if( gav.length != 3 ) { 
                    throw new IllegalArgumentException("Dependendency id '" + dep  + "' does not conform to the format <groupId>:<artifactId>:<version> (Classifiers are not accepted).");
                }
                depReleaseIds.add(new ReleaseIdImpl(gav[0], gav[1], gav[2]));
            }
            releaseIds = depReleaseIds.toArray(new ReleaseId[depReleaseIds.size()]);
        }
        config.pomText = getPomText(releaseId, releaseIds);
        
        KieFileSystem kfs = createKieFileSystemWithKProject(kbaseName, ksessionName);
        kfs.writePomXML(this.config.pomText);
    
        List<KJarResource> resourceFiles = loadResources(resourceFilePaths);
        for (KJarResource resource : resourceFiles) {
            kfs.write("src/main/resources/" + kbaseName + "/" + resource.name, resource.content);
        }
    
        if( classes != null ) { 
            for( Class<?> userClass : classes ) {
                addClass(userClass, kfs);
            }
        }
    
        KieBuilder kieBuilder = config.getKieServicesInstance().newKieBuilder(kfs);
        int buildMsgs = 0;
        for( Message buildMsg : kieBuilder.buildAll().getResults().getMessages() ) { 
           System.out.println( buildMsg.getPath() + " : " + buildMsg.getText() );
           ++buildMsgs;
        }
        if( buildMsgs > 0 ) { 
            throw new RuntimeException("Unable to build KieModule, see the " + buildMsgs + " messages above.");
        }
        
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    /**
     * Create the {@link KieFileSystem} instance to store the content going into the KJar.
     * 
     * @param kbaseName
     * @param ksessionName
     * @return
     */
    private KieFileSystem createKieFileSystemWithKProject(String kbaseName, String ksessionName) {
        KieModuleModel kproj = config.getKieProject();
        KieFileSystem kfs = config.getKieServicesInstance().newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    /**
     * Create the pom that will be placed in the KJar.
     * 
     * @param releaseId The release (deployment) id.
     * @param dependencies The list of dependendencies to be added to the pom
     * @return A string representation of the pom.
     */
    private static String getPomText(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
                + "  <modelVersion>4.0.0</modelVersion>\n" 
                + "\n" 
                + "  <groupId>" + releaseId.getGroupId() + "</groupId>\n"
                + "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" 
                + "  <version>" + releaseId.getVersion() + "</version>\n" + "\n";
        
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        
        pom += "</project>";
        
        return pom;
    }

    /**
     * Create a list of {@link KJarResource} instances with the process files to be included in the KJar.
     * 
     * @return The list of {@link KJarResource} instances.
     * @throws Exception
     */
    static List<KJarResource> internalLoadResources(String path, boolean fromDir) throws Exception {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        List<KJarResource> output = new ArrayList<KJarResource>();
        URL url = KieModuleDeploymentHelperImpl.class.getResource(path);
        if (fromDir) {
            if (url == null) {
                File folder = new File(path);
                if (folder.exists()) {
                    for (File folderFile : folder.listFiles()) {
                        if (folderFile.isDirectory()) {
                            continue;
                        }
                        String content = convertFileToString(new FileInputStream(folderFile));
                        output.add(new KJarResource(folderFile.getName(), content));
                    }
                } else {
                    throw new IllegalArgumentException("Directory + '" + path + "' does not exist.");
                }
            } else if ("file".equals(url.getProtocol())) {
                File folder = new File(url.toURI());
                if (folder.isDirectory()) {
                    for (File folderFile : folder.listFiles()) {
                        if (folderFile.getName().endsWith(".class")
                                || folderFile.isDirectory() ) {
                            continue;
                        }
                        String content = convertFileToString(new FileInputStream(folderFile));
                        output.add(new KJarResource(folderFile.getName(), content));
                    }
                } else {
                    throw new IllegalStateException("'" + path + "' is not an existing directory.");
                }
            } else if ("jar".equals(url.getProtocol())) {
                String urlString = url.toExternalForm();
                int jarPathIndex = urlString.lastIndexOf(".jar!") + 4;
                String resourcePath = urlString.substring(jarPathIndex + 1);
                if (resourcePath.startsWith("/")) {
                    resourcePath = resourcePath.substring(1);
                }
                int depth = resourcePath.split("/").length + 1;

                String jarUrlString = urlString.substring("jar:".length(), jarPathIndex);
                url = new URL(jarUrlString);
                ZipInputStream zip = new ZipInputStream(url.openStream());

                ZipEntry ze = zip.getNextEntry();
                while (ze != null) {
                    String name = ze.getName();
                    if (name.startsWith(resourcePath) && !name.endsWith("/")
                            && (name.split("/").length == depth)) {
                        String shortName = name.substring(name.lastIndexOf("/") + 1);
                        String content = convertFileToString(zip);
                        output.add(new KJarResource(shortName, content));
                    }
                    ze = zip.getNextEntry();
                }
            } else {
                throw new IllegalArgumentException("Unable to find resource directory '" + path + "'");
            }
        } else {
            InputStream is = KieModuleDeploymentHelperImpl.class.getResourceAsStream(path);
            if (is == null) {
                is = new FileInputStream(new File(path));
            }
            String content = convertFileToString(is);
            String name = path.substring(path.lastIndexOf("/") + 1);
            output.add(new KJarResource(name, content));
        }
        return output;
    }
   
    /**
     * Find the resource files specified and create {@link KJarResource} instances from them.
     * 
     * @param resourceFilePaths A list of resource files or directories containing resource files
     * @return A List of {@link KJarResource} instances representing the given list of resource files
     */
    private static List<KJarResource> loadResources(List<String> resourceFilePaths) { 
        List<KJarResource> kjarResources = new ArrayList<KieModuleDeploymentHelperImpl.KJarResource>();
        for( String filePath : resourceFilePaths ) { 
            if( filePath.endsWith("/") ) {
                try {
                    kjarResources.addAll(internalLoadResources(filePath, true));
                } catch (Exception e) {
                    throw new RuntimeException("Unable to load resources from " + filePath + ": " + e.getMessage(), e);
                }
            } else { 
                try {
                    kjarResources.addAll(internalLoadResources(filePath, false));
                } catch (FileNotFoundException fnfe) {
                    throw new RuntimeException("No file found at '" + filePath + "' -- if it's a directory, please add a " + File.separator + " to the end of the path.");
                } catch (Exception e) {
                    throw new RuntimeException("Unable to load resource from '" + filePath + "'", e);
                }
            }
        }
        
        return kjarResources;
    }
    
    private static String convertFileToString(InputStream in) {
        InputStreamReader input = new InputStreamReader(in, IoUtils.UTF8_CHARSET);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter output = new OutputStreamWriter(baos, IoUtils.UTF8_CHARSET);
        char[] buffer = new char[4096];
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(baos.toByteArray(), IoUtils.UTF8_CHARSET);
    }

    /**
     * Add class to the {@link KieFileSystem}.
     * 
     * @param userClass The class to be added.
     * @param kfs The {@link KieFileSystem}
     */
    private static void addClass(Class<?> userClass, KieFileSystem kfs) {
        String classSimpleName = userClass.getSimpleName();
        
        URL classFileUrl = userClass.getResource(classSimpleName + ".class");
        if( classFileUrl == null ) { 
            throw new RuntimeException("Class " + userClass.getCanonicalName() + " can not be found on the classpath." );
        }
    
        byte[] classByteCode = null;
        if ("file".equalsIgnoreCase(classFileUrl.getProtocol())) {
            File classFile = new File(classFileUrl.getPath());
            if( ! classFile.exists() ) { 
                throw new RuntimeException("Unable to find path for class " + userClass.getCanonicalName() 
                        + " that should be here: " + classFileUrl.toExternalForm());
            }
    
            try {
                classByteCode = readStream(new FileInputStream(classFile));
            } catch (Exception e) {
                throw new RuntimeException("Unable to read in " + classFile.getAbsolutePath(), e);
            }
            if( classByteCode.length == 0 ) { 
                throw new RuntimeException("No bytes retrieved from " + classFile.getAbsolutePath() );
            }
        } else if ("jar".equalsIgnoreCase(classFileUrl.getProtocol())) {
            // file:/opt/mavenRepository/org/kie/tests/kie-wb-tests-base/6.0.0-SNAPSHOT/kie-wb-tests-base-6.0.0-SNAPSHOT.jar!/org/kie/tests/wb/base/test/MyType.class
            String path = classFileUrl.getPath();
            int bangIndex = path.indexOf('!');
            String jarPath = path.substring("file:".length(), bangIndex);
            String classPath = path.substring(bangIndex+2); // no base /
            
            try { 
                ZipFile zip = new ZipFile(new File(jarPath));
                ZipEntry entry = zip.getEntry(classPath);
                InputStream zipStream = zip.getInputStream(entry);
                classByteCode = readStream(zipStream);
            } catch (Exception e) {
                throw new RuntimeException("Unable to read from " + jarPath, e);
            }
        }

        String pkgFolder = userClass.getPackage().toString();

        // @#$%ing Eclipe classloader!! The following 2 lines "normalize" the package name from what Eclipse (and other IDE's?) do with it
        pkgFolder = pkgFolder.replace("package ", "");
        pkgFolder = pkgFolder.replaceAll(",.+$", "");
        pkgFolder = pkgFolder.replaceAll("\\.", "/" );
        String classFilePath = pkgFolder + "/" + classSimpleName + ".class";

        if( classFilePath.contains(" " ) ) { 
            throw new RuntimeException("Invalid class name ('" + classFilePath + "'), contact the developers.");
        }
    
        kfs.write(classFilePath, classByteCode);
    }

    private static byte[] readStream(InputStream ios) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
        return baos.toByteArray();
    }

    static class KJarResource {
        public String name;
        public String content;
    
        public KJarResource(String name, String content) {
            this.content = content;
            this.name = name;
        }
    }





}
