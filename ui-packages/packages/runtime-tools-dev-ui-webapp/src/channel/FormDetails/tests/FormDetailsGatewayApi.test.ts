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
import { FormContent } from '@kogito-apps/form-details/dist/api/FormDetailsEnvelopeApi';
import { getFormContent, saveFormContent } from '../../apis/apis';
import {
  FormDetailsGatewayApi,
  FormDetailsGatewayApiImpl
} from '../FormDetailsGatewayApi';

jest.mock('../../apis/apis', () => ({
  getFormContent: jest.fn(),
  saveFormContent: jest.fn()
}));

let gatewayApi: FormDetailsGatewayApi;

describe('FormDetailsGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new FormDetailsGatewayApiImpl();
  });

  it('getFormContent', async () => {
    const formName = 'form1';
    await gatewayApi.getFormContent(formName);
    expect(getFormContent).toHaveBeenCalledWith(formName);
  });

  it('saveFormContent', async () => {
    const formName = 'form1';
    const content: FormContent = {
      source: '',
      configuration: {
        schema: '',
        resources: {
          scripts: {
            name: 'test1'
          },
          styles: {
            padding: 'test2'
          }
        }
      }
    };
    await gatewayApi.saveFormContent(formName, content);
    expect(saveFormContent).toHaveBeenCalledWith(formName, content);
  });
});
