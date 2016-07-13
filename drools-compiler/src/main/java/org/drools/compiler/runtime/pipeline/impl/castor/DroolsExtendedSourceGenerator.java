/**
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

// Parts of this file are modified code from org.exolab.castor.builder.SourceGenerator.
// Thus, the following license also applies:

/**
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and notices. Redistributions
 * must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote products derived from this Software
 * without prior written permission of Intalio, Inc. For written permission, please contact
 * info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab" nor may "Exolab" appear in
 * their names without prior written permission of Intalio, Inc. Exolab is a registered trademark of
 * Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INTALIO, INC. OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2003 (C) Intalio, Inc. All Rights Reserved.
 *
 * This file was originally developed by Keith Visco during the course of employment at Intalio Inc.
 * All portions of this file developed by Keith Visco after Jan 19 2005 are Copyright (C) 2005 Keith
 * Visco. All Rights Reserved.
 *
 * $Id$
 */
package org.drools.compiler.runtime.pipeline.impl.castor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.castor.xml.InternalContext;
import org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl;
import org.exolab.castor.builder.BuilderConfiguration;
import org.exolab.castor.builder.SourceGenerator;
import org.exolab.castor.builder.conflictresolution.InformViaLogClassNameCRStrategy;
import org.exolab.castor.builder.printing.JClassPrinter;
import org.exolab.castor.builder.printing.JClassPrinterFactory;
import org.exolab.castor.builder.printing.JClassPrinterFactoryRegistry;
import org.exolab.castor.builder.printing.StandardJClassPrinterFactory;
import org.exolab.castor.util.NestedIOException;
import org.exolab.castor.util.dialog.ConsoleDialog;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLException;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.SchemaContext;
import org.exolab.castor.xml.schema.SchemaContextImpl;
import org.exolab.castor.xml.schema.reader.Sax2ComponentReader;
import org.exolab.castor.xml.schema.reader.SchemaUnmarshaller;
import org.exolab.castor.xml.util.XMLClassDescriptorImpl;
import org.exolab.javasource.JClass;
import org.exolab.javasource.JComment;
import org.exolab.javasource.JMethod;
import org.exolab.javasource.JSourceWriter;
import org.kie.api.KieBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXParseException;

/**
 * This class extends (hacks? :/) the {@link SourceGenerator} implementation in order to
 * <ul>
 * <li>print generated classes into a Map<String, byte[]> instead of the filesystem</li>
 * <li>make the Map<String, byte[]> easily available (otherwise we have to chain through several instances: generator -> factory -> printer.map)</li>
 * <li>make the Map<String, byte[]> available to the calling code so that the classes can be added as resources to the {@link KieBase}</li>
 * </ul>
 */
public class DroolsExtendedSourceGenerator extends SourceGenerator {

    private JClassPrinterFactoryRegistry _overridenJclassPrinterFactoryRegistry;
    private final Map<String, byte[]> map;

    public DroolsExtendedSourceGenerator() {
        super();
        map = new HashMap<String, byte[]>();

        setNameConflictStrategy(InformViaLogClassNameCRStrategy.NAME);
        Properties overrideProps = new Properties();
        overrideProps.setProperty(
                // - Register this class as the "standard" JClassPrinterFactory
                // - By doing this, we are overriding the default settings,
                //   which use the StandardJClassPrinterFactory class
                BuilderConfiguration.Property.JCLASSPRINTER_FACTORIES, DroolsJClassPrinterFactory.class.getName());

        // use boxed primitives method for generated classes
        overrideProps.setProperty(BuilderConfiguration.Property.WRAPPER, "true");

        // Make sure that castor does NOT generate a dialog (@#$%!) when running into name conflicts
        overrideProps.setProperty(
                BuilderConfiguration.Property.NAME_CONFLICT_STRATEGIES,
                InformViaLogClassNameCRStrategy.class.getName());
        // Not sure this does anything.. but better safe than sorry.
        overrideProps.setProperty(
                BuilderConfiguration.Property.AUTOMATIC_CONFLICT_RESOLUTION,
                "true");

        // make sure that only types (and not elements as well -- which can cause all sorts of conflicts) are mapped to classes
        overrideProps.setProperty(
                BuilderConfiguration.Property.JAVA_CLASS_MAPPING,
                "type");

        setDefaultProperties(overrideProps);
    }

