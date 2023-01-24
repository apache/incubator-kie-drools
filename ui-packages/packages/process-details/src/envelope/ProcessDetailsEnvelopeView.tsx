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
import {
  DiagramPreviewSize,
  ProcessDetailsChannelApi,
  ProcessDetailsInitArgs
} from '../api';
import ProcessDetails from './components/ProcessDetails/ProcessDetails';
import ProcessDetailsEnvelopeViewDriver from './ProcessDetailsEnvelopeViewDriver';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import '@patternfly/patternfly/patternfly.css';

export interface ProcessDetailsEnvelopeViewApi {
  initialize: (initArgs: ProcessDetailsInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

export const ProcessDetailsEnvelopeView = React.forwardRef<
  ProcessDetailsEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [processInstance, setProcessInstance] = useState<ProcessInstance>(
    {} as ProcessInstance
  );
  const [omittedProcessTimelineEvents, setOmittedProcessTimelineEvents] =
    useState<string[]>([]);
  const [diagramPreviewSize, setDiagramPreviewSize] =
    useState<DiagramPreviewSize>();
  const [showSwfDiagram, setShowSwfDiagram] = useState<boolean>(false);
  const [isStunnerEnabled, setIsStunnerEnabled] = useState<boolean>(false);
  const [singularProcessLabel, setSingularProcessLabel] = useState<string>('');
  const [pluralProcessLabel, setPluralProcessLabel] = useState<string>('');
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (initArgs) => {
        setProcessInstance(initArgs.processInstance);
        setOmittedProcessTimelineEvents(initArgs.omittedProcessTimelineEvents);
        setDiagramPreviewSize(initArgs.diagramPreviewSize);
        setShowSwfDiagram(initArgs.showSwfDiagram);
        setIsStunnerEnabled(initArgs.isStunnerEnabled);
        setSingularProcessLabel(initArgs.singularProcessLabel);
        setPluralProcessLabel(initArgs.pluralProcessLabel);
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
        processDetails={processInstance}
        omittedProcessTimelineEvents={omittedProcessTimelineEvents}
        diagramPreviewSize={diagramPreviewSize}
        showSwfDiagram={showSwfDiagram}
        isStunnerEnabled={isStunnerEnabled}
        singularProcessLabel={singularProcessLabel}
        pluralProcessLabel={pluralProcessLabel}
      />
    </React.Fragment>
  );
});

export default ProcessDetailsEnvelopeView;
