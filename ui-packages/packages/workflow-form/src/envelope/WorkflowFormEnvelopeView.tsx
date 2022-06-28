/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.s
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

import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import { WorkflowDefinition, WorkflowFormChannelApi } from '../api';
import '@patternfly/patternfly/patternfly.css';
import WorkflowForm from './components/WorkflowForm/WorkflowForm';
import { WorkflowFormEnvelopeViewDriver } from './WorkflowFormEnvelopeViewDriver';

export interface WorkflowFormEnvelopeViewApi {
  initialize: (workflowDefinitionData: WorkflowDefinition) => void;
}

interface Props {
  channelApi: MessageBusClientApi<WorkflowFormChannelApi>;
}

export const WorkflowFormEnvelopeView = React.forwardRef<
  WorkflowFormEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [workflowDefinition, setWorkflowDefinition] = useState<
    WorkflowDefinition
  >();
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (workflowDefinitionData: WorkflowDefinition) => {
        setWorkflowDefinition(workflowDefinitionData);
      }
    }),
    []
  );
  return (
    <WorkflowForm
      workflowDefinition={workflowDefinition}
      driver={new WorkflowFormEnvelopeViewDriver(props.channelApi)}
    />
  );
});

export default WorkflowFormEnvelopeView;
