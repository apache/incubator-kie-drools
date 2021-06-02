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
import { ProcessListChannelApi, ProcessListState } from '../api';
import ProcessListPage from './components/ProcessListPage/ProcessListPage';
import ProcessListEnvelopeViewDriver from './ProcessListEnvelopeViewDriver';
import '@patternfly/patternfly/patternfly.css';

export interface ProcessListEnvelopeViewApi {
  initialize: (initalState?: ProcessListState) => void;
}
interface Props {
  channelApi: MessageBusClientApi<ProcessListChannelApi>;
}

export const ProcessListEnvelopeView = React.forwardRef<
  ProcessListEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [
    isEnvelopeConnectedToChannel,
    setEnvelopeConnectedToChannel
  ] = useState<boolean>(false);
  const [processInitialState, setProcessInitialState] = useState<
    ProcessListState
  >({} as ProcessListState);
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: initialState => {
        setEnvelopeConnectedToChannel(false);
        setProcessInitialState(initialState);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );

  return (
    <React.Fragment>
      <ProcessListPage
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        driver={new ProcessListEnvelopeViewDriver(props.channelApi)}
        initialState={processInitialState}
      />
    </React.Fragment>
  );
});

export default ProcessListEnvelopeView;
