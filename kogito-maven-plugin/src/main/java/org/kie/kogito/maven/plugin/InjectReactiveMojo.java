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
package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.drools.core.phreak.ReactiveObject;

@Mojo(name = "injectreactive",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
//@Execute(goal = "injectreactive",
//        phase = LifecyclePhase.COMPILE)
public class InjectReactiveMojo extends AbstractKieMojo {

    private List<File> sourceSet = new ArrayList<>();
    
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
        debugPrintPackageRegexps(packageRegExps);
        
        // Perform a depth first search for sourceSet
        searchForSourceFiles(outputDirectory);

        getLog().info( "Starting InjectReactive enhancement for classes on " + outputDirectory );
        final ClassPool classPool = new ClassPool( true ); // 'true' will append classpath for Object.class.

        appendClasspathForOutputDirectory(classPool);
        appendClasspathForReactiveObject(classPool);
        appendClasspathForDependencies(classPool);
        
        final BytecodeInjectReactive enhancer = BytecodeInjectReactive.newInstance(classPool);
        
        for ( File file : sourceSet ) {
            final CtClass ctClass = toCtClass( file, classPool );
            if ( ctClass == null ) {
                continue;
            }

            getLog().info( "Evaluating class [" + ctClass.getName() + "]" );
            getLog().info( ctClass.getPackageName() );
            getLog().info( "" + Collections.singletonList(packageRegExps));
            if (isPackageNameIncluded(ctClass.getPackageName(), packageRegExps) ) {
                try {
                    byte[] enhancedBytecode = enhancer.injectReactive(ctClass.getName());
                    writeOutEnhancedClass( enhancedBytecode, ctClass, file );

                    getLog().info( "Successfully enhanced class [" + ctClass.getName() + "]" );
                } catch (Exception e) {
                    getLog().error( "ERROR while trying to enhanced class [" + ctClass.getName() + "]" );
                    if (failOnError) {
                        throw new MojoExecutionException("ERROR while trying to enhanced class [" + ctClass.getName() + "]", e);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private void searchForSourceFiles(final File rootDirectory) {
        if ( !rootDirectory.exists() ) {
            getLog().info( "Skipping InjectReactive enhancement plugin execution since there is no classes dir " + outputDirectory );
            return;
        }
        walkDir( rootDirectory );
        if ( sourceSet.isEmpty() ) {
            getLog().info( "Skipping InjectReactive enhancement plugin execution since there are no classes to enhance on " + outputDirectory );
        }
    }

    private void debugPrintPackageRegexps(final List<String> packageRegExps) {
        if (getLog().isDebugEnabled()) {
            for (String prefix : packageRegExps) {
                getLog().debug(" "+prefix);
            }
        }
    }

    private void appendClasspathForOutputDirectory(final ClassPool classPool) throws MojoExecutionException {
        // Need to append classpath for the project itself output directory for dependencies between POJOs of the project itself.
        try {
            getLog().info("Adding to ClassPool the classpath: " + outputDirectory.getAbsolutePath());
            classPool.appendClassPath(outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            getLog().error( "Unable to append path for outputDirectory : "+outputDirectory );
            if (failOnError) {
                throw new MojoExecutionException("Unable to append path for outputDirectory : "+outputDirectory, e);
            }
        }
    }

    private void appendClasspathForReactiveObject(final ClassPool classPool) throws MojoExecutionException {
        // Uses the JAR of the kie-maven-plugin for this
        try {
            String aname = ReactiveObject.class.getPackage().getName().replaceAll("\\.", "/") + "/" +  ReactiveObject.class.getSimpleName()+".class";
            getLog().info("Resolving ReactiveObject from : "+aname);
            // The ReactiveObject shall be resolved by using the JAR of the kie-maven-plugin hence asking the ClassLoader of the kie-maven-plugin to resolve it
            String apath = Thread.currentThread().getContextClassLoader().getResource( aname).getPath();
            getLog().info(".. as in resource: " + apath );
            String path = null;
            if (apath.contains("!")) {
                path = apath.substring(0, apath.indexOf('!'));
            } else {
                path = "file:"+apath.substring(0, apath.indexOf(aname));
            }
            getLog().info(".. as in file path: " + path );

            File f = new File(new URI(path));

            getLog().info("Adding to ClassPool the classpath: " + f.getAbsolutePath());
            classPool.appendClassPath(f.getAbsolutePath());
        } catch (NotFoundException | URISyntaxException e) {
            getLog().error( "Unable to locate path for ReactiveObject." );
            if (failOnError) {
                throw new MojoExecutionException("Unable to locate path for ReactiveObject.", e);
            }
        }
    }

    private void appendClasspathForDependencies(final ClassPool classPool) throws MojoExecutionException {
        for ( URL url : dependenciesURLs() ) {
            try {
                getLog().info("Adding to ClassPool the classpath from dependencies: " + url.getPath());
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
    }
    
    private CtClass toCtClass(File file, ClassPool classPool) throws MojoExecutionException {
        try (final InputStream is = new FileInputStream( file.getAbsolutePath() )) {
            return classPool.makeClass( is );
        } catch (IOException e) {
            String msg = "Javassist unable to load class in preparation for enhancing: " + file.getAbsolutePath();
            if ( failOnError ) {
                throw new MojoExecutionException( msg, e );
            }
            getLog().warn( msg );
            return null;
        }
    }

    private List<URL> dependenciesURLs() throws MojoExecutionException {
        // HHH-10145 Add dependencies to classpath as well - all but the ones used for testing purposes
        MavenProject project = ( (MavenProject) getPluginContext().get( "project" ) );
        if ( project != null ) {
            return dependenciesURLsFromProject(project);
        } else {
            return new ArrayList<>();
        }
    }

    private List<URL> dependenciesURLsFromProject(final MavenProject mavenProject) throws MojoExecutionException {
        // Prefer execution project when available (it includes transient dependencies)
        List<URL> urls = new ArrayList<>();
        MavenProject executionProject = mavenProject.getExecutionProject();
        Set<Artifact> artifacts = ( executionProject != null ? executionProject.getArtifacts() : mavenProject.getArtifacts() );
        for ( Artifact a : artifacts ) {
            if ( !Artifact.SCOPE_TEST.equals( a.getScope() ) ) {
                try {
                    urls.add( a.getFile().toURI().toURL() );
                    getLog().debug( "Adding classpath entry for dependency " + a.getId() );
                } catch (MalformedURLException e) {
                    String msg = "Unable to resolve URL for dependency " + a.getId() + " at " + a.getFile().getAbsolutePath();
                    if ( failOnError ) {
                        throw new MojoExecutionException( msg, e );
                    }
                    getLog().warn( msg );
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
                pathname -> ( pathname.isFile() && pathname.getName().endsWith( ".class" ) ),
                pathname -> ( pathname.isDirectory() )
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
            } else {
                getLog().error( "Unable to delete class file [" + ctClass.getName() + "]" );
            }
        } catch (IOException e) {
            getLog().warn( "Problem preparing class file for writing out enhancements [" + ctClass.getName() + "]" );
        }

        try (FileOutputStream outputStream = new FileOutputStream( file, false )) {
            outputStream.write( enhancedBytecode );
            outputStream.flush();
        } catch (IOException e) {
            String msg = String.format( "Error writing to enhanced class [%s] to file [%s]", ctClass.getName(), file.getAbsolutePath() );
            if ( failOnError ) {
                throw new MojoExecutionException( msg, e );
            }
            getLog().warn( msg );
        } finally {
            ctClass.detach();
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
