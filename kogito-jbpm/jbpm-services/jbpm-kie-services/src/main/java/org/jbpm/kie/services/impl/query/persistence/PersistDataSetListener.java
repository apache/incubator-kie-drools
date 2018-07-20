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

package org.jbpm.kie.services.impl.query.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistryListener;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistDataSetListener implements DataSetDefRegistryListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PersistDataSetListener.class);
    
    private TransactionalCommandService commandService;

    public PersistDataSetListener() {
        
    }
    
    public PersistDataSetListener(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public void onDataSetDefStale(DataSetDef def) {

    }

    @Override
    public void onDataSetDefModified(DataSetDef oldDef, DataSetDef newDef) {
        
        if (commandService != null) {
            try {
                final String uniqueQueryName = oldDef.getUUID();
                final QueryDefinitionEntity updated = get(newDef);
                
                if (updated != null) {
                    commandService.execute(new ExecutableCommand<Void>() {
        
                        private static final long serialVersionUID = 6476274660250555118L;
        
                        @SuppressWarnings("unchecked")
                        @Override
                        public Void execute(Context context) {
                            JpaPersistenceContext ctx = (JpaPersistenceContext) context;
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("name", uniqueQueryName);
                            List<QueryDefinitionEntity> entities = ctx.queryWithParametersInTransaction("getQueryDefinitionByName", params, List.class);
                            
                            if (entities != null && !entities.isEmpty()) {
                                for (QueryDefinitionEntity entity : entities) {
                                    entity.setExpression(updated.getExpression());
                                    entity.setSource(updated.getSource());
                                    entity.setTarget(updated.getTarget());
                                    
                                    ctx.merge(entity);
                                    logger.debug("Updated data set {} to value: {}", entity.getName(), entity);
                                }
                            } else {
                                ctx.persist(updated);
                                logger.debug("Inserted data set {} as it did not exist with value: {}", updated.getName(), updated);
                            }
                            
                            return null;
                        }
                    });
                    logger.info("Data set {} updated in db storage", updated.getName());
                }
            } catch (Exception e) {
                logger.warn("Unable to persist data set {} in db due to {}", newDef.getUUID(), e.getMessage());
            }
            
        }
    }

    @Override
    public void onDataSetDefRegistered(DataSetDef newDef) {
        if (commandService != null) {
            try {
                final QueryDefinitionEntity entity = get(newDef);
                if (entity != null) {
                    commandService.execute(new ExecutableCommand<Void>() {
                        
                        private static final long serialVersionUID = 6476274660250555128L;
        
                        @SuppressWarnings("unchecked")
                        @Override
                        public Void execute(Context context) {
                            JpaPersistenceContext ctx = (JpaPersistenceContext) context;
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("name", entity.getName());
                            List<QueryDefinitionEntity> entities = ctx.queryWithParametersInTransaction("getQueryDefinitionByName", params, List.class);
                            
                            if (entities == null || entities.isEmpty()) {
                                ctx.persist(entity);
                                logger.info("Data set {} saved in db storage", entity.getName());
                            }
                            
                            return null;
                        }
                    });
                    
                }
            } catch (Exception e) {
                logger.warn("Unable to persist data set {} in db due to {}", newDef.getUUID(), e.getMessage());
            }
        }
    }

    @Override
    public void onDataSetDefRemoved(DataSetDef oldDef) {
        
        if (commandService != null) {
                        
            final String uniqueQueryName = oldDef.getUUID();
            try {
                commandService.execute(new ExecutableCommand<Void>() {
    
                    private static final long serialVersionUID = 6476274660250555118L;
    
                    @SuppressWarnings("unchecked")
                    @Override
                    public Void execute(Context context) {
                        JpaPersistenceContext ctx = (JpaPersistenceContext) context;
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("name", uniqueQueryName);
                        List<QueryDefinitionEntity> entities = ctx.queryWithParametersInTransaction("getQueryDefinitionByName", params, List.class);
                        
                        if (entities != null) {
                            for (QueryDefinitionEntity entity : entities) {
                                ctx.remove(entity);
                            }
                        }
                        
                        return null;
                    }
                });
                logger.info("Data set {} removed from db storage", uniqueQueryName);
            } catch (Exception e) {
                logger.warn("Unable to persist data set {} in db due to {}", uniqueQueryName, e.getMessage());
            }
        }

    }

    protected QueryDefinitionEntity get(DataSetDef def) {
        QueryDefinitionEntity entity = null;
        if (def instanceof SQLDataSetDef && ((SQLDataSetDef) def).getDbSQL() != null) {
            String target = "CUSTOM";
            String nameWithTarget = def.getName();
            
            if (nameWithTarget.indexOf("::") != -1) {
                
                try {
                    target = nameWithTarget.split("::")[1];
                    target = Target.valueOf(target).name();
                } catch (Exception e) {
                    target = "CUSTOM";   
                }
            }
            
            entity = new QueryDefinitionEntity();
            entity.setName(def.getUUID());
            entity.setExpression(((SQLDataSetDef) def).getDbSQL());
            entity.setSource(((SQLDataSetDef) def).getDataSource());
            entity.setTarget(target);
        }
        
        return entity;
    }
}
