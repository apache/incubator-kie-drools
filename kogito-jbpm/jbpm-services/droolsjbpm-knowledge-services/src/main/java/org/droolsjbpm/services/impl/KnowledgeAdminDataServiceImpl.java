/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.jboss.seam.transaction.Transactional;

/**
 *
 * @author salaboy
 */
@Transactional
@ApplicationScoped
public class KnowledgeAdminDataServiceImpl implements KnowledgeAdminDataService{
    @Inject EntityManager em;
    
    public int removeAllData() {
        int deleted = 0;
        deleted += em.createQuery("delete from  NodeInstanceDesc nid").executeUpdate();
        deleted += em.createQuery("delete from  ProcessInstanceDesc pid").executeUpdate();
        deleted += em.createQuery("delete from  ProcessDesc pd").executeUpdate();
        deleted += em.createQuery("delete from  VariableStateDesc vsd").executeUpdate();
        return deleted;
    }
    
}
