/**
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
import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { ProcessDefinition, ProcessFormChannelApi } from '../api';
import '@patternfly/patternfly/patternfly.css';
import ProcessForm from './components/ProcessForm/ProcessForm';
import { ProcessFormEnvelopeViewDriver } from './ProcessFormEnvelopeViewDriver';

export interface ProcessFormEnvelopeViewApi {
  initialize: (processDefinitionData: ProcessDefinition) => void;
}

interface Props {
  channelApi: MessageBusClientApi<ProcessFormChannelApi>;
  targetOrigin: string;
}

export const ProcessFormEnvelopeView = React.forwardRef<
  ProcessFormEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [processDefinition, setProcessDefinition] =
    useState<ProcessDefinition>();
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (processDefinitionData: ProcessDefinition) => {
        setProcessDefinition(processDefinitionData);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );
  return (
    <ProcessForm
      isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
      processDefinition={processDefinition}
      driver={new ProcessFormEnvelopeViewDriver(props.channelApi)}
      targetOrigin={props.targetOrigin}
    />
  );
});

export default ProcessFormEnvelopeView;
