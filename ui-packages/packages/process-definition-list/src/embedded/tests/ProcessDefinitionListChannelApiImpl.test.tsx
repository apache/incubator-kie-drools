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
import { ProcessDefinitionListDriver } from '../../api';
import { ProcessDefinitionListChannelApiImpl } from '../ProcessDefinitionListChannelApiImpl';
import { MockedProcessDefinitionListDriver } from './utils/Mocks';

let driver: ProcessDefinitionListDriver;
let api: ProcessDefinitionListChannelApiImpl;

describe('ProcessDefinitionListChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedProcessDefinitionListDriver();
    api = new ProcessDefinitionListChannelApiImpl(driver);
  });

  it('ProcessDefinitionList__getProcessDefinitionsQuery', () => {
    api.processDefinitionList__getProcessDefinitionsQuery();
    expect(driver.getProcessDefinitionsQuery).toHaveBeenCalled();
  });
  it('ProcessDefinitionList__getProcessDefinitionsFilter', () => {
    api.processDefinitionList__getProcessDefinitionFilter();
    expect(driver.getProcessDefinitionFilter).toHaveBeenCalled();
  });

  it('ProcessDefinitionList__setProcessDefinitionFilter', () => {
    const filter = ['process1'];
    api.processDefinitionList__setProcessDefinitionFilter(filter);
    expect(driver.setProcessDefinitionFilter).toHaveBeenCalledWith(filter);
  });

  it('ProcessDefinitionList__getProcessDefinitionsQuery', () => {
    const processDefinition = {
      processName: 'process1',
      endpoint: 'http://localhost:4000'
    };
    api.processDefinitionList__openProcessForm(processDefinition);
    expect(driver.openProcessForm).toHaveBeenCalledWith(processDefinition);
  });
});
