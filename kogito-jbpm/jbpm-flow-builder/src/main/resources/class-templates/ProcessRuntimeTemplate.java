package org.jbpm.process.core.dummy;

import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactoryService;
import org.jbpm.process.core.dummy.DummyWorkingMemory;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.kie.api.definition.process.Process;

public class ProcessRuntimeProvider {
    
    private static InternalProcessRuntime runtime;

    public static InternalProcessRuntime getProcessRuntime() {
        
        if (runtime != null) {            
            return runtime;
        }
        
        KnowledgeBaseImpl kb = new KnowledgeBaseImpl("", new RuleBaseConfiguration());
        
        for (Process process : getProcesses()) {
            kb.addProcess(process);
        }
        
        ProcessRuntimeFactoryService svc = ProcessRuntimeFactory.getProcessRuntimeFactoryService();
        DummyWorkingMemory wm = new DummyWorkingMemory(kb);
        
        InternalProcessRuntime processRuntime = svc.newProcessRuntime(wm);
        wm.setProcessRuntime(processRuntime);
        
        processRuntime.getWorkItemManager().registerWorkItemHandler("Log", new DoNothingWorkItemHandler());
        
        runtime = processRuntime;        
        
        return processRuntime;
    }
    
    public static List<Process> getProcesses() {
        return null;
    }
 }
