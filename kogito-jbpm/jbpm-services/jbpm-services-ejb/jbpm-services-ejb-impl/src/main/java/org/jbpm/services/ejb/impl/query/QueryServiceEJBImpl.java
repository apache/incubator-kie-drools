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

package org.jbpm.services.ejb.impl.query;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.services.api.query.QueryAlreadyRegisteredException;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.ejb.api.query.QueryServiceEJBLocal;
import org.jbpm.services.ejb.api.query.QueryServiceEJBRemote;
import org.jbpm.services.ejb.impl.identity.EJBContextIdentityProvider;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class QueryServiceEJBImpl extends QueryServiceImpl implements QueryServiceEJBLocal, QueryServiceEJBRemote {

    
    @Inject
    private Instance<IdentityProvider> identityProvider;

    @Inject
    private Instance<UserGroupCallback> userGroupCallback;

    @Resource
    private EJBContext context;
    // inject resources

    @PostConstruct
    public void configure() {
        if (identityProvider.isUnsatisfied()) {
            setIdentityProvider(new EJBContextIdentityProvider(context));
        } else {
            setIdentityProvider(identityProvider.get());
        }
        if (!userGroupCallback.isUnsatisfied()) {
            setUserGroupCallback(userGroupCallback.get());
        }
        super.init();
    }

    @EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
    @Override
    public void setCommandService(TransactionalCommandService commandService) {
        super.setCommandService(commandService);
    }

    @Lock(LockType.WRITE)
    @Override
    public void registerQuery(QueryDefinition queryDefinition) throws QueryAlreadyRegisteredException {
        super.registerQuery(queryDefinition);
    }

    @Lock(LockType.WRITE)
    @Override
    public void replaceQuery(QueryDefinition queryDefinition) {
        super.replaceQuery(queryDefinition);
    }

    @Lock(LockType.WRITE)
    @Override
    public void unregisterQuery(String uniqueQueryName) throws QueryNotFoundException {
        super.unregisterQuery(uniqueQueryName);
    }

}
