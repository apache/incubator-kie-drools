/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.compiler.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.SAXParser;

import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.SemanticModules;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlPackageReader {
    private ExtensibleXmlParser parser;

    private PackageDescr        packageDescr;

    public XmlPackageReader(final SemanticModules modules) {
        this( modules, null );
    }

    public XmlPackageReader(final SemanticModules modules, final SAXParser parser) {
        if ( parser == null ) {
            this.parser = new ExtensibleXmlParser();
        } else {
            this.parser = new ExtensibleXmlParser( parser );
        }
        this.parser.setSemanticModules( modules );
    }
    
    public ExtensibleXmlParser getParser() {
        return this.parser;
    }

    /**
     * Read a <code>RuleSet</code> from a <code>Reader</code>.
     *
     * @param reader
     *            The reader containing the rule-set.
     *
     * @return The rule-set.
     */
    public PackageDescr read(final Reader reader) throws SAXException,
                                                 IOException {
        this.packageDescr = (PackageDescr) this.parser.read( reader );
        return this.packageDescr;
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputStream</code>.
     *
     * @param inputStream
     *            The input-stream containing the rule-set.
     *
     * @return The rule-set.
     */
    public PackageDescr read(final InputStream inputStream) throws SAXException,
                                                           IOException {
        this.packageDescr = (PackageDescr) this.parser.read( inputStream );
        return this.packageDescr;
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputSource</code>.
     *
     * @param in
     *            The rule-set input-source.
     *
     * @return The rule-set.
     */
    public PackageDescr read(final InputSource in) throws SAXException,
                                                  IOException {
        this.packageDescr = (PackageDescr) this.parser.read( in );
        return this.packageDescr;
    }

    void setPackageDescr(final PackageDescr packageDescr) {
        this.packageDescr = packageDescr;
    }

    public PackageDescr getPackageDescr() {
        return this.packageDescr;
    }
}
