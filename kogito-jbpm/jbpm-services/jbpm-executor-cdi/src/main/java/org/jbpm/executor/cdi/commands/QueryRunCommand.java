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

package org.jbpm.executor.cdi.commands;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.BeanManager;

import org.jbpm.executor.cdi.CDIUtils;
import org.jbpm.services.api.query.QueryResultMapper;
import org.jbpm.services.api.query.QueryService;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple command to log the contextual data and return empty results. After attempting to get BeanManager
 * and creating simple CDI bean based on given class name as parameter. 
 * Just for demo purpose.
 * 
 */
public class QueryRunCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(QueryRunCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
        BeanManager manager = CDIUtils.lookUpBeanManager(ctx);
        String clazz = QueryService.class.getName();

        try {
            QueryService cdiBean = (QueryService) CDIUtils.createBean(Class.forName(clazz), manager);
            logger.info("CDI bean created {}", cdiBean);

            String mapperClass = (String) ctx.getData("mapper");
            if (mapperClass == null) {
                mapperClass = "org.jbpm.kie.services.impl.query.mapper.ProcessInstanceQueryMapper";
            }

            Method m = Class.forName(mapperClass).getMethod("get", new Class[0]);

            QueryResultMapper<?> mapper = (QueryResultMapper<?>) m.invoke(null, new Object[0]);

            Object queryR = cdiBean.query((String) ctx.getData("query"), mapper, new QueryContext());

            logger.info("Result of the query is " + queryR);
        } catch (Exception e) {
            logger.error("Error while creating CDI bean from jbpm executor", e);
        }

        logger.info("Command executed on executor with data {}", ctx.getData());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }

    protected Object getParameter(CommandContext commandContext, String parameterName) {
        if (commandContext.getData(parameterName) != null) {
            return commandContext.getData(parameterName);
        }
        WorkItem workItem = (WorkItem) commandContext.getData("workItem");
        if (workItem != null) {
            return workItem.getParameter(parameterName);
        }
        return null;
    }

}
