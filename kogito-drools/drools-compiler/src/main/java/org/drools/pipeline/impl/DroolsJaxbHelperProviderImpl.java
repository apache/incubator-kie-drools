package org.drools.pipeline.impl;

/*
 * Copyright 2005 JBoss Inc
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.help.DroolsJaxbHelperProvider;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.command.runtime.BatchExecutionCommand;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.ModifyCommand.SetterImpl;
import org.drools.common.DefaultFactHandle;
import org.drools.common.DisconnectedFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.xml.jaxb.util.JaxbListWrapper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;

public class DroolsJaxbHelperProviderImpl
    implements
    DroolsJaxbHelperProvider {
	
	private static final String[] JAXB_ANNOTATED_CMD = {BatchExecutionCommand.class.getName(),
														SetGlobalCommand.class.getName(),
														GetGlobalCommand.class.getName(),
														FireAllRulesCommand.class.getName(),
														InsertElementsCommand.class.getName(),
														InsertObjectCommand.class.getName(),
														ModifyCommand.class.getName(),
														SetterImpl.class.getName(),
														QueryCommand.class.getName(),
														RetractCommand.class.getName(),
														AbortWorkItemCommand.class.getName(),
														SignalEventCommand.class.getName(),
														StartProcessCommand.class.getName(),
														BatchExecutionCommand.class.getName(),
														ExecutionResultImpl.class.getName() ,
														DefaultFactHandle.class.getName(),
														JaxbListWrapper.class.getName(),
														DisconnectedFactHandle.class.getName(),
														FlatQueryResults.class.getName()
														};
	
    public static String[] addXsdModel(Resource resource,
                                PackageBuilder pkgBuilder,
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

        List<String> classNames = new ArrayList<String>();
        for ( Entry<String, byte[]> entry : codeWriter.getMap().entrySet() ) {
            String name = entry.getKey();
            //if (name.endsWith("ObjectFactory.java")) {
            //  continue;
            //}
            
            String pkgName = null;
            int dotPos = name.lastIndexOf( '.' );
            pkgName = name.substring( 0,
                                      dotPos );

            if ( !name.endsWith( "package-info.java" ) ) {
                classNames.add( pkgName );
            }

            dotPos = pkgName.lastIndexOf( '.' );
            if ( dotPos != -1 ) {
                pkgName = pkgName.substring( 0,
                                             dotPos );
            }

            PackageRegistry pkgReg = pkgBuilder.getPackageRegistry( pkgName );
            if ( pkgReg == null ) {
                pkgBuilder.addPackage( new PackageDescr( pkgName ) );
                pkgReg = pkgBuilder.getPackageRegistry( pkgName );
            }

            JavaDialect dialect = (JavaDialect) pkgReg.getDialectCompiletimeRegistry().getDialect( "java" );
            dialect.addSrc( convertToResource( entry.getKey() ),
                            entry.getValue() );
        }

        pkgBuilder.compileAll();
        pkgBuilder.updateResults();

        return classNames.toArray( new String[classNames.size()] );
    }
	
    public String[] addXsdModel(Resource resource,
                                KnowledgeBuilder kbuilder,
                                Options xjcOpts,
                                String systemId) throws IOException {
        PackageBuilder pkgBuilder = ((KnowledgeBuilderImpl) kbuilder).pkgBuilder;
        return addXsdModel( resource, pkgBuilder, xjcOpts, systemId );
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
		ClassLoader classLoader = ((InternalRuleBase) ((KnowledgeBaseImpl) kbase)
				.getRuleBase()).getRootClassLoader();
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
