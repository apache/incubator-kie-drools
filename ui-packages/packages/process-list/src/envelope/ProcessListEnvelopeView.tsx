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
import * as React from 'react';
import { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { ProcessListChannelApi, ProcessListInitArgs } from '../api';
import ProcessList from './components/ProcessList/ProcessList';
import ProcessListEnvelopeViewDriver from './ProcessListEnvelopeViewDriver';
import '@patternfly/patternfly/patternfly.css';

export interface ProcessListEnvelopeViewApi {
  initialize: (initialState?: ProcessListInitArgs) => void;
}
interface Props {
  channelApi: MessageBusClientApi<ProcessListChannelApi>;
}

export const ProcessListEnvelopeView = React.forwardRef<
  ProcessListEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [processInitialState, setProcessInitialState] =
    useState<ProcessListInitArgs>({} as ProcessListInitArgs);
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (initialState) => {
        setEnvelopeConnectedToChannel(false);
        setProcessInitialState(initialState);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );

  return (
    <React.Fragment>
      <ProcessList
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        driver={new ProcessListEnvelopeViewDriver(props.channelApi)}
        initialState={processInitialState.initialState}
        singularProcessLabel={processInitialState.singularProcessLabel}
        pluralProcessLabel={processInitialState.pluralProcessLabel}
        isTriggerCloudEventEnabled={
          processInitialState.isTriggerCloudEventEnabled
        }
        isWorkflow={processInitialState.isWorkflow}
      />
    </React.Fragment>
  );
});

export default ProcessListEnvelopeView;
