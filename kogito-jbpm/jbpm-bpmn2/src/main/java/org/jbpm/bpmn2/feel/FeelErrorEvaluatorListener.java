/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.feel;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeelErrorEvaluatorListener implements FEELEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(FeelErrorEvaluatorListener.class);

    private final List<FEELEvent> errorEvents = new ArrayList<>();

    @Override
    public void onEvent(FEELEvent event) {
        switch (event.getSeverity()) {
            case ERROR:
                errorEvents.add(event);
                LOG.error("{}", event);
                break;
            case TRACE:
                LOG.debug("{}", event);
                break;
            case WARN:
                LOG.warn("{}", event);
                break;
            case INFO:
            default:
                LOG.info("{}", event);
                break;
        }
    }

    public List<FEELEvent> getErrorEvents() {
        return errorEvents;
    }
}