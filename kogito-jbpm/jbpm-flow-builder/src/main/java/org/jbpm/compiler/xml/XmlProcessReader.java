package org.jbpm.compiler.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.xml.parsers.SAXParser;

import org.kie.definition.process.Process;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.SemanticModules;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlProcessReader {
    private ExtensibleXmlParser parser;

    private List<Process>        processes;

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader) {
        this( modules, classLoader, null );
    }

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader, final SAXParser parser) {
        if ( parser == null ) {
            this.parser = new ExtensibleXmlParser();
        } else {
            this.parser = new ExtensibleXmlParser( parser );
        }      
        this.parser.setSemanticModules( modules );
        this.parser.setData( new ProcessBuildData() );
        this.parser.setClassLoader( classLoader );
    }

    /**
     * Read a <code>Process</code> from a <code>Reader</code>.
     *
     * @param reader
     *            The reader containing the rule-set.
     *
     * @return The rule-set.
     */
    public List<Process> read(final Reader reader) throws SAXException,
                                                 IOException {
        this.processes = ((ProcessBuildData) this.parser.read( reader )).getProcesses();
        return this.processes;
    }

    /**
     * Read a <code>Process</code> from an <code>InputStream</code>.
     *
     * @param inputStream
     *            The input-stream containing the rule-set.
     *
     * @return The rule-set.
     */
    public List<Process> read(final InputStream inputStream) throws SAXException,
                                                           IOException {
        this.processes = ((ProcessBuildData) this.parser.read( inputStream )).getProcesses();
        return this.processes;
    }

    /**
     * Read a <code>Process</code> from an <code>InputSource</code>.
     *
     * @param in
     *            The rule-set input-source.
     *
     * @return The rule-set.
     */
    public List<Process> read(final InputSource in) throws SAXException,
                                                  IOException {
        this.processes = ((ProcessBuildData)this.parser.read( in )).getProcesses();
        return this.processes;
    }

    void setProcesses(final List<Process> processes) {
        this.processes = processes;
    }

    public List<Process> getProcess() {
        return this.processes;
    }
}
