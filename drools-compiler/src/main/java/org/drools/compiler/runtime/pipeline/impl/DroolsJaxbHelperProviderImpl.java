/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.runtime.pipeline.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ProjectJavaCompiler;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.compiler.runtime.pipeline.impl.castor.DroolsExtendedSourceGenerator;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.DeleteCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.command.runtime.rule.ModifyCommand;
import org.drools.core.command.runtime.rule.ModifyCommand.SetterImpl;
import org.drools.core.command.runtime.rule.QueryCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.kie.api.io.Resource;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.help.DroolsJaxbHelperProvider;
import org.xml.sax.InputSource;

public class DroolsJaxbHelperProviderImpl
    implements DroolsJaxbHelperProvider {

    public static final String[] JAXB_ANNOTATED_CMD = {
            BatchExecutionCommandImpl.class.getName(),
            SetGlobalCommand.class.getName(),
            GetGlobalCommand.class.getName(),
            FireAllRulesCommand.class.getName(),
            InsertElementsCommand.class.getName(),
            InsertObjectCommand.class.getName(),
            ModifyCommand.class.getName(),
            SetterImpl.class.getName(),
            QueryCommand.class.getName(),
            DeleteCommand.class.getName(),
            AbortWorkItemCommand.class.getName(),
            SignalEventCommand.class.getName(),
            StartProcessCommand.class.getName(),
            BatchExecutionCommandImpl.class.getName(),
            ExecutionResultImpl.class.getName(),
            DefaultFactHandle.class.getName(),
            JaxbListWrapper.class.getName(),
            FlatQueryResults.class.getName(),
            CompleteWorkItemCommand.class.getName(),
            GetObjectsCommand.class.getName()};

    public static String[] addXsdModel(Resource resource,
                                       KnowledgeBuilderImpl kBuilder,
                                       String systemId) throws IOException {

        Map<String, byte[]> generatedClassesMap = generateXsdModelClasses(resource, systemId);

        List<String> classNames = addGeneratedClassesAndReturnClassNames(kBuilder, generatedClassesMap);
        return classNames.toArray( new String[classNames.size()] );
    }

    private static Map<String, byte[]> generateXsdModelClasses(Resource resource, String systemId) throws IOException {
        InputSource source = new InputSource( new CachingRewindableReader( resource.getReader() ) );
        source.setSystemId( systemId.trim().startsWith( "." ) ? systemId : "." + systemId );

        DroolsExtendedSourceGenerator sgen = new DroolsExtendedSourceGenerator();
        sgen.generateSource(source);
        return sgen.getMap();
    }

    private static List<String> addGeneratedClassesAndReturnClassNames(KnowledgeBuilderImpl kBuilder, Map<String, byte[]> classNameBytesMap) {
        boolean useProjectClassLoader = kBuilder.getRootClassLoader() instanceof ProjectClassLoader;

        MemoryResourceReader src = new MemoryResourceReader();

        List<String> classNames = new ArrayList<String>();
        List<String> srcNames = new ArrayList<String>();

        for ( Entry<String, byte[]> entry : classNameBytesMap.entrySet() ) {
            String name = entry.getKey();

            if ( !name.endsWith( "package-info.java" ) ) {
                classNames.add( name );
            }

            int dotPos = name.lastIndexOf( '.' );
            String pkgName = name.substring( 0, dotPos );
            if ( dotPos != -1 ) {
                pkgName = pkgName.substring( 0, dotPos );
            }

            // we need to ad ".java", otherwise (ECJ) compilation will throw errors
            // (not completely sure why? But that's how it works.. )
            name += ".java";
            PackageRegistry pkgReg = kBuilder.getPackageRegistry( pkgName );
            if ( pkgReg == null ) {
                kBuilder.addPackage( new PackageDescr( pkgName ) );
                pkgReg = kBuilder.getPackageRegistry( pkgName );
            }

            if (useProjectClassLoader) {
                String srcName = convertToResource( name );
                src.add( srcName, entry.getValue() );
                srcNames.add( srcName );
            } else {
                JavaDialect dialect = (JavaDialect) pkgReg.getDialectCompiletimeRegistry().getDialect( "java" );
                dialect.addSrc( convertToResource( name ),
                                entry.getValue() );
            }
        }

        if (useProjectClassLoader) {
            ProjectJavaCompiler compiler = new ProjectJavaCompiler(kBuilder.getBuilderConfiguration());
            List<KnowledgeBuilderResult> results = compiler.compileAll((ProjectClassLoader)kBuilder.getRootClassLoader(),
                                                                       srcNames,
                                                                       src);
            for (String className : classNames) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName( className, true, kBuilder.getRootClassLoader() );
                } catch (ClassNotFoundException e) {
                    continue;
                }
                String pkgName = className.substring( 0, className.lastIndexOf( '.' ) );
                PackageRegistry pkgReg = kBuilder.getPackageRegistry(pkgName);
                pkgReg.getPackage().addTypeDeclaration( new TypeDeclaration( clazz ) );
            }

            kBuilder.updateResults(results);
        } else {
            kBuilder.compileAll();
            kBuilder.updateResults();
        }

        return classNames;
    }

    public static JAXBContext createDroolsJaxbContext(List<String> classNames, Map<String, ?> properties) throws ClassNotFoundException, JAXBException {
        int i = 0;
        Class<?>[] classes = new Class[classNames.size() + JAXB_ANNOTATED_CMD.length];

        for (i = 0; i < classNames.size(); i++) {
            classes[i] = Class.forName(classNames.get(i));
        }
        int j = 0;
        for (i = classNames.size(); i < classes.length; i++, j++) {
            classes[i] = Class.forName(JAXB_ANNOTATED_CMD[j]);
        }
        return JAXBContext.newInstance(classes, properties);

    }

    public String[] addXsdModel(Resource resource,
                                KnowledgeBuilder kbuilder,
                                String systemId) throws IOException {
        return addXsdModel( resource, (KnowledgeBuilderImpl)kbuilder, systemId );
    }

    public JAXBContext newJAXBContext(String[] classNames,
                                          KnowledgeBase kbase) throws JAXBException {
        return newJAXBContext( classNames,
                            Collections.<String, Object> emptyMap(),
                            kbase );
    }

    public JAXBContext newJAXBContext(String[] classNames,
                                      Map<String, ? > properties,
                                      KnowledgeBase kbase) throws JAXBException {
        ClassLoader classLoader = ((InternalKnowledgeBase) kbase).getRootClassLoader();
        int i = 0;
        try {
            Class<?>[] classes = new Class[classNames.length
                    + JAXB_ANNOTATED_CMD.length];

            for (i = 0; i < classNames.length; i++) {
                classes[i] = classLoader.loadClass(classNames[i]);
            }
            int j = 0;
            for (i = classNames.length; i < classes.length; i++, j++) {
                classes[i] = classLoader.loadClass(JAXB_ANNOTATED_CMD[j]);
            }
            return JAXBContext.newInstance(classes, properties);
        } catch (ClassNotFoundException e) {
            throw new JAXBException("Unable to resolve class '" + classNames[i] + "'", e);
        }
    }

    private static String convertToResource(String string) {
        int lastDot = string.lastIndexOf( '.' );
        return string.substring( 0,
                                 lastDot ).replace( '.',
                                                    '/' ) + string.substring( lastDot,
                                                                              string.length() );
    }

    public static class CachingRewindableReader extends Reader {
        private Reader                 source;
        private boolean                sourceClosed;
        private RewindableStringReader cache;
        private StringBuilder          strBuilder;

        public CachingRewindableReader(Reader source) {
            this.source = source;
            this.strBuilder = new StringBuilder();
        }

        public int read(char[] cbuf,
                        int off,
                        int len) throws IOException {
            int value = 0;
            if ( this.cache == null ) {
                value = this.source.read( cbuf,
                                          off,
                                          len );
                if ( value != -1 ) {
                    // keep appening to the stringBuilder until we are at the end
                    this.strBuilder.append( cbuf,
                                            off,
                                            value );
                } else {
                    // we are at the end, so switch to cache
                    this.cache = new RewindableStringReader( strBuilder.toString() );
                }
            } else {
                value = this.cache.read( cbuf,
                                         off,
                                         len );
            }
            return value;
        }

        public void close() throws IOException {
            if ( !sourceClosed ) {
                // close the source, we only do this once.
                this.source.close();
                this.sourceClosed = true;
            }

            if ( cache == null ) {
                // switch to cache if we haven't already
                this.cache = new RewindableStringReader( strBuilder.toString() );
            } else {
                // reset the cache, so it can be read again.
                this.cache.reset();
            }
        }
    }

    public static class RewindableStringReader extends StringReader {
        public RewindableStringReader(String s) {
            super( s );
        }

        public void close() {
            try {
                reset();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
