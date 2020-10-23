/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.serverless.workflow.api.interfaces;

import org.jbpm.serverless.workflow.api.end.End;
import org.jbpm.serverless.workflow.api.error.Error;
import org.jbpm.serverless.workflow.api.filters.StateDataFilter;
import org.jbpm.serverless.workflow.api.retry.Retry;
import org.jbpm.serverless.workflow.api.start.Start;
import org.jbpm.serverless.workflow.api.states.DefaultState.Type;
import org.jbpm.serverless.workflow.api.transitions.Transition;

import java.util.List;
import java.util.Map;

public interface State {

    String getId();

    String getName();

    Type getType();

    Start getStart();

    End getEnd();

    StateDataFilter getStateDataFilter();

    String getDataInputSchema();

    String getDataOutputSchema();

    Transition getTransition();

    List<Error> getOnError();

    List<Retry> getRetry();

    Map<String, String> getMetadata();
}