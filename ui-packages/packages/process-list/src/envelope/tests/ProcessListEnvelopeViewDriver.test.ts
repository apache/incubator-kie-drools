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

import {
  MessageBusClientApi,
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { MockedMessageBusClientApi, processInstance } from './mocks/Mocks';
import ProcessListEnvelopeViewDriver from '../ProcessListEnvelopeViewDriver';
import { ProcessListChannelApi } from '../../api';
import {
  OperationType,
  ProcessInstanceState,
  OrderBy,
  ProcessInstanceFilter,
  ProcessListSortBy
} from '@kogito-apps/management-console-shared';

let channelApi: MessageBusClientApi<ProcessListChannelApi>;
let requests: Pick<
  ProcessListChannelApi,
  RequestPropertyNames<ProcessListChannelApi>
>;
let driver: ProcessListEnvelopeViewDriver;

describe('ProcessListEnvelopeViewDriver tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    channelApi = new MockedMessageBusClientApi();
    requests = channelApi.requests;
    driver = new ProcessListEnvelopeViewDriver(channelApi);
  });

  describe('Requests', () => {
    it('initial Load', () => {
      const initialState = {
        filters: {
          status: [ProcessInstanceState.Active]
        },
        sortBy: { lastUpdate: OrderBy.DESC }
      };

      driver.initialLoad(initialState.filters, initialState.sortBy);

      expect(requests.processList__initialLoad).toHaveBeenCalledWith(
        initialState.filters,
        initialState.sortBy
      );
    });

    it('applyFilter', () => {
      const filter: ProcessInstanceFilter = {
        status: [ProcessInstanceState.Active],
        businessKey: []
      };
      driver.applyFilter(filter);

      expect(requests.processList__applyFilter).toHaveBeenCalledWith(filter);
    });

    it('applySorting', () => {
      const sortBy: ProcessListSortBy = {
        lastUpdate: OrderBy.DESC
      };
      driver.applySorting(sortBy);

      expect(requests.processList__applySorting).toHaveBeenCalledWith(sortBy);
    });

    it('handleProcessSkip', () => {
      driver.handleProcessSkip(processInstance);
      expect(requests.processList__handleProcessSkip).toHaveBeenCalledWith(
        processInstance
      );
    });

    it('handleProcessRetry', () => {
      driver.handleProcessRetry(processInstance);
      expect(requests.processList__handleProcessRetry).toHaveBeenCalledWith(
        processInstance
      );
    });

    it('handleProcessAbort', () => {
      driver.handleProcessAbort(processInstance);
      expect(requests.processList__handleProcessAbort).toHaveBeenCalledWith(
        processInstance
      );
    });

    it('handleProcessMultipleAction', () => {
      driver.handleProcessMultipleAction(
        [processInstance],
        OperationType.ABORT
      );
      expect(
        requests.processList__handleProcessMultipleAction
      ).toHaveBeenCalledWith([processInstance], OperationType.ABORT);
    });

    it('query', () => {
      driver.query(0, 10);
      expect(requests.processList__query).toHaveBeenCalledWith(0, 10);
    });

    it('get child query', () => {
      const rootProcessInstanceId = 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863';
      driver.getChildProcessesQuery(rootProcessInstanceId);
      expect(requests.processList__getChildProcessesQuery).toHaveBeenCalledWith(
        rootProcessInstanceId
      );
    });
  });
});
