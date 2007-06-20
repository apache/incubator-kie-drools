package org.drools.compiler;

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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;

import com.thoughtworks.xstream.XStream;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ProcessBuilder {
	
	private PackageBuilder packageBuilder;
	
	public ProcessBuilder(PackageBuilder packageBuilder) {
		this.packageBuilder = packageBuilder;
	}

    private final List processes = new ArrayList();

    public Process[] getProcesses() {
        return (Process[]) this.processes.toArray( new Process[this.processes.size()] );
    }

    public void addProcess(final Process process) {
        this.processes.add( process );
        // generate and add rule for process
        String rules = generateRules( process );
        if (rules != null && rules.length() != 0) {
        	try {
        		packageBuilder.addPackageFromDrl(new StringReader(rules));
        	} catch (Throwable t) {
        		// should never occur
        	}
        }
    }

    public void addProcessFromFile(final Reader reader) throws Exception {
        final XStream stream = new XStream();
        stream.setMode( XStream.ID_REFERENCES );

        
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader newLoader = this.getClass().getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( newLoader );
            final RuleFlowProcess process = (RuleFlowProcess) stream.fromXML( reader );
            addProcess( process );
        } catch ( final Exception t ) {
            t.printStackTrace();
            throw t;
        } finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
        reader.close();
    }
    
    private String generateRules(final Process process) {
    	String result = "";
    	if (process instanceof RuleFlowProcessImpl) {
    		RuleFlowProcessImpl ruleFlow = (RuleFlowProcessImpl) process;
    		Node[] nodes = ruleFlow.getNodes();
    		for (int i = 0; i < nodes.length; i++) {
    			 if (nodes[i] instanceof Split) {
    				 Split split = (Split) nodes[i];
    				 if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
    					 for (Iterator iterator = split.getOutgoingConnections().iterator(); iterator.hasNext(); ) {
    						 Connection connection = (Connection) iterator.next();
    						 result += createSplitRule(process, connection, split.getConstraint(connection).getConstraint());
    					 }
    				 }
    			 }
    		}
    	}
    	return result;
    }
    
    private String createSplitRule(Process process, Connection connection, String constraint) {
		return 
    		"rule \"RuleFlow-" + process.getId() + "-"
    			+ connection.getFrom().getId() + "-" + connection.getTo().getId() + "\" \n" + 
			"      ruleflow-group \"DROOLS_SYSTEM\" \n" + 
			"    when \n" + 
			"      " + constraint + "\n" +
			"    then \n" +
			"end \n\n";
    }
}
