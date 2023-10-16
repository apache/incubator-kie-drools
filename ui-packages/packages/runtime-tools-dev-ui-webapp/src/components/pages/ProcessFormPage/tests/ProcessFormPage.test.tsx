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
import { fireEvent, render, screen } from '@testing-library/react';
import ProcessFormPage from '../ProcessFormPage';
import { BrowserRouter } from 'react-router-dom';
import * as ProcessFormContext from '../../../../channel/ProcessForm/ProcessFormContext';
import { ProcessFormGatewayApi } from '../../../../channel/ProcessForm/ProcessFormGatewayApi';
import * as DevUIAppContext from '../../../contexts/DevUIAppContext';

jest.mock('../components/InlineEdit/InlineEdit');

jest.mock('../../../containers/ProcessFormContainer/ProcessFormContainer');

const mockHistoryPush = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useHistory: () => ({
    push: mockHistoryPush,
    location: {
      state: {
        processDefinition: {
          processName: 'process1',
          endpoint: 'http://localhost:4000'
        }
      }
    }
  })
}));

const MockProcessFormGatewayApi = jest.fn<ProcessFormGatewayApi, []>(() => ({
  setBusinessKey: jest.fn(),
  getBusinessKey: jest.fn(),
  currentBusinessKey: ''
}));

const MockDevUIApp = jest.fn(() => ({
  getDevUIUrl: jest.fn()
}));

const gatewayApi = new MockProcessFormGatewayApi();

const devUIApp = new MockDevUIApp();

jest
  .spyOn(DevUIAppContext, 'useDevUIAppContext')
  .mockImplementation(() => devUIApp);

jest
  .spyOn(ProcessFormContext, 'useProcessFormGatewayApi')
  .mockImplementation(() => gatewayApi);

describe('ProcessFormPage tests', () => {
  it('Snapshot', () => {
    const { container } = render(
      <BrowserRouter>
        <ProcessFormPage />
      </BrowserRouter>
    );
    expect(container).toMatchSnapshot();
    const checkProcessForm = container.querySelector(
      'section[data-ouia-component-type="process-form-page-section"]'
    );
    expect(checkProcessForm).toBeTruthy();
    expect(container.querySelector('h1').textContent).toEqual('Start process1');
  });
  it('Test Go to process List in Alert component', () => {
    render(
      <BrowserRouter>
        <ProcessFormPage />
      </BrowserRouter>
    );

    const button = screen.getByText('Go to process list');
    fireEvent.click(button);
    expect(mockHistoryPush).toHaveBeenCalledWith('/Processes');
  });

  it('Test Go to process List in Alert component', () => {
    render(
      <BrowserRouter>
        <ProcessFormPage />
      </BrowserRouter>
    );

    const button = screen.getByText('Go to Process details');
    fireEvent.click(button);
    expect(mockHistoryPush).toHaveBeenCalledWith('/Processes');
  });

  it('Test close action in Alert component', () => {
    render(
      <BrowserRouter>
        <ProcessFormPage />
      </BrowserRouter>
    );

    const button = screen.getByTestId('close-button');
    fireEvent.click(button);
  });
});
