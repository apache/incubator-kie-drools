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
package org.jbpm.process.instance;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;

public final class StartProcessHelper {
    
    public static final String PROCESS_COMPARATOR_CLASS_KEY = "jbpm.process.name.comparator";
    
    private static String comparatorClass = System.getProperty(PROCESS_COMPARATOR_CLASS_KEY);
	
	public static ProcessInstance startProcessByName( KieRuntime kruntime, String name, Map<String, Object> parameters ) {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}
		String processId = findLatestProcessByName(kruntime.getKieBase(), name);
		
		if (processId == null) {
			throw new IllegalArgumentException("Could not find process with name " + name);
		}
		return kruntime.startProcess(processId, parameters);
	}
	
	public static ProcessInstance startProcessByName(KieRuntime kruntime, String name, Map<String, Object> parameters, Comparator<Process> comparator) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        String processId = findLatestProcessByName(kruntime.getKieBase(), name, comparator);
        
        if (processId == null) {
            throw new IllegalArgumentException("Could not find process with name " + name);
        }
        return kruntime.startProcess(processId, parameters);
    }

	public static String findLatestProcessByName(KieBase kbase, final String processName) {
        if (kbase == null) {
            return null;
        }
        return findLatestProcessByName(kbase.getProcesses(), processName);
    }
	
	public static String findLatestProcessByName(KieBase kbase, final String processName, Comparator<Process> comparator) {
        if (kbase == null) {
            return null;
        }
        return findLatestProcessByName(kbase.getProcesses(), processName, comparator);
    }
    
    public static String findLatestProcessByName(Collection<Process> processes, final String processName) {
        
        return findLatestProcessByName(processes, processName, getComparator(processName)); 
    }
    
    public static String findLatestProcessByName(Collection<Process> processes, final String processName, Comparator<Process> comparator) {
        if (processes == null || processName == null) {
            return null;
        }
        
        Process highestVersionProcess = Collections.max(processes, comparator);
        if (highestVersionProcess != null && processName.equals(highestVersionProcess.getName())) {
            return highestVersionProcess.getId();
        }   
        
        return null;
    }
    
    public static Comparator<Process> getComparator(String name) {
        
        if (comparatorClass != null) {
            try {
                Class<Comparator<Process>> comparatorClazz = (Class<Comparator<Process>>) Class.forName(comparatorClass);
                Constructor<Comparator<Process>> constructor = comparatorClazz.getConstructor(String.class);
                
                return constructor.newInstance(name);
            } catch (Exception e) {
                
            }
        }
        return new NumberVersionComparator(name);
    }
    
    private static class NumberVersionComparator implements Comparator<Process> {
        
        private String processName;
        
        private NumberVersionComparator(String processName) {
            this.processName = processName;
        }
        
        public int compare(Process o1, Process o2) {
            // first match by process name
            if (o1.getName().equals(processName) && o2.getName().equals(processName)) {
                // then match on version
                try {
                    if( o1.getVersion() != null && o2.getVersion() != null ) { 
                        if ((Double.valueOf(o1.getVersion()) > Double.valueOf(o2.getVersion()))) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if( o1.getVersion() != null ) { 
                        return 1;
                    } else { 
                        return o1.getId().compareTo(o2.getId());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Could not parse version: " + o1.getVersion() + " " + o2.getVersion(), e);
                }
            } else if (o1.getName().equals(processName)) {
                return 1;
            }
            return -1;
        }
    }
}
