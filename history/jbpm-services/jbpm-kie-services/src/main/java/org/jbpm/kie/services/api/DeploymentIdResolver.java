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

package org.jbpm.kie.services.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.internal.runtime.manager.RuntimeManagerIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deployment id resolver that allows to find out latest version of given deployment id. To be able
 * to find it deployment id needs to be done with valid group and artifact id and version must be 
 * set to <code>latest</code> (case insensitive):<br/>
 * <code>org.jbpm:HR:latest</code><br/>
 * then available deployment units will be scanned and based on Maven version comparison latest will
 * be returned.
 * <br/>
 * Primary method to be used {@link DeploymentIdResolver#matchAndReturnLatest}
 */
public class DeploymentIdResolver implements RuntimeManagerIdFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(DeploymentIdResolver.class);
	
	public static String matchAndReturnLatest(String deploymentId, Collection<String> availableDeployments) {
		try {
			GAVInfo gav = new GAVInfo(deploymentId);
			if (deploymentId != null && gav.getVersion().equalsIgnoreCase("latest")) {
				Collection<String> matched = matchDeployments(deploymentId, availableDeployments);
				
				if (matched != null && !matched.isEmpty()) {
					return findLatest(matched);
				}
			}
		} catch (Exception e) {
			logger.debug("Unable to resolve latest version of deployment {} due to {}", deploymentId, e.getMessage());
		}
		return deploymentId;
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> matchDeployments(String deploymentId, Collection<String> availableDeployments) {
		Collection<String> matched = CollectionUtils.select(availableDeployments, new GroupAndArtifactMatchPredicate(deploymentId));
		
		return matched;
	}
	
	public static String findLatest(Collection<String> deploymentIds) {
		List<ComparableVersion> comparableVersions = new ArrayList<ComparableVersion>();
		Map<String, String> versionToIdentifier = new HashMap<String, String>();
		for (String deploymentId : deploymentIds) {
			GAVInfo gav = new GAVInfo(deploymentId);
			comparableVersions.add(new ComparableVersion(gav.getVersion()));
			versionToIdentifier.put(gav.getVersion(), deploymentId);
		}
		
		ComparableVersion latest = Collections.max(comparableVersions);
		
		return versionToIdentifier.get(latest.toString());
	}
	
    private static class GroupAndArtifactMatchPredicate implements Predicate {
        
        private GAVInfo gavInfo;
        private String groupArtifact;
        
        private GroupAndArtifactMatchPredicate(String deploymentId) {
        	this.gavInfo = new GAVInfo(deploymentId);
        	this.groupArtifact = gavInfo.getGroupId() + ":" + gavInfo.getArtifactId();
        }
        
        @Override
        public boolean evaluate(Object object) {
        	if (object instanceof String) {
        		if (((String) object).startsWith(groupArtifact)) {
        			return true;
        		}
        	} else if (object instanceof DeployedUnit) {
        		String identifier = ((DeployedUnit) object).getDeploymentUnit().getIdentifier();
        		if (identifier.startsWith(groupArtifact)) {
        			return true;
        		}
        	}
            return false;
        }        
    }
	
	private static class GAVInfo {
		private String groupId;
		private String artifactId;
		private String version;
		
		GAVInfo(String deploymentId) {
			String[] details = deploymentId.split(":");
			
			this.groupId = details[0];
			this.artifactId = details[1];
			this.version = details[2];
		}

		public String getGroupId() {
			return groupId;
		}

		public String getArtifactId() {
			return artifactId;
		}
		public String getVersion() {
			return version;
		}
	}

    @Override
    public Collection<String> filter(String pattern, Collection<String> identifiers) {
        Collection<String> filtered = new ArrayList<String>();
        String found = DeploymentIdResolver.matchAndReturnLatest(pattern, identifiers);
        if (found != null && !found.equals(pattern)) {
            filtered.add(found);
        }
        
        return filtered;
    }
}
