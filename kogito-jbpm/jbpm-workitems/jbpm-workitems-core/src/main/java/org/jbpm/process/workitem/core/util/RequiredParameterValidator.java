/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.workitem.core.util;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequiredParameterValidator {

    private static final Logger logger = LoggerFactory.getLogger(RequiredParameterValidator.class);

    public static void validate(Class<? extends WorkItemHandler> handlerClass,
                                WorkItem workItem) throws Exception {

        Wid handlerWidAnnotation = handlerClass.getDeclaredAnnotation(Wid.class);
        if (workItem == null || handlerWidAnnotation == null || handlerWidAnnotation.parameters() == null || handlerWidAnnotation.parameters().length < 1) {
            // nothing to validate
            return;
        }

        for (WidParameter handlerWidParameter : handlerWidAnnotation.parameters()) {
            if (handlerWidParameter.name() != null && handlerWidParameter.required()) {
                if (workItem.getParameter(handlerWidParameter.name()) == null) {
                    logger.error("Workitem declares following required parameter which does not exist: " + handlerWidParameter.name());
                    throw new IllegalArgumentException("Workitem declares following required parameter which does not exist: " + handlerWidParameter.name());
                }
            }
        }
    }
}
