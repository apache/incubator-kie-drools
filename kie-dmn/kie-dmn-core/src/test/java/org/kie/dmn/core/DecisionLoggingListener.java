/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.impl.DMNEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionLoggingListener extends DefaultDMNRuntimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DecisionLoggingListener.class);

    @Override
    public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {
        logger.info("Decision Service '{}' completed - attempting to extract outputs...",
                event.getDecisionService().getName());
        try {
            logger.info("Calling DMNEventUtils.extractDSOutputDecisionsValues(event)...");
            var outputs = DMNEventUtils.extractDSOutputDecisionsValues(event);
            logger.info("Successfully extracted outputs: {}", outputs);
        } catch (NullPointerException e) {
            logger.error("Exception caught! ", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected exception occurred: ", e);
            throw e;
        }
    }
}
