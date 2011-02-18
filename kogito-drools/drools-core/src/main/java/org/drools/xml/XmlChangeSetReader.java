/*
 * Copyright 2010 JBoss Inc
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

package org.drools.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.SAXParser;

import org.drools.ChangeSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlChangeSetReader {
    private ExtensibleXmlParser parser;

    public XmlChangeSetReader(final SemanticModules modules) {
        this( modules, null );
    }

    public XmlChangeSetReader(final SemanticModules modules, final SAXParser parser) {
        if ( parser == null ) {
            this.parser = new ExtensibleXmlParser();
        } else {
            this.parser = new ExtensibleXmlParser( parser );
        }
        this.parser.setSemanticModules( modules );
    }
    
    public void setClassLoader(ClassLoader classLoader, Class clazz ) {
        this.parser.setClassLoader( classLoader );
        this.parser.getMetaData().put( "clazz", clazz );
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
    public ChangeSet read(final Reader reader) throws SAXException,
                                                 IOException {
        return (ChangeSet) this.parser.read( reader );
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputStream</code>.
     *
     * @param inputStream
     *            The input-stream containing the rule-set.
     *
     * @return The rule-set.
     */
    public ChangeSet read(final InputStream inputStream) throws SAXException,
                                                           IOException {
        return (ChangeSet) this.parser.read( inputStream );
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputSource</code>.
     *
     * @param in
     *            The rule-set input-source.
     *
     * @return The rule-set.
     */
    public ChangeSet read(final InputSource in) throws SAXException,
                                                  IOException {
        return (ChangeSet) this.parser.read( in );
    }
}
