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

import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import {
  ProcessDefinitionListChannelApi,
  ProcessDefinitionListInitArgs
} from '../api';
import ProcessDefinitionList from './components/ProcessDefinitionList/ProcessDefinitionList';
import ProcessDefinitionListEnvelopeViewDriver from './ProcessDefinitionListEnvelopeViewDriver';
import '@patternfly/patternfly/patternfly.css';

export interface ProcessDefinitionListEnvelopeViewApi {
  initialize: (initArgs: ProcessDefinitionListInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<ProcessDefinitionListChannelApi>;
}

export const ProcessDefinitionListEnvelopeView = React.forwardRef<
  ProcessDefinitionListEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [singularProcessLabel, setSingularProcessLabel] = useState<string>('');

  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (initArgs) => {
        setSingularProcessLabel(initArgs.singularProcessLabel);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );

  return (
    <React.Fragment>
      <ProcessDefinitionList
        isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
        driver={new ProcessDefinitionListEnvelopeViewDriver(props.channelApi)}
        singularProcessLabel={singularProcessLabel}
      />
    </React.Fragment>
  );
});

export default ProcessDefinitionListEnvelopeView;
