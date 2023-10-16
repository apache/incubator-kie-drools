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
import {
  MessageBusClientApi,
  RequestPropertyNames
} from '@kie-tools-core/envelope-bus/dist/api';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessDefinitionListEnvelopeViewDriver from '../ProcessDefinitionListEnvelopeViewDriver';
import { ProcessDefinition, ProcessDefinitionListChannelApi } from '../../api';

let channelApi: MessageBusClientApi<ProcessDefinitionListChannelApi>;
let requests: Pick<
  ProcessDefinitionListChannelApi,
  RequestPropertyNames<ProcessDefinitionListChannelApi>
>;
let driver: ProcessDefinitionListEnvelopeViewDriver;

describe('ProcessDefinitionListEnvelopeViewDriver tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    channelApi = new MockedMessageBusClientApi();
    requests = channelApi.requests;
    driver = new ProcessDefinitionListEnvelopeViewDriver(channelApi);
  });

  describe('Requests', () => {
    it('get ProcessDefinition query', () => {
      driver.getProcessDefinitionsQuery();
      expect(
        requests.processDefinitionList__getProcessDefinitionsQuery
      ).toHaveBeenCalled();
    });

    it('get ProcessDefinition filter', () => {
      driver.getProcessDefinitionFilter();
      expect(
        requests.processDefinitionList__getProcessDefinitionFilter
      ).toHaveBeenCalled();
    });

    it('set ProcessDefinition filter', () => {
      const filter = ['process1'];
      driver.setProcessDefinitionFilter(filter);
      expect(
        requests.processDefinitionList__setProcessDefinitionFilter
      ).toHaveBeenCalledWith(filter);
    });

    it('open form', () => {
      const processDefinition: ProcessDefinition = {
        processName: 'process1',
        endpoint: 'http://localhost:4000'
      };
      driver.openProcessForm(processDefinition);
      expect(
        requests.processDefinitionList__openProcessForm
      ).toHaveBeenCalledWith(processDefinition);
    });
  });
});
