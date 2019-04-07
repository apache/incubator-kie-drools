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

package org.jbpm.casemgmt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;

import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CommentInstanceImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthorizationManagerImpl implements AuthorizationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationManagerImpl.class);
    
    private static final String NO_ACCESS_MSG = "User {0} is not authorized to access case {1}";
    private static final String NO_AUTH_OPER_MSG = "User {0} is not authorized to {1} on case {2}";
    private static final String NO_AUTH_TO_DATA = "User {0} does not have access to data item named {1} in case {2}";
    private static final String NO_AUTH_TO_COMMENT = "User {0} does not have access to comment {1} in case {2}";

    private IdentityProvider identityProvider;
    private TransactionalCommandService commandService;
    
    private boolean enabled = Boolean.parseBoolean(System.getProperty("org.jbpm.cases.auth.enabled", "true"));
    
    private Map<ProtectedOperation, List<String>> operationAuthorization = new HashMap<>();
           
    public AuthorizationManagerImpl(IdentityProvider identityProvider, TransactionalCommandService commandService) {       
        this.identityProvider = identityProvider;
        this.commandService = commandService;
        buildAuthorizationConfig();
    }


    @Override
    public void checkAuthorization(String caseId) throws SecurityException {
        if (!isEnabled()) {
            return;
        }

        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Skipping authorization check.");
            return;
        }

        logger.debug("Checking authorization to case {} for user {}", caseId, identityProvider.getName());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId);

        List<String> authorizedEntities = commandService.execute(new QueryNameCommand<List<String>>("getAuthorizationToCaseInstance", params));
        verifyAuthorization(caseId, authorizedEntities, MessageFormat.format(NO_ACCESS_MSG, identityProvider.getName(), caseId));
    }
    
    @Override
    public void checkOperationAuthorization(String caseId, ProtectedOperation operation) throws SecurityException {
        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Skipping operation authorization check.");
            return;
        }

        List<String> rolesForOperation = operationAuthorization.get(operation);
        
        if (rolesForOperation == null || rolesForOperation.isEmpty()) {
            logger.debug("No restrictions defined for operation {}", operation);
            checkAuthorization(caseId);
            return;
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseId);
        params.put("roles", rolesForOperation);

        List<String> authorizedEntities = commandService.execute(new QueryNameCommand<List<String>>("getAuthorizationToCaseInstanceByRole", params));
        verifyAuthorization(caseId, authorizedEntities, MessageFormat.format(NO_AUTH_OPER_MSG, identityProvider.getName(), operation, caseId));
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /*
     * Helper methods
     */ 
    
    protected void buildAuthorizationConfig() {
        Properties loaded = new Properties();
        InputStream configuration = this.getClass().getResourceAsStream("/case-authorization.properties");
        if (configuration != null) {            
            try {
                loaded.load(configuration);
            } catch (IOException e) {
                logger.error("Error loading case autorization config from file due to {}", e.getMessage(), e);
            }            
        }        

        Stream.of(ProtectedOperation.values()).forEach(operation -> {
           List<String> roles = new ArrayList<>();
           String grantedRoles = loaded.getProperty(operation.toString());
           if (grantedRoles != null) {
               roles.addAll(Arrays.asList(grantedRoles.split(",")));
           }
           operationAuthorization.put(operation, roles);
        });
    }
    
    protected void verifyAuthorization(String caseId, List<String> authorizedEntities, String errorMessage) {
        logger.debug("Case {} authorization set is {}", caseId, authorizedEntities);
        if (authorizedEntities.isEmpty()) {
            logger.debug("Not access restrictions defined for case {}", caseId);
            // no authorization configured - roles assignment are not defined
            return;
        }
        List<String> callerAuthorization = collectUserAuthInfo();
        logger.debug("Caller authorization set is {}", callerAuthorization);
        boolean isAuthorized = callerAuthorization.stream().anyMatch(entity -> authorizedEntities.contains(entity));
        
        if (!isAuthorized) {
            logger.debug("User {} not authorized to access case {}", identityProvider.getName(), caseId);
            throw new SecurityException(errorMessage);
        }
        logger.debug("User {} authorized to access case {}", identityProvider.getName(), caseId);
    }
    
    protected List<String> collectUserAuthInfo() {
        List<String> entities = new ArrayList<>();
        entities.add(identityProvider.getName());
        entities.addAll(identityProvider.getRoles());
        
        // add special public role to allow to find cases that do not use case roles
        entities.add(PUBLIC_GROUP);
        
        return entities;
    }


    @Override
    public Map<String, Object> filterByDataAuthorization(String caseId, CaseFileInstance caseFileInstance, Map<String, Object> data) {
        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Returning all data.");
            return data;
        }

        if (data == null || data.isEmpty()) {
            logger.debug("No data to be filtered");
            return data;
        }
        
        Map<String, List<String>> accessRestrictions = ((CaseFileInstanceImpl) caseFileInstance).getAccessRestrictions();
        if (accessRestrictions == null || accessRestrictions.isEmpty()) {
            logger.debug("Case {} does not define any data restrictions returning unfiltered map", caseId);
            return data;
        }
        
        List<String> callerAuthorization = collectUserAuthInfo();
        logger.debug("Caller authorization set is {}", callerAuthorization);
        
        List<String> callerCaseRoles = getCallerRoles(caseFileInstance, callerAuthorization);
        logger.debug("Caller case role set is {}", callerCaseRoles);
        Map<String, Object> filteredData = new HashMap<>(data);
        for (Entry<String, List<String>> entry : accessRestrictions.entrySet()) {
            
            List<String> requiredRoles = entry.getValue();
            if (requiredRoles.isEmpty() || requiredRoles.stream().anyMatch(role -> callerCaseRoles.contains(role))) {
                logger.debug("Caller {} has access to data item named {}", identityProvider.getName(), entry.getKey());
                continue;
            }
            logger.debug("Caller {} does not have access to data item named {}, required roles are {} and caller has {}", 
                    identityProvider.getName(), entry.getKey(), requiredRoles, callerCaseRoles);
            filteredData.remove(entry.getKey());
        }
        
        return filteredData;
    }

    @Override
    public void checkDataAuthorization(String caseId, CaseFileInstance caseFileInstance, Collection<String> dataNames) {
        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Skipping data authorization check.");
            return;
        }

        if (dataNames == null || dataNames.isEmpty()) {
            logger.debug("No data to be checked");
            return;
        }
        
        Map<String, List<String>> accessRestrictions = ((CaseFileInstanceImpl) caseFileInstance).getAccessRestrictions();
        if (accessRestrictions == null || accessRestrictions.isEmpty()) {
            logger.debug("Case {} does not define any data restrictions", caseId);
            return;
        }
        
        List<String> callerAuthorization = collectUserAuthInfo();
        logger.debug("Caller {} authorization set is {}", identityProvider.getName(), callerAuthorization);
        
        List<String> callerCaseRoles = getCallerRoles(caseFileInstance, callerAuthorization);
        logger.debug("Caller {} case role set is {}", identityProvider.getName(), callerCaseRoles);
        
        for (Entry<String, List<String>> entry : accessRestrictions.entrySet()) {
            
            if (dataNames.contains(entry.getKey())) {
                List<String> requiredRoles = entry.getValue();
                if (requiredRoles.isEmpty() || requiredRoles.stream().anyMatch(role -> callerCaseRoles.contains(role))) {
                    logger.debug("Caller has access to data item named {}", entry.getKey());
                    continue;
                }
                logger.warn("User {} does not have access to data item {} in case {}, required roles are {} and user has {}", 
                        identityProvider.getName(), entry.getKey(), caseId, requiredRoles, callerCaseRoles);
                throw new SecurityException(MessageFormat.format(NO_AUTH_TO_DATA, identityProvider.getName(), entry.getKey(), caseId, requiredRoles, callerCaseRoles));
            }
        }
    }
    
    @Override
    public List<CommentInstance> filterByCommentAuthorization(String caseId, CaseFileInstance caseFileInstance, List<CommentInstance> comments) {
        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Returning all comments.");
            return comments;
        }

        if (comments == null || comments.isEmpty()) {
            logger.debug("No comments to be filtered");
            return comments;
        }
        
        List<String> callerAuthorization = collectUserAuthInfo();
        logger.debug("Caller {} authorization set is {}", identityProvider.getName(), callerAuthorization);
        
        List<String> callerCaseRoles = getCallerRoles(caseFileInstance, callerAuthorization);
        logger.debug("Caller {} case role set is {}", identityProvider.getName(), callerCaseRoles);
        
        List<CommentInstance> filteredComments = new ArrayList<>(comments);
        
        for (CommentInstance commentInstance : comments) {
            CommentInstanceImpl comment = ((CommentInstanceImpl) commentInstance);
            List<String> requiredRoles = comment.getRestrictedTo();
            if (requiredRoles == null || requiredRoles.isEmpty()) {
                continue;
            }
            
            if (requiredRoles.isEmpty() || requiredRoles.stream().anyMatch(role -> callerCaseRoles.contains(role))) {
                logger.debug("Caller {} has access to comment {}", identityProvider.getName(), comment.getId());
                continue;
            }
            logger.debug("Caller {} does not have access to comment {}", identityProvider.getName(), comment.getId());
            filteredComments.remove(comment);
        }
        
        return filteredComments;
    }
    
    @Override
    public void checkCommentAuthorization(String caseId, CaseFileInstance caseFileInstance, CommentInstance commentInstance) {
        if (loggedInAsSystemUser()) {
            logger.debug("Logged in as system user 'unknown'. Skipping comment authorization check.");
            return;
        }

        CommentInstanceImpl comment = ((CommentInstanceImpl) commentInstance);
        if (comment.getRestrictedTo() == null || comment.getRestrictedTo().isEmpty()) {
            return;
        }
        
        List<String> callerAuthorization = collectUserAuthInfo();
        logger.debug("Caller {} authorization set is {}", identityProvider.getName(), callerAuthorization);
        
        List<String> callerCaseRoles = getCallerRoles(caseFileInstance, callerAuthorization);
        logger.debug("Caller {} case role set is {}", identityProvider.getName(), callerCaseRoles);
        
        List<String> requiredRoles = comment.getRestrictedTo();
        if (requiredRoles.isEmpty() || requiredRoles.stream().anyMatch(role -> callerCaseRoles.contains(role))) {
            logger.debug("Caller has access to comment {}", comment.getId());
            return;
        }
        logger.warn("User {} does not have access to comment {} in case {}, required roles are {} and user has {}", 
                identityProvider.getName(), comment.getId(), caseId, requiredRoles, callerCaseRoles);
        throw new SecurityException(MessageFormat.format(NO_AUTH_TO_COMMENT, identityProvider.getName(), comment.getId(), caseId));
    }
    
    protected List<String> getCallerRoles(CaseFileInstance caseFileInstance, List<String> callerAuthorization) {
        List<String> callerRoles = ((CaseFileInstanceImpl) caseFileInstance).getRolesForOrgEntities(callerAuthorization);
        // always add public group
        callerRoles.add(PUBLIC_GROUP);
        
        return callerRoles;
    }

    protected boolean loggedInAsSystemUser() {
        return UNKNOWN_USER.equals(identityProvider.getName());
    }
}
