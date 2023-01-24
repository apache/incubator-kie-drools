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

import React, { useEffect } from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import { EmbeddedProcessDetails } from '@kogito-apps/process-details';
import { ProcessDetailsGatewayApi } from '../../../channel/ProcessDetails';
import { useProcessDetailsGatewayApi } from '../../../channel/ProcessDetails/ProcessDetailsContext';
import { useHistory } from 'react-router-dom';
import { DiagramPreviewSize } from '@kogito-apps/process-details/dist/api';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

interface ProcessDetailsContainerProps {
  processInstance: ProcessInstance;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
}

const ProcessDetailsContainer: React.FC<
  ProcessDetailsContainerProps & OUIAProps
> = ({
  processInstance,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  ouiaId,
  ouiaSafe
}) => {
  const history = useHistory();
  const appContext = useDevUIAppContext();
  const gatewayApi: ProcessDetailsGatewayApi = useProcessDetailsGatewayApi();
  useEffect(() => {
    const unSubscribeHandler = gatewayApi.onOpenProcessInstanceDetailsListener({
      onOpen(id: string) {
        history.push(`/`);
        history.push(`/Process/${id}`);
      }
    });

    return () => {
      unSubscribeHandler.unSubscribe();
    };
  }, [processInstance]);
  return (
    <EmbeddedProcessDetails
      {...componentOuiaProps(ouiaId, 'process-details-container', ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={'*'}
      processInstance={processInstance}
      omittedProcessTimelineEvents={omittedProcessTimelineEvents}
      diagramPreviewSize={diagramPreviewSize}
      showSwfDiagram={appContext.isWorkflow()}
      isStunnerEnabled={appContext.getIsStunnerEnabled()}
      singularProcessLabel={appContext.customLabels.singularProcessLabel}
      pluralProcessLabel={appContext.customLabels.pluralProcessLabel}
    />
  );
};

export default ProcessDetailsContainer;
