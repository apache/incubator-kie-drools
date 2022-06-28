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
import {
  EmbeddedProcessDefinitionList,
  ProcessDefinition
} from '@kogito-apps/process-definition-list';
import { ProcessDefinitionListGatewayApi } from '../../../channel/ProcessDefinitionList';
import { useProcessDefinitionListGatewayApi } from '../../../channel/ProcessDefinitionList/ProcessDefinitionListContext';
import { useHistory } from 'react-router-dom';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

interface ProcessDefinitionListProps {
  singularProcessLabel: string;
}

const ProcessDefinitionListContainer: React.FC<ProcessDefinitionListProps &
  OUIAProps> = ({ singularProcessLabel, ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const appContext = useDevUIAppContext();
  const gatewayApi: ProcessDefinitionListGatewayApi = useProcessDefinitionListGatewayApi();

  useEffect(() => {
    const onOpenProcess = {
      onOpen(processDefinition: ProcessDefinition) {
        history.push({
          pathname: `ProcessDefinition/Form/${processDefinition.processName}`,
          state: {
            processDefinition: processDefinition
          }
        });
      }
    };
    const onOpenWorkflow = {
      onOpen(processDefinition: ProcessDefinition) {
        history.push({
          pathname: `WorkflowDefinition/Form/${processDefinition.processName}`,
          state: {
            workflowDefinition: {
              workflowName: processDefinition.processName,
              endpoint: processDefinition.endpoint
            }
          }
        });
      }
    };
    const unsubscriber = gatewayApi.onOpenProcessFormListen(
      appContext.isWorkflow() ? onOpenWorkflow : onOpenProcess
    );
    return () => {
      unsubscriber.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedProcessDefinitionList
      {...componentOuiaProps(
        ouiaId,
        'process-definition-list-container',
        ouiaSafe
      )}
      driver={gatewayApi}
      targetOrigin={'*'}
      singularProcessLabel={singularProcessLabel}
    />
  );
};

export default ProcessDefinitionListContainer;
