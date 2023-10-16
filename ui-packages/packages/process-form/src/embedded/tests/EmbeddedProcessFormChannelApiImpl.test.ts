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
import { ProcessFormDriver } from '../../api';
import { EmbeddedProcessFormChannelApiImpl } from '../EmbeddedProcessFormChannelApiImpl';
import { MockedProcessFormDriver } from './mocks/Mocks';

let driver: ProcessFormDriver;
let api: EmbeddedProcessFormChannelApiImpl;

describe('EmbeddedProcessFormChannelApiImpl tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    driver = new MockedProcessFormDriver();
    api = new EmbeddedProcessFormChannelApiImpl(driver);
  });

  it('processForm__getProcessFormSchema', () => {
    const processDefinitionData = {
      processName: 'process1',
      endpoint: 'http://localhost:4000'
    };
    api.processForm__getProcessFormSchema(processDefinitionData);

    expect(driver.getProcessFormSchema).toHaveBeenCalledWith(
      processDefinitionData
    );
  });

  it('ProcessForm__doSubmit', () => {
    const formJSON = {
      something: 'something'
    };
    api.processForm__startProcess(formJSON);

    expect(driver.startProcess).toHaveBeenCalledWith(formJSON);
  });
});
