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
import { FormInfo } from '@kogito-apps/forms-list';
import {
  FormsListGatewayApi,
  FormsListGatewayApiImpl,
  OnOpenFormListener
} from '../FormsListGatewayApi';

jest.mock('../../apis/apis', () => ({
  getForms: jest.fn()
}));

let gatewayApi: FormsListGatewayApi;

describe('FormsListGatewayApi tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    gatewayApi = new FormsListGatewayApiImpl();
  });

  it('applyFilter', async () => {
    const formsFilter = {
      formNames: ['form1']
    };
    gatewayApi.applyFilter(formsFilter);
    expect(await gatewayApi.getFormFilter()).toEqual(formsFilter);
  });

  it('getForms', async () => {
    const formsFilter = {
      formNames: ['form1']
    };
    gatewayApi.applyFilter(formsFilter);
    gatewayApi.getFormsQuery();
    expect(await gatewayApi.getFormFilter()).toEqual(formsFilter);
  });

  it('openForm', () => {
    const form: FormInfo = {
      name: 'form1',
      type: 'html',
      lastModified: new Date(2020, 6, 12)
    };
    const listener: OnOpenFormListener = {
      onOpen: jest.fn()
    };

    const unsubscribe = gatewayApi.onOpenFormListen(listener);

    gatewayApi.openForm(form);

    expect(listener.onOpen).toHaveBeenLastCalledWith(form);

    unsubscribe.unSubscribe();
  });
});
