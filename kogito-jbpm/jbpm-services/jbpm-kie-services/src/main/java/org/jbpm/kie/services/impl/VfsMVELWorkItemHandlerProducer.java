package org.jbpm.kie.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.Vfs;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.commons.java.nio.file.Path;
import org.mvel2.MVEL;

public class VfsMVELWorkItemHandlerProducer implements WorkItemHandlerProducer {

    @Inject
    private FileService fs;
    @Inject
    @Vfs
    private Instance<DeploymentService> deploymentService;

    public VfsMVELWorkItemHandlerProducer() {
    }

    public void setFs(FileService fs) {
        this.fs = fs;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();

        try {
            if (deploymentService.get() == null) {
                return handlers;
            }
            DeployedUnit deployedUnit = deploymentService.get().getDeployedUnit(identifier);
            VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) deployedUnit.getDeploymentUnit();
            Path assetFolder = fs.getPath(vfsUnit.getRepository() + vfsUnit.getRepositoryFolder());
            if (identifier == null || !fs.exists(assetFolder)) {
                return handlers;
            }
            params.put("fs", fs);
        
            Iterable<Path> widFiles = fs.loadFilesByType(assetFolder, "conf");
            
            for (Path widPath : widFiles) {
                String content = new String(fs.loadFile(widPath), "UTF-8");
                
                handlers.putAll((Map<String, WorkItemHandler>) MVEL.eval( content, params ));
            }
        } catch (FileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return handlers;
    }

}
