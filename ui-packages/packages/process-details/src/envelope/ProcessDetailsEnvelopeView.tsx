/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from 'react';
import { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import { ProcessDetailsChannelApi } from '../api';
import ProcessDetails from './components/ProcessDetails/ProcessDetails';
import ProcessDetailsEnvelopeViewDriver from './ProcessDetailsEnvelopeViewDriver';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import '@patternfly/patternfly/patternfly.css';

export interface ProcessDetailsEnvelopeViewApi {
  initialize: (processInstance?: ProcessInstance) => void;
}

interface Props {
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

export const ProcessDetailsEnvelopeView = React.forwardRef<
  ProcessDetailsEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [
    isEnvelopeConnectedToChannel,
    setEnvelopeConnectedToChannel
  ] = useState<boolean>(false);
  const [processDetails, setProcessDetails] = useState<ProcessInstance>(
    {} as ProcessInstance
  );
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: processInstance => {
        setProcessDetails(processInstance);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );

  return (
    <React.Fragment>
      <ProcessDetails
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        driver={new ProcessDetailsEnvelopeViewDriver(props.channelApi)}
        processDetails={processDetails}
      />
    </React.Fragment>
  );
});

export default ProcessDetailsEnvelopeView;
