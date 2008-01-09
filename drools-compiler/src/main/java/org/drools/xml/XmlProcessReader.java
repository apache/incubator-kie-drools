package org.drools.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.drools.lang.descr.PackageDescr;
import org.drools.xml.rules.AccumulateHandler;
import org.drools.xml.rules.AccumulateHelperHandler;
import org.drools.xml.rules.AndHandler;
import org.drools.xml.rules.CollectHandler;
import org.drools.xml.rules.EvalHandler;
import org.drools.xml.rules.ExistsHandler;
import org.drools.xml.rules.ExpressionHandler;
import org.drools.xml.rules.FieldBindingHandler;
import org.drools.xml.rules.FieldConstraintHandler;
import org.drools.xml.rules.ForallHandler;
import org.drools.xml.rules.FromHandler;
import org.drools.xml.rules.FunctionHandler;
import org.drools.xml.rules.LiteralRestrictionHandler;
import org.drools.xml.rules.NotHandler;
import org.drools.xml.rules.OrHandler;
import org.drools.xml.rules.PackageHandler;
import org.drools.xml.rules.PatternHandler;
import org.drools.xml.rules.PredicateHandler;
import org.drools.xml.rules.QualifiedIdentifierRestrictionHandler;
import org.drools.xml.rules.QueryHandler;
import org.drools.xml.rules.RestrictionConnectiveHandler;
import org.drools.xml.rules.ReturnValueRestrictionHandler;
import org.drools.xml.rules.RuleHandler;
import org.drools.xml.rules.VariableRestrictionsHandler;
import org.drools.process.core.Process;
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
     * Read a <code>RuleSet</code> from a <code>Reader</code>.
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
     * Read a <code>RuleSet</code> from an <code>InputStream</code>.
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
     * Read a <code>RuleSet</code> from an <code>InputSource</code>.
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
