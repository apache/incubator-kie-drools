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
import java.util.ArrayList;
import java.util.List;

import org.drools.ruleflow.common.core.IProcess;
import org.drools.ruleflow.core.IRuleFlowProcess;

import com.thoughtworks.xstream.XStream;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ProcessBuilder {
	
	private List processes = new ArrayList();
	
	public IProcess[] getProcesses() {
		return (IProcess[]) processes.toArray(new IProcess[processes.size()]);
	}
	
	public void addProcess(IProcess process) {
		processes.add(process);
	}
	
	public void addProcessFromFile(final Reader reader) throws Exception {
        XStream stream = new XStream();
        stream.setMode(XStream.ID_REFERENCES);
        
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newLoader = this.getClass().getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(newLoader);
            IRuleFlowProcess process = (IRuleFlowProcess) stream.fromXML(reader);
            addProcess(process);
        } catch (Exception t) {
            t.printStackTrace();
            throw t;
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
        reader.close();
	}
}