    /**
     * Returns the registry for {@link JClassPrinterFactory} instances.
     *
     * @return the registry for {@link JClassPrinterFactory} instances.
     */
    @Override
    public JClassPrinterFactoryRegistry getJClassPrinterFactoryRegistry() {
        if (_overridenJclassPrinterFactoryRegistry == null) {
            // This is a hack around the castor code.. sorry!
            Properties overrideProps = new Properties();
            overrideProps.setProperty(
                    // - Register this class as the "standard" JClassPrinterFactory
                    // - By doing this, we are overriding the default settings,
                    //   which use the StandardJClassPrinterFactory class
                    BuilderConfiguration.Property.JCLASSPRINTER_FACTORIES, DroolsJClassPrinterFactory.class.getName());

            setDefaultProperties(overrideProps);

            // uses the JCLASSPRINTER_FACTORIES property  that we just set
            _overridenJclassPrinterFactoryRegistry = new JClassPrinterFactoryRegistry(this);
        }
        return _overridenJclassPrinterFactoryRegistry;
    }

    public void generateSource(InputSource source) throws IOException {
        // The packagename is filled in in the following method
        generateSource(source, null);

        // retrieve generated classes
        Map<String, byte[]> printerFactoryMap = DroolsJClassPrinterFactory.getLastInstance().getMap();
        this.map.putAll(printerFactoryMap);
    }

    /**
     * Creates Java Source code (Object model) for the given XML Schema. Parses the schema provided by
     * the InputSource and then calls {@link #generateSource(Schema, String)} to actually generate the
     * source.
     *
     * @param source - the InputSource representing the XML schema.
     * @param packageName the package for the generated source files
     * @throws IOException if an IOException occurs writing the new source files
     */
    @Override
    public void generateSource(InputSource source, String packageName) throws IOException {
        // -- get default parser from Configuration
        Parser parser = null;
        try {
            parser = getField("_internalContext", InternalContext.class).getParser();
        } catch (RuntimeException rte) {
            // ignore
        }

        ConsoleDialog _dialog = getField("_dialog", ConsoleDialog.class);
        if (parser == null) {
            _dialog.notify("fatal error: unable to create SAX parser.");
            return;
        }

        SchemaContext schemaContext = new SchemaContextImpl();
        SchemaUnmarshaller schemaUnmarshaller = null;
        try {
            schemaUnmarshaller = new SchemaUnmarshaller(schemaContext);
        } catch (XMLException e) {
            // --The default constructor cannot throw exception so this should never happen
            throw new RuntimeException("Unable to create " + SchemaUnmarshaller.class.getSimpleName(), e);
        }

        Sax2ComponentReader handler = new Sax2ComponentReader(schemaUnmarshaller);
        parser.setDocumentHandler(handler);
        parser.setErrorHandler(handler);

        // Drools: we always fail on the first error (no reference to the private SourceGenerator._failOnFirstError field)
        try {
            parser.parse(source);
        } catch (java.io.IOException ioe) {
            _dialog.notify("error reading XML Schema file");
            throw ioe;
        } catch (org.xml.sax.SAXException sx) {
            Exception except = sx.getException();
            if (except == null) {
                except = sx;
            }

            if (except instanceof SAXParseException) {
                SAXParseException spe = (SAXParseException) except;
                _dialog.notify("SAXParseException: " + spe);
                _dialog.notify(" - occured at line ");
                _dialog.notify(Integer.toString(spe.getLineNumber()));
                _dialog.notify(", column ");
                _dialog.notify(Integer.toString(spe.getColumnNumber()));
            } else {
                except.printStackTrace();
            }
            String msg = "Source Generator: schema parser threw an Exception";
            throw new RuntimeException(msg, sx);
        }

        Schema schema = schemaUnmarshaller.getSchema();

        try {
            schema.validate();
        } catch (ValidationException vx) {
            throw new NestedIOException(vx);
        }

        // Drools: retrieve target namespace and convert it to the package name
        String targetNamespace = schema.getTargetNamespace();
        packageName = XmlnsNameConverter.toPackageName(targetNamespace);

        generateSource(schema, packageName);
    } // -- generateSource

    public <F> F getField(String fieldName, Class<F> fieldType) {
        try {
            Field field = SourceGenerator.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (F) field.get(this);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e ) {
            throw new IllegalStateException("Unable to access field " + SourceGenerator.class.getName() + "." + fieldName, e  );
        }
    }

    public Map<String, byte[]> getMap() {
        return this.map;
    }

    // JClassPrinterFactory class -----------------------------------------------------------------------------------------------

    /**
     * {@link JClassPrinterFactory} instance that returns a memory VFS-based {@link JClassPrinter}
     * instances. This will then be the default {@link JClassPrinterFactory} instance used for code
     * generation.
     * </p>
     * This replaces the {@link StandardJClassPrinterFactory} instance in the castor code.
     * </p>
     * This is an internal class so that we can easily transfer the class info (String/byte[])
     * when generated to the Map<String, byte[]> field in the parent class.
     */
    public static class DroolsJClassPrinterFactory implements JClassPrinterFactory {

