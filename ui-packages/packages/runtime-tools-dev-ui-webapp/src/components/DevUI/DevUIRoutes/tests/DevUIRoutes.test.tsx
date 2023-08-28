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

import React from 'react';
import { render, screen } from '@testing-library/react';
import DevUIRoutes from '../DevUIRoutes';
import { MemoryRouter } from 'react-router-dom';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';
import { ProcessListQueries } from '../../../../channel/ProcessList/ProcessListQueries';
import * as ProcessDefinitionListContext from '../../../../channel/ProcessDefinitionList/ProcessDefinitionListContext';
import { ProcessDefinitionListGatewayApiImpl } from '../../../../channel/ProcessDefinitionList/ProcessDefinitionListGatewayApi';
import * as FormsListContext from '../../../../channel/FormsList/FormsListContext';
import { FormsListGatewayApiImpl } from '../../../../channel/FormsList/FormsListGatewayApi';
import * as ProcessListContext from '../../../../channel/ProcessList/ProcessListContext';
import { ProcessListGatewayApiImpl } from '../../../../channel/ProcessList/ProcessListGatewayApi';

jest
  .spyOn(ProcessDefinitionListContext, 'useProcessDefinitionListGatewayApi')
  .mockImplementation(
    () =>
      new ProcessDefinitionListGatewayApiImpl(
        'http://localhost:9000',
        '/mocked'
      )
  );

jest
  .spyOn(FormsListContext, 'useFormsListGatewayApi')
  .mockImplementation(() => new FormsListGatewayApiImpl());

const MockQueries = jest.fn<ProcessListQueries, []>(() => ({
  getProcessInstances: jest.fn(),
  getChildProcessInstances: jest.fn(),
  handleProcessSkip: jest.fn(),
  handleProcessAbort: jest.fn(),
  handleProcessRetry: jest.fn(),
  handleProcessMultipleAction: jest.fn(),
  onOpenProcessListen: jest.fn()
}));

jest
  .spyOn(ProcessListContext, 'useProcessListGatewayApi')
  .mockImplementation(() => new ProcessListGatewayApiImpl(new MockQueries()));

const props = {
  trustyServiceUrl: 'http://url-to-service',
  navigate: 'JobsManagement'
};

describe('DevUIRoutes tests::Process and Tracing enabled', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('Test Jobs management route', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkJobsManagement = screen.getByText('Jobs Management');
    expect(checkJobsManagement).toBeTruthy();
    const checkJobsManagementSection = container.querySelector(
      'section[data-ouia-component-type="jobs-management-page-section"]'
    );
    expect(checkJobsManagementSection).toBeTruthy();
  });
  it('processes test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{ singularProcessLabel: '', pluralProcessLabel: '' }}
      >
        <MemoryRouter keyLength={0} initialEntries={['/Processes']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();
    const checkProcessListPage = document.querySelector(
      'body[data-ouia-page-type="process-instances"]'
    );
    expect(checkProcessListPage).toBeTruthy();
  });
  it('jobs management page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/JobsManagement']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkJobsManagement = screen.getByText('Jobs Management');
    expect(checkJobsManagement).toBeTruthy();
    const checkJobsManagementSection = container.querySelector(
      'section[data-ouia-component-type="jobs-management-page-section"]'
    );
    expect(checkJobsManagementSection).toBeTruthy();
  });

  it('forms list page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/Forms']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkProcessListPage = document.querySelector(
      'body[data-ouia-page-type="forms-list"]'
    );
    expect(checkProcessListPage).toBeTruthy();

    const checkTitle = screen.getByText('Forms');
    expect(checkTitle).toBeTruthy();
  });

  it('audit investigation page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/Audit']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkAuditPage = screen.getByText('Audit investigation');
    expect(checkAuditPage).toBeTruthy();
  });

  it('no data page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/NoData']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();
    const checkNodataTitle = screen.getByText('No matches');
    expect(checkNodataTitle).toBeTruthy();
    const checkNodataBody = screen.getByText('No data to display');
    expect(checkNodataBody).toBeTruthy();
  });

  it('page not found page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['*']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');
  });

  it('Test NoData route', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/NoData']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const checkNodataTitle = screen.getByText('No matches');
    expect(checkNodataTitle).toBeTruthy();
    const checkNodataBody = screen.getByText('No data to display');
    expect(checkNodataBody).toBeTruthy();
  });

  it('Test PageNotFound route', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['*']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();
    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');
  });
});

describe('DevUIRoutes tests::Sections disabled', () => {
  it('Test Jobs management route', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkJobsManagementPage = container.querySelector(
      'section[data-ouia-component-type="jobs-management"]'
    );
    expect(checkJobsManagementPage).toBeFalsy();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');

    const checkButton = container.querySelector('button').textContent;
    expect(checkButton).toEqual('Go to audit');
  });

  it('process list test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/ProcessInstances']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkProcessListPage = container.querySelector(
      'section[data-ouia-component-type="process-list"]'
    );
    expect(checkProcessListPage).toBeFalsy();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');

    const checkButton = container.querySelector('button').textContent;
    expect(checkButton).toEqual('Go to audit');
  });

  it('jobs management page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/JobsManagement']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkJobsManagementPage = container.querySelector(
      'section[data-ouia-component-type="jobs-management"]'
    );
    expect(checkJobsManagementPage).toBeFalsy();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');

    const checkButton = container.querySelector('button').textContent;
    expect(checkButton).toEqual('Go to audit');
  });

  it('forms list page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
      >
        <MemoryRouter keyLength={0} initialEntries={['/Forms']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkJobsManagementPage = container.querySelector(
      'section[data-ouia-component-type="forms-list"]'
    );
    expect(checkJobsManagementPage).toBeFalsy();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');

    const checkButton = container.querySelector('button').textContent;
    expect(checkButton).toEqual('Go to audit');
  });

  it('audit investigation page test', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
      >
        <MemoryRouter keyLength={0} initialEntries={['/Audit']}>
          <DevUIRoutes {...props} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    const checkJobsManagementPage = container.querySelector(
      'section[data-ouia-component-type="trusty-app"]'
    );
    expect(checkJobsManagementPage).toBeFalsy();

    const checkPageNotFoundPage = container.querySelector(
      'section[data-ouia-component-type="page-not-found"]'
    );
    expect(checkPageNotFoundPage).toBeTruthy();

    const checkTitle = container.querySelector('h1').textContent;
    expect(checkTitle).toEqual('404 Error: page not found');

    const checkButton = container.querySelector('button').textContent;
    expect(checkButton).toEqual('Go to jobs management');
  });
});
