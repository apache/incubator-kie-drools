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
package org.kie.maven.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.core.phreak.ReactiveObject;

import javassist.ClassPool;
import javassist.CtClass;

@Mojo(name = "injectreactive",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class InjectReactiveMojo extends AbstractKieMojo {

    private List<File> sourceSet = new ArrayList<File>();
    
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;
    
    @Parameter(alias = "instrument-enabled", property = "kie.instrument.enabled", defaultValue = "false")
    private boolean enabled;
    
    @Parameter(alias = "instrument-failOnError", property = "kie.instrument.failOnError", defaultValue = "true")
    private boolean failOnError;
    
    /*
     * DO NOT add a default to @Parameter annotation as it buggy to assign it regardless
     */
    @Parameter(alias = "instrument-packages", property = "kie.instrument.packages")
    private String[] instrumentPackages;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!enabled) {
            getLog().debug("Configuration for instrument-enabled is false, skipping goal 'injectreactive' execute() end.");
            return;
        }
        
        if (instrumentPackages.length == 0) {
            getLog().debug("No configuration passed for instrument-packages, default to '*' .");
            instrumentPackages = new String[]{"*"};
        }
        getLog().debug("Configured with resolved instrument-packages: "+Arrays.asList(instrumentPackages));
        List<String> packageRegExps = convertAllToPkgRegExps(instrumentPackages);
        for (String prefix : packageRegExps) {
            getLog().debug(" "+prefix);
        }
        
        // Perform a depth first search for sourceSet
        File root = outputDirectory;
        if ( !root.exists() ) {
            getLog().info( "Skipping InjectReactive enhancement plugin execution since there is no classes dir " + outputDirectory );
            return;
        }
        walkDir( root );
        if ( sourceSet.isEmpty() ) {
            getLog().info( "Skipping InjectReactive enhancement plugin execution since there are no classes to enhance on " + outputDirectory );
            return;
        }

        getLog().info( "Starting InjectReactive enhancement for classes on " + outputDirectory );
        final ClassLoader classLoader = toClassLoader( Collections.singletonList( root ) );
        
        
        final ClassPool classPool = new ClassPool( true ); // 'true' will append classpath for Object.class.
        // Need to append classpath for the project itself output directory for dependencies betweek Pojos of the project itself.
        try {
            getLog().info("Adding to ClassPool the classpath: " + outputDirectory.getAbsolutePath());
            classPool.appendClassPath(outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            getLog().error( "Unable to append path for outputDirectory : "+outputDirectory );
            if (failOnError) {
                throw new MojoExecutionException("Unable to append path for outputDirectory : "+outputDirectory, e);
            } else {
                return;
            }
        }
        // Append classpath for ReactiveObject.class by using the JAR of the kie-maven-plugin
        try {
            String aname = ReactiveObject.class.getPackage().getName().replaceAll("\\.", "/") + "/" +  ReactiveObject.class.getSimpleName()+".class";
            getLog().info("Resolving ReactiveObject from : "+aname);
            // The ReactiveObject shall be resolved by using the JAR of the kie-maven-plugin hence asking the ClassLoader of the kie-maven-plugin to resolve it
            String apath = Thread.currentThread().getContextClassLoader().getResource( aname).getPath();
            getLog().info(".. as in resource: " + apath );
            String path = null;
            if (apath.contains("!")) {
                path = apath.substring(0, apath.indexOf("!"));
            } else {
                path = "file:"+apath.substring(0, apath.indexOf(aname));
            }
            getLog().info(".. as in file path: " + path );
            
            File f = new File(new URI(path));
    
            getLog().info("Adding to ClassPool the classpath: " + f.getAbsolutePath());
            classPool.appendClassPath(f.getAbsolutePath());
        } catch (Exception e) {
            getLog().error( "Unable to locate path for ReactiveObject." );
            e.printStackTrace();
            if (failOnError) {
                throw new MojoExecutionException("Unable to locate path for ReactiveObject.", e);
            } else {
                return;
            }
        }
        // Append classpath for the project dependencies
        for ( URL url : dependenciesURLs() ) {
            try {
                getLog().info("Adding to ClassPool the classpath: " + url.getPath());
                classPool.appendClassPath(url.getPath());
            } catch (Exception e) {
                getLog().error( "Unable to append path for project dependency : "+url.getPath() );
                if (failOnError) {
                    throw new MojoExecutionException( "Unable to append path for project dependency : "+url.getPath(), e);
                } else {
                    return;
                }
            }
        }
        
        final BytecodeInjectReactive enhancer = BytecodeInjectReactive.newInstance(classPool);
        
        for ( File file : sourceSet ) {
            final CtClass ctClass = toCtClass( file, classPool );
            if ( ctClass == null ) {
                continue;
            }

            getLog().info( "Evaluating class [" + ctClass.getName() + "]" );
            getLog().info( ctClass.getPackageName() );
            getLog().info( ""+Arrays.asList( packageRegExps ) );
            if ( !isPackageNameIncluded(ctClass.getPackageName(), packageRegExps) ) {
                continue;
            }

            byte[] enhancedBytecode;
            try {
                enhancedBytecode = enhancer.injectReactive(ctClass.getName());
                
                writeOutEnhancedClass( enhancedBytecode, ctClass, file );

                getLog().info( "Successfully enhanced class [" + ctClass.getName() + "]" );
            } catch (Exception e) {
                getLog().error( "ERROR while trying to enhanced class [" + ctClass.getName() + "]" );
                e.printStackTrace();
                if (failOnError) {
                    throw new MojoExecutionException("ERROR while trying to enhanced class [" + ctClass.getName() + "]", e);
                } else {
                    return;
                }
            }
            
        }
    }
    
    private CtClass toCtClass(File file, ClassPool classPool) throws MojoExecutionException {
        try {
            final InputStream is = new FileInputStream( file.getAbsolutePath() );

            try {
                return classPool.makeClass( is );
            }
            catch (IOException e) {
                String msg = "Javassist unable to load class in preparation for enhancing: " + file.getAbsolutePath();
                if ( failOnError ) {
                    throw new MojoExecutionException( msg, e );
                }
                getLog().warn( msg );
                return null;
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException e) {
                    getLog().info( "Was unable to close InputStream : " + file.getAbsolutePath(), e );
                }
            }
        }
        catch (FileNotFoundException e) {
            // should never happen, but...
            String msg = "Unable to locate class file for InputStream: " + file.getAbsolutePath();
            if ( failOnError ) {
                throw new MojoExecutionException( msg, e );
            }
            getLog().warn( msg );
            return null;
        }
    }
    
    private ClassLoader toClassLoader(List<File> runtimeClasspath) throws MojoExecutionException {
        List<URL> urls = new ArrayList<URL>();
        for ( File file : runtimeClasspath ) {
            try {
                urls.add( file.toURI().toURL() );
                getLog().debug( "Adding classpath entry for classes root " + file.getAbsolutePath() );
            }
            catch (MalformedURLException e) {
                String msg = "Unable to resolve classpath entry to URL: " + file.getAbsolutePath();
                if ( failOnError ) {
                    throw new MojoExecutionException( msg, e );
                }
                getLog().warn( msg );
            }
        }

        urls.addAll( dependenciesURLs() );

        return new URLClassLoader( urls.toArray( new URL[urls.size()] ), null );
    }

    private List<URL> dependenciesURLs() throws MojoExecutionException {
        List<URL> urls = new ArrayList<>();
        // HHH-10145 Add dependencies to classpath as well - all but the ones used for testing purposes
        Set<Artifact> artifacts = null;
        MavenProject project = ( (MavenProject) getPluginContext().get( "project" ) );
        if ( project != null ) {
            // Prefer execution project when available (it includes transient dependencies)
            MavenProject executionProject = project.getExecutionProject();
            artifacts = ( executionProject != null ? executionProject.getArtifacts() : project.getArtifacts() );
        }
        if ( artifacts != null) {
            for ( Artifact a : artifacts ) {
                if ( !Artifact.SCOPE_TEST.equals( a.getScope() ) ) {
                    try {
                        urls.add( a.getFile().toURI().toURL() );
                        getLog().debug( "Adding classpath entry for dependency " + a.getId() );
                    }
                    catch (MalformedURLException e) {
                        String msg = "Unable to resolve URL for dependency " + a.getId() + " at " + a.getFile().getAbsolutePath();
                        if ( failOnError ) {
                            throw new MojoExecutionException( msg, e );
                        }
                        getLog().warn( msg );
                    }
                }
            }
        }
        return urls;
    }
    
    /**
     * Expects a directory.
     */
    private void walkDir(File dir) {
        walkDir(
                dir,
                new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return ( pathname.isFile() && pathname.getName().endsWith( ".class" ) );
                    }
                },
                new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return ( pathname.isDirectory() );
                    }
                }
        );
    }

    private void walkDir(File dir, FileFilter classesFilter, FileFilter dirFilter) {
        File[] dirs = dir.listFiles( dirFilter );
        for ( File dir1 : dirs ) {
            walkDir( dir1, classesFilter, dirFilter );
        }
        File[] files = dir.listFiles( classesFilter );
        Collections.addAll( this.sourceSet, files );
    }

    private void writeOutEnhancedClass(byte[] enhancedBytecode, CtClass ctClass, File file) throws MojoExecutionException{
        if ( enhancedBytecode == null ) {
            return;
        }
        try {
            if ( file.delete() ) {
                if ( !file.createNewFile() ) {
                    getLog().error( "Unable to recreate class file [" + ctClass.getName() + "]" );
                }
            }
            else {
                getLog().error( "Unable to delete class file [" + ctClass.getName() + "]" );
            }
        }
        catch (IOException e) {
            getLog().warn( "Problem preparing class file for writing out enhancements [" + ctClass.getName() + "]" );
        }

        try {
            FileOutputStream outputStream = new FileOutputStream( file, false );
            try {
                outputStream.write( enhancedBytecode );
                outputStream.flush();
            }
            catch (IOException e) {
                String msg = String.format( "Error writing to enhanced class [%s] to file [%s]", ctClass.getName(), file.getAbsolutePath() );
                if ( failOnError ) {
                    throw new MojoExecutionException( msg, e );
                }
                getLog().warn( msg );
            }
            finally {
                try {
                    outputStream.close();
                    ctClass.detach();
                }
                catch (IOException ignore) {
                }
            }
        }
        catch (FileNotFoundException e) {
            String msg = "Error opening class file for writing: " + file.getAbsolutePath();
            if ( failOnError ) {
                throw new MojoExecutionException( msg, e );
            }
            getLog().warn( msg );
        }
    }
    
    public static List<String> convertAllToPkgRegExps(String[] patterns) {
        List<String> result = new ArrayList<>();
        for (String p : patterns) {
            if ( p.equals("*") ) {
                result.add("^.*$");
            } else if ( !p.endsWith(".*") ) {
                // a pattern like com.acme should match for com.acme only (not the subpackages).
                result.add( "^" + p.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*") + "$" );
            } else if ( p.endsWith(".*") ) {
                // a pattern like com.acme.* should match for com.acme and all subpackages of com.acme.*
                result.add( "^" + p.substring(0, p.length()-2).replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*") + "$" );
                result.add( "^" + p.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*") + "$" );
            } else {
                // unexpected input will be passed as-is.
                result.add(p);  
            }
        }
        return result;
    }
    
    public static boolean isPackageNameIncluded(String packageName, List<String> regexps) {
        for (String r : regexps) {
            if (packageName.matches(r)) {
                return true;
            }
        }
        return false;
    }
}
