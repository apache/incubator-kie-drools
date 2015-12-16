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

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ProjectJavaCompiler;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
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
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
                                       Options xjcOpts,
                                       String systemId) throws IOException {
        InputSource source = new InputSource( new CachingRewindableReader( resource.getReader() ) );
        source.setSystemId( systemId.trim().startsWith( "." ) ? systemId : "." + systemId );

        xjcOpts.addGrammar( source );

        try {
            xjcOpts.parseArguments( new String[]{"-npa"} );
        } catch ( BadCommandLineException e ) {
            throw new IllegalArgumentException( "Unable to parse arguments",
                                                e );
        }

        ErrorReceiver errorReceiver = new JaxbErrorReceiver4Drools();

        Model model = ModelLoader.load( xjcOpts,
                                        new JCodeModel(),
                                        errorReceiver );

        model.generateCode( xjcOpts, errorReceiver );

        MapVfsCodeWriter codeWriter = new MapVfsCodeWriter();
        model.codeModel.build( xjcOpts.createCodeWriter( codeWriter ) );

        MemoryResourceReader src = new MemoryResourceReader();

        boolean useProjectClassLoader = kBuilder.getRootClassLoader() instanceof ProjectClassLoader;

        List<String> classNames = new ArrayList<String>();
        List<String> srcNames = new ArrayList<String>();

        for ( Entry<String, byte[]> entry : codeWriter.getMap().entrySet() ) {
            String name = entry.getKey();

            int dotPos = name.lastIndexOf( '.' );
            String pkgName = name.substring( 0, dotPos );

            if ( !name.endsWith( "package-info.java" ) ) {
                classNames.add( pkgName );
            }

            dotPos = pkgName.lastIndexOf( '.' );
            if ( dotPos != -1 ) {
                pkgName = pkgName.substring( 0, dotPos );
            }

            PackageRegistry pkgReg = kBuilder.getPackageRegistry( pkgName );
            if ( pkgReg == null ) {
                kBuilder.addPackage( new PackageDescr( pkgName ) );
                pkgReg = kBuilder.getPackageRegistry( pkgName );
            }

            if (useProjectClassLoader) {
                String srcName = convertToResource( entry.getKey() );
                src.add( srcName, entry.getValue() );
                srcNames.add( srcName );
            } else {
                JavaDialect dialect = (JavaDialect) pkgReg.getDialectCompiletimeRegistry().getDialect( "java" );
                dialect.addSrc( convertToResource( entry.getKey() ),
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

        return classNames.toArray( new String[classNames.size()] );
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
                                Options xjcOpts,
                                String systemId) throws IOException {
        return addXsdModel( resource, (KnowledgeBuilderImpl)kbuilder, xjcOpts, systemId );
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

    public static class MapVfsCodeWriter extends CodeWriter {

        private final Map<String, byte[]> map;

        private ByteArrayOutputStream     currentBaos;
        private String                    currentPath;

        public MapVfsCodeWriter() {
            this.map = new LinkedHashMap<String, byte[]>();
        }

        public OutputStream openBinary(JPackage pkg,
                                       String fileName) throws IOException {
            String pkgName = pkg.name();

            if ( pkgName.length() != 0 ) {
                pkgName += '.';
            }

            if ( this.currentBaos != null ) {
                this.currentBaos.close();
                this.map.put( this.currentPath,
                              this.currentBaos.toByteArray() );
            }

            this.currentPath = pkgName + fileName;
            this.currentBaos = new ByteArrayOutputStream();

            return new FilterOutputStream( this.currentBaos ) {
                public void close() {
                    // don't let this stream close
                }
            };
        }

        public void close() throws IOException {
            if ( this.currentBaos != null ) {
                this.currentBaos.close();
                this.map.put( this.currentPath,
                              this.currentBaos.toByteArray() );
            }
        }

        public Map<String, byte[]> getMap() {
            return this.map;
        }

    }

    public static class JaxbErrorReceiver4Drools extends ErrorReceiver {

        public String stage = "processing";

        public void warning(SAXParseException e) {
            e.printStackTrace();
        }

        public void error(SAXParseException e) {
            e.printStackTrace();
        }

        public void fatalError(SAXParseException e) {
            e.printStackTrace();
        }

        public void info(SAXParseException e) {
            e.printStackTrace();
        }
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