        private static final ThreadLocal<DroolsJClassPrinterFactory> threadLocal = new ThreadLocal<>();

        private final Map<String, byte[]> printerFactoryMap;

        /**
         * The name of the factory.
         */
        private static final String NAME = "standard";

        public DroolsJClassPrinterFactory() {
            threadLocal.set(this);
            printerFactoryMap = new HashMap<>();
        }

        public static DroolsJClassPrinterFactory getLastInstance() {
            DroolsJClassPrinterFactory instance = threadLocal.get();
            threadLocal.set(null);
            return instance;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.exolab.castor.builder.printing.JClassPrinterFactory#getJClassPrinter()
         */
        public JClassPrinter getJClassPrinter() {
            return new DroolsMemoryVfsJClassPrinter(printerFactoryMap);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.exolab.castor.builder.printing.JClassPrinterFactory#getName()
         */
        public String getName() {
            return NAME;
        }

        public Map<String, byte[]> getMap() {
            return this.printerFactoryMap;
        }

    }

    // JClassPrinter class -----------------------------------------------------------------------------------------------

    /**
     * Prints a given {@link JClass} to the file system using the
     * {@link JClass#print(org.exolab.javasource.JSourceWriter)} method.
     * </p>
     * This is an internal class so that we can easily transfer the class info (String/byte[])
     * when generated to the Map<String, byte[]> field in the parent class.
     * </p>
     * Then, via that field (see the {@link DroolsExtendedSourceGenerator#getMap()} method)
     * we can then pass all of the information back to the {@link DroolsJaxbHelperProviderImpl} instance.
     */
    private static class DroolsMemoryVfsJClassPrinter implements JClassPrinter {

        private final Map<String, byte[]> map;
        private ByteArrayOutputStream currentBaos;
        private String currentPath;

        public DroolsMemoryVfsJClassPrinter(Map<String, byte[]> parentMap) {
            this.map = parentMap;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.exolab.castor.builder.printing.JClassPrinter#printClass(org.exolab.javasource.JClass,
         *      java.lang.String, java.lang.String, java.lang.String)
         */
        public void printClass(final JClass jClass, final String outputDir, final String lineSeparator, final String header) {

            // generated by castor, but we don't need it..
            String superClass = jClass.getSuperClassQualifiedName();
            if (superClass != null && superClass.equals(XMLClassDescriptorImpl.class.getName())) {
                return;
            }

            // == copied from the castor code == //

            // add header
            JComment comment = new JComment(JComment.HEADER_STYLE);
            comment.appendComment(header);
            jClass.setHeader(comment);

            // == end copy == //

            // Delete extra methods
            String[] extraMethods = {"isValid", "marshal", "unmarshal", "validate"
            };

            for (int i = 0; i < extraMethods.length; ++i) {
                for( JMethod method : jClass.getMethods() ) {
                    if( method.getName().startsWith(extraMethods[i]) ) {
                        jClass.removeMethod(method);
                    }
                }
            }

            // print
            print(jClass, outputDir, lineSeparator);
        }

        /**
         * Prints the source code for this JStructure to the destination directory. Subdirectories will be
         * created if necessary for the package.
         *
         * @param destDir Directory name to use as the root directory for all output.
         * @param lineSeparator The line separator to use at the end of each line. If null, then the
         *        default line separator for the runtime platform will be used.
         * @param jclass
         */
        public final void print(JClass jclass, final String destDir, final String lineSeparator) {
            if (currentBaos == null) {
                currentBaos = new ByteArrayOutputStream();
                currentPath = jclass.getName();
            }

            OutputStreamWriter baosWriter = new OutputStreamWriter(currentBaos) {

                @Override
                public void close() throws IOException {
                    super.close();
                    byte[] existingClassContent = map.put(currentPath, currentBaos.toByteArray());
                    currentBaos = null;
                    if (existingClassContent != null) {
                        // This should never be able to happen
                        throw new IllegalStateException("Duplicate class content added for file '" + currentPath + "'");
                    }
                }
            };

            JSourceWriter jsw = new JSourceWriter(baosWriter);

            if (lineSeparator == null) {
                jsw.setLineSeparator(System.getProperty("line.separator"));
            } else {
                jsw.setLineSeparator(lineSeparator);
            }
            jclass.print(jsw);

            // class put into map
            jsw.close();
        }

        public Map<String, byte[]> getMap() {
            return this.map;
        }
    }
}
