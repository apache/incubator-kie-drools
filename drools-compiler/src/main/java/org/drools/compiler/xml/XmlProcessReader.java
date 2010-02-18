package org.drools.compiler.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.SAXParser;

import org.drools.definition.process.Process;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.SemanticModules;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlProcessReader {
    private ExtensibleXmlParser parser;

    private Process        process;

    public XmlProcessReader(final SemanticModules modules) {
        this( modules, null );
    }

    public XmlProcessReader(final SemanticModules modules, final SAXParser parser) {
        if ( parser == null ) {
            this.parser = new ExtensibleXmlParser();
        } else {
            this.parser = new ExtensibleXmlParser( parser );
        }      
        this.parser.setSemanticModules( modules );
        this.parser.setData( new ProcessBuildData() );
    }

    /**
     * Read a <code>Process</code> from a <code>Reader</code>.
     *
     * @param reader
     *            The reader containing the rule-set.
     *
     * @return The rule-set.
     */
    public Process read(final Reader reader) throws SAXException,
                                                 IOException {
        this.process = ((ProcessBuildData) this.parser.read( reader )).getProcess();
        return this.process;
    }

    /**
     * Read a <code>Process</code> from an <code>InputStream</code>.
     *
     * @param inputStream
     *            The input-stream containing the rule-set.
     *
     * @return The rule-set.
     */
    public Process read(final InputStream inputStream) throws SAXException,
                                                           IOException {
        this.process = ((ProcessBuildData) this.parser.read( inputStream )).getProcess();
        return this.process;
    }

    /**
     * Read a <code>Process</code> from an <code>InputSource</code>.
     *
     * @param in
     *            The rule-set input-source.
     *
     * @return The rule-set.
     */
    public Process read(final InputSource in) throws SAXException,
                                                  IOException {
        this.process = ((ProcessBuildData)this.parser.read( in )).getProcess();
        return this.process;
    }

    void setProcess(final Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return this.process;
    }
}
