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
import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { EmbeddedProcessList } from '@kogito-apps/process-list';
import {
  ProcessListGatewayApi,
  useProcessListGatewayApi
} from '../../../channel/ProcessList';
import {
  ProcessInstance,
  ProcessListState
} from '@kogito-apps/management-console-shared/dist/types';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';
import { CloudEventPageSource } from '../../pages/CloudEventFormPage/CloudEventFormPage';

interface ProcessListContainerProps {
  initialState: ProcessListState;
}

const ProcessListContainer: React.FC<ProcessListContainerProps & OUIAProps> = ({
  initialState,
  ouiaId,
  ouiaSafe
}) => {
  const history = useHistory();
  const gatewayApi: ProcessListGatewayApi = useProcessListGatewayApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const onOpenInstanceUnsubscriber = gatewayApi.onOpenProcessListen({
      onOpen(process: ProcessInstance) {
        history.push({
          pathname: `/Process/${process.id}`,
          state: gatewayApi.processListState
        });
      }
    });
    const onTriggerCloudEventUnsubscriber = appContext.isWorkflow()
      ? gatewayApi.onOpenTriggerCloudEventListen({
          onOpen(processInstance?: ProcessInstance) {
            history.push({
              pathname: `/Processes/CloudEvent/${processInstance?.id ?? ''}`,
              state: {
                source: CloudEventPageSource.INSTANCES
              }
            });
          }
        })
      : undefined;
    return () => {
      onOpenInstanceUnsubscriber.unSubscribe();
      onTriggerCloudEventUnsubscriber?.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedProcessList
      {...componentOuiaProps(ouiaId, 'process-list-container', ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
      initialState={initialState}
      singularProcessLabel={appContext.customLabels.singularProcessLabel}
      pluralProcessLabel={appContext.customLabels.pluralProcessLabel}
      isTriggerCloudEventEnabled={appContext.isWorkflow()}
      isWorkflow={appContext.isWorkflow()}
    />
  );
};

export default ProcessListContainer;
