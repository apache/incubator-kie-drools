/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;

import java.text.MessageFormat;
import javax.xml.parsers.SAXParser;

import org.kie.api.definition.process.Process;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.SemanticModules;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//processId, processPkg, processName, processVersion
public class XmlProcessReader {
    private ExtensibleXmlParser parser;
    private final MessageFormat message = new java.text.MessageFormat("Node Info: id:{0} name:{1} \n" +
                                                                              "Parser message: {2}");

    private final MessageFormat messageWithProcessInfo = new java.text.MessageFormat("Process Info: id:{0}, pkg:{1}, name:{2}, version:{3} \n" +
                                                                              "Node Info: id:{4} name:{5} \n" +
                                                                              "Parser message: {6}");

    private List<Process>        processes;

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader) {
        this( modules, classLoader, null );
    }

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader, final SAXParser parser) {
        this.parser = new ExtensibleXmlParser() {
            @Override
            protected String buildPrintMessage(final SAXParseException x) {
                return processParserMessage(super.getParents(), super.getAttrs(), super.buildPrintMessage(x));
            }
        };

        if(parser != null) {
            this.parser.setParser(parser);
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
    
    public ProcessBuildData getProcessBuildData() {
        return (ProcessBuildData) this.parser.getData();
    }

    protected String processParserMessage(LinkedList<Object> parents, Attributes attr, String errorMessage) {
        String nodeId = (attr == null  || attr.getValue("id") == null) ? "" : attr.getValue("id");
        String nodeName = (attr == null  || attr.getValue("name") == null) ? "" : attr.getValue("name");

        for(Object parent : parents) {
            if(parent != null && parent instanceof RuleFlowProcess) {
                RuleFlowProcess process = ((RuleFlowProcess) parent);
                return messageWithProcessInfo.format(new Object[] {process.getId(),
                        process.getPackageName(),
                        process.getName(),
                        process.getVersion(), nodeId, nodeName, errorMessage});
            }
        }

        return message.format(new Object[] {nodeId, nodeName, errorMessage});
    }
}
