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
import { getProcessSchema, startProcessInstance } from '../../apis';
import {
  ProcessFormGatewayApi,
  ProcessFormGatewayApiImpl
} from '../ProcessFormGatewayApi';

jest.mock('../../apis/apis', () => ({
  getProcessSchema: jest.fn(),
  startProcessInstance: jest.fn()
}));

let gatewayApi: ProcessFormGatewayApi;

describe('ProcessFormListGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new ProcessFormGatewayApiImpl();
  });

  it('get process definition query', async () => {
    const processDefinitionData = {
      processName: 'process1',
      endpoint: 'http://localhost:8080/hiring'
    };
    await gatewayApi.getProcessFormSchema(processDefinitionData);
    expect(getProcessSchema).toHaveBeenCalledWith(processDefinitionData);
  });

  it('getter and setter on filter', async () => {
    const businesskey = 'AAA';
    gatewayApi.setBusinessKey(businesskey);
    expect(await gatewayApi.getBusinessKey()).toEqual(businesskey);
  });

  it('start process instance', async () => {
    const processDefinitionData: ProcessDefinition = {
      processName: 'process1',
      endpoint: 'http://localhost:8080/hiring'
    };
    gatewayApi.setBusinessKey('AAA');
    await gatewayApi.startProcess({}, processDefinitionData);
    expect(startProcessInstance).toHaveBeenCalledWith(
      {},
      'AAA',
      processDefinitionData
    );
  });
});
