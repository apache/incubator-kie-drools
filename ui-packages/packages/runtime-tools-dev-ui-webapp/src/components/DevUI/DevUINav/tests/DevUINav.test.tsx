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
import { render } from '@testing-library/react';
import DevUINav from '../DevUINav';
import { MemoryRouter } from 'react-router-dom';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';

describe('DevUINav tests::Process and Tracing enabled', () => {
  it('Snapshot testing with processes props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Processes'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const DevUINavWrapper = container.querySelector(
      '[data-ouia-navigation-name="processes-nav"]'
    );
    expect(DevUINavWrapper).toBeTruthy();
  });

  it('Snapshot testing with jobs management props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/JobsManagement'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const DevUINavWrapper = container.querySelector(
      '[data-ouia-navigation-name="jobs-management-nav"]'
    );
    expect(DevUINavWrapper).toBeTruthy();
  });

  it('Snapshot testing with forms list props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Forms'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const DevUINavWrapper = container.querySelector(
      '[data-ouia-navigation-name="forms-list-nav"]'
    );
    expect(DevUINavWrapper).toBeTruthy();
  });

  it('Snapshot testing audit investigation link props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        availablePages={['Processess']}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Audit'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    const DevUINavWrapper = container.querySelector(
      '[data-ouia-navigation-name="audit-nav"]'
    );
    expect(DevUINavWrapper).toBeTruthy();
  });
});

describe('DevUINav tests::Sections disabled', () => {
  it('Snapshot testing with processes props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Processes'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(
      container.querySelector('[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector(
        '[data-ouia-navigation-name="jobs-management-nav"]'
      )
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing with jobs management props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/JobsManagement'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(
      container.querySelector('[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector(
        '[data-ouia-navigation-name="jobs-management-nav"]'
      )
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing with forms list props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Forms'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(
      container.querySelector('[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector(
        '[data-ouia-navigation-name="jobs-management-nav"]'
      )
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      container.querySelector('[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing audit investigation link props', () => {
    const { container } = render(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Audit'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(container).toMatchSnapshot();

    expect(
      container.querySelector('[data-ouia-navigation-name="processes-nav"]')
    ).toBeFalsy();
    expect(
      container.querySelector(
        '[data-ouia-navigation-name="jobs-management-nav"]'
      )
    ).toBeFalsy();
    expect(
      container.querySelector('[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeFalsy();
    expect(
      container.querySelector('[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeFalsy();
    expect(
      container.querySelector('[data-ouia-navigation-name="audit-nav"]')
    ).toBeTruthy();
  });
});
