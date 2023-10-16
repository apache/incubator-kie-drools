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
import { ProcessDefinition } from '@kogito-apps/process-definition-list';
import { getProcessDefinitionList } from '../../apis';
import {
  OnOpenProcessFormListener,
  ProcessDefinitionListGatewayApi,
  ProcessDefinitionListGatewayApiImpl
} from '../ProcessDefinitionListGatewayApi';

jest.mock('../../apis/apis', () => ({
  getProcessDefinitionList: jest.fn()
}));

let gatewayApi: ProcessDefinitionListGatewayApi;

describe('ProcessDefinitionListListGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new ProcessDefinitionListGatewayApiImpl(
      'http://localhost:8080',
      '/docs/openapi.json'
    );
  });

  it('get process definition query', async () => {
    gatewayApi.getProcessDefinitionsQuery();
    expect(getProcessDefinitionList).toHaveBeenCalled();
  });

  it('getter and setter on filter', async () => {
    const filter = ['process1'];
    gatewayApi.setProcessDefinitionFilter(filter);
    expect(await gatewayApi.getProcessDefinitionFilter()).toEqual(filter);
  });
  it('openForm', () => {
    const processDefinitionData: ProcessDefinition = {
      processName: 'process1',
      endpoint: 'http://localhost:8080/hiring'
    };
    const listener: OnOpenProcessFormListener = {
      onOpen: jest.fn()
    };

    const unsubscribe = gatewayApi.onOpenProcessFormListen(listener);

    gatewayApi.openProcessForm(processDefinitionData);

    expect(listener.onOpen).toHaveBeenLastCalledWith(processDefinitionData);

    unsubscribe.unSubscribe();
  });
});
