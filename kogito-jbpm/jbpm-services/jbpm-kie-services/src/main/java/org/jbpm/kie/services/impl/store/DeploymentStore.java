/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.kie.services.impl.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.MergeObjectCommand;
import org.jbpm.shared.services.impl.commands.PersistObjectCommand;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class DeploymentStore {
	
	private static final Logger logger = LoggerFactory.getLogger(DeploymentStore.class);

	private final XStream xstream = new XStream();
	
	
	private TransactionalCommandService commandService;
	
	public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
	
	public Collection<DeploymentUnit> getEnabledDeploymentUnits() {
		List<DeploymentUnit> activeDeployments = new ArrayList<DeploymentUnit>();
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("state", 1);
        List<DeploymentStoreEntry> deployments = commandService.execute(
				new QueryNameCommand<List<DeploymentStoreEntry>>("getDeploymentUnitsByState", params));
        
        for (DeploymentStoreEntry entry : deployments) {
        	DeploymentUnit unit = (DeploymentUnit) xstream.fromXML(entry.getDeploymentUnit());
        	
        	activeDeployments.add(unit);
        }
        
        return activeDeployments;
	}
	
	public void getDeploymentUnitsByDate(Date date, Collection<DeploymentUnit> enabled, Collection<DeploymentUnit> disabled) {
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("ludate", date);
        List<DeploymentStoreEntry> deployments = commandService.execute(
				new QueryNameCommand<List<DeploymentStoreEntry>>("getDeploymentUnitsByDate", params));
        
        for (DeploymentStoreEntry entry : deployments) {
        	DeploymentUnit unit = (DeploymentUnit) xstream.fromXML(entry.getDeploymentUnit());
        	if (entry.getState() == 1) {
        		enabled.add(unit);
        	} else if (entry.getState() == 0) {
        		disabled.add(unit);
        	} else {
        		logger.warn("Unknown state of deployment store entry {} for entry {} will be ignored", entry.getId(), entry);
        	}
        }
        
	}
	
	public void enableDeploymentUnit(DeploymentUnit unit) {
		String unitContent = xstream.toXML(unit);
		DeploymentStoreEntry entry = findDeploymentStoreByDeploymentId(unit.getIdentifier());
		if (entry != null) {
			// update only
			entry.setState(1);// 0 - disabled, 1 - enabled
			entry.setUpdateDate(new Date());		
			entry.setDeploymentUnit(unitContent);
			
			commandService.execute(new MergeObjectCommand(entry));
			return;
		}
		
		entry = new DeploymentStoreEntry();
		entry.setDeploymentId(unit.getIdentifier());
		entry.setState(1);// 0 - disabled, 1 - enabled
		entry.setUpdateDate(new Date());		
		entry.setDeploymentUnit(unitContent);
		
		commandService.execute(new PersistObjectCommand(entry));
		
	}
	
	public void disableDeploymentUnit(DeploymentUnit unit) {
		
		DeploymentStoreEntry entry = findDeploymentStoreByDeploymentId(unit.getIdentifier());
		if (entry != null) {
			// update only
			entry.setState(0);// 0 - disabled, 1 - enabled
			entry.setUpdateDate(new Date());		
			
			commandService.execute(new MergeObjectCommand(entry));	
		}
		
	}
	
	public void markDeploymentUnitAsObsolete(DeploymentUnit unit) {
		
		DeploymentStoreEntry entry = findDeploymentStoreByDeploymentId(unit.getIdentifier());
		if (entry != null) {
			// update only
			entry.setState(-1);// 0 - disabled, 1 - enabled, -1 - obsolete 
			entry.setUpdateDate(new Date());		
			
			commandService.execute(new MergeObjectCommand(entry));	
		}
		
	}
	
	public DeploymentStoreEntry findDeploymentStoreByDeploymentId(String deploymentId) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("deploymentId", deploymentId);
        params.put("maxResults", 1);
        List<DeploymentStoreEntry> deployments = commandService.execute(
				new QueryNameCommand<List<DeploymentStoreEntry>>("getDeploymentUnit", params));
        
        if (!deployments.isEmpty()) {
        	return deployments.get(0);
        }
        return null;
	}


}
