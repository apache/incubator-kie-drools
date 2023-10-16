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
import React from 'react';
import { BanIcon } from '@patternfly/react-icons/dist/js/icons/ban-icon';
import { OnRunningIcon } from '@patternfly/react-icons/dist/js/icons/on-running-icon';
import { CheckCircleIcon } from '@patternfly/react-icons/dist/js/icons/check-circle-icon';
import { PausedIcon } from '@patternfly/react-icons/dist/js/icons/paused-icon';
import { ErrorCircleOIcon } from '@patternfly/react-icons/dist/js/icons/error-circle-o-icon';
import {
  ProcessInstanceState,
  ProcessInstance
} from '@kogito-apps/management-console-shared/dist/types';
import { BulkListItem } from '@kogito-apps/management-console-shared/dist/components/BulkList';
export const ProcessInstanceIconCreator = (
  state: ProcessInstanceState
): JSX.Element => {
  switch (state) {
    case ProcessInstanceState.Active:
      return (
        <>
          <OnRunningIcon className="pf-u-mr-sm" />
          Active
        </>
      );
    case ProcessInstanceState.Completed:
      return (
        <>
          <CheckCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--success-color--100)"
          />
          Completed
        </>
      );
    case ProcessInstanceState.Aborted:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Aborted
        </>
      );
    case ProcessInstanceState.Suspended:
      return (
        <>
          <PausedIcon className="pf-u-mr-sm" />
          Suspended
        </>
      );
    case ProcessInstanceState.Error:
      return (
        <>
          <ErrorCircleOIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />
          Error
        </>
      );
  }
};

export const getProcessInstanceDescription = (
  processInstance: ProcessInstance
): {
  id: string;
  name: string;
  description: string;
} => {
  return {
    id: processInstance.id,
    name: processInstance.processName,
    description: processInstance.businessKey
  };
};

export const alterOrderByObj = (orderByObj: {
  [key: string]: string;
}): { [key: string]: string } => {
  if (orderByObj['id']) {
    orderByObj['processName'] = orderByObj['id'];
    delete orderByObj['id'];
  } else if (orderByObj['status']) {
    orderByObj['state'] = orderByObj['status'];
    delete orderByObj['status'];
  } else if (orderByObj['created']) {
    orderByObj['start'] = orderByObj['created'];
    delete orderByObj['created'];
  }
  return orderByObj;
};

export const checkProcessInstanceState = (
  processInstance: Pick<ProcessInstance, 'state' | 'addons' | 'serviceUrl'>
): boolean => {
  if (
    (processInstance.state === ProcessInstanceState.Error ||
      processInstance.state === ProcessInstanceState.Active ||
      processInstance.state === ProcessInstanceState.Suspended) &&
    processInstance.addons.includes('process-management') &&
    processInstance.serviceUrl
  ) {
    return false;
  } else {
    return true;
  }
};

export const formatForBulkListProcessInstance = (
  processInstanceList: (ProcessInstance & { errorMessage?: string })[]
): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  processInstanceList.forEach(
    (item: ProcessInstance & { errorMessage?: string }) => {
      const formattedObj: BulkListItem = {
        id: item.id,
        description: item.businessKey,
        name: item.processName,
        errorMessage: item.errorMessage ? item.errorMessage : null
      };
      formattedItems.push(formattedObj);
    }
  );
  return formattedItems;
};

export const processListDefaultStatusFilter = [ProcessInstanceState.Active];

export const workflowListDefaultStatusFilter = [
  ProcessInstanceState.Aborted,
  ProcessInstanceState.Active,
  ProcessInstanceState.Completed,
  ProcessInstanceState.Error,
  ProcessInstanceState.Suspended
];
