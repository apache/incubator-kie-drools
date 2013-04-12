package org.droolsjbpm.services.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.java.nio.file.Path;
import org.kie.api.runtime.process.WorkItemHandler;
import org.mvel2.MVEL;

public class MVELWorkItemHandlerProducer implements WorkItemHandlerProducer {

    @Inject
    private FileService fs;

    public MVELWorkItemHandlerProducer() {
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        if (identifier == null || !fs.exists("processes/" + identifier)) {
            return handlers;
        }
        try {
            Iterable<Path> widFiles = fs.loadFilesByType("processes/" + identifier, "conf");
            
            for (Path widPath : widFiles) {
                String content = new String(fs.loadFile(widPath), "UTF-8");
                
                handlers.putAll((Map<String, WorkItemHandler>) MVEL.eval( content, params ));
            }
        } catch (FileException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return handlers;
    }

}
