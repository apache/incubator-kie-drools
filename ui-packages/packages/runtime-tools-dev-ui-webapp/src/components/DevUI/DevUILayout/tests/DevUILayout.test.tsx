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
import DevUILayout from '../DevUILayout';
import DevUIRoutes from '../../DevUIRoutes/DevUIRoutes';
import { ApolloClient } from 'apollo-client';
import { MemoryRouter } from 'react-router-dom';
import { InMemoryCache } from 'apollo-cache-inmemory';

jest.mock('../../DevUIRoutes/DevUIRoutes');

jest.mock('apollo-client');
const ApolloClientMock = ApolloClient as jest.MockedClass<typeof ApolloClient>;
const cache = new InMemoryCache();

describe('DevUILayout tests', () => {
  it('Snapshot test with default props', () => {
    const client = new ApolloClientMock({ cache: cache });
    const props = {
      apolloClient: client,
      users: [{ id: 'John snow', groups: ['admin'] }],
      devUIUrl: 'http://localhost:8080',
      openApiPath: '/docs/opeapi.json',
      isProcessEnabled: true,
      isTracingEnabled: true,
      customLabels: {
        singularProcessLabel: 'Workflow',
        pluralProcessLabel: 'Workflows'
      },
      isStunnerEnabled: false
    };
    const { container } = render(
      <DevUILayout {...props}>
        <MemoryRouter initialEntries={['/']} keyLength={0}>
          <DevUIRoutes
            dataIndexUrl={'http://localhost:8180'}
            trustyServiceUrl={'http://localhost:8081'}
            navigate={'JobsManagement'}
          />
        </MemoryRouter>
      </DevUILayout>
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
    ).toBeTruthy();
  });

  it('Snapshot test with Processing disabled', () => {
    const client = new ApolloClientMock({ cache: cache });
    const props = {
      apolloClient: client,
      users: [{ id: 'John snow', groups: ['admin'] }],
      devUIUrl: 'http://localhost:8080',
      openApiPath: '/docs/opeapi.json',
      isProcessEnabled: false,
      isTracingEnabled: true,
      customLabels: {
        singularProcessLabel: 'Workflow',
        pluralProcessLabel: 'Workflows'
      },
      isStunnerEnabled: false
    };
    const { container } = render(
      <DevUILayout {...props}>
        <MemoryRouter initialEntries={['/']} keyLength={0}>
          <DevUIRoutes
            dataIndexUrl={'http://localhost:8180'}
            trustyServiceUrl={'http://localhost:8081'}
            navigate={'JobsManagement'}
          />
        </MemoryRouter>
      </DevUILayout>
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

  it('Snapshot test with Tracing disabled', () => {
    const client = new ApolloClientMock({ cache: cache });
    const props = {
      apolloClient: client,
      users: [{ id: 'John snow', groups: ['admin'] }],
      devUIUrl: 'http://localhost:8080',
      openApiPath: '/docs/opeapi.json',
      isProcessEnabled: true,
      isTracingEnabled: false,
      customLabels: {
        singularProcessLabel: 'Workflow',
        pluralProcessLabel: 'Workflows'
      },
      isStunnerEnabled: false
    };
    const { container } = render(
      <DevUILayout {...props}>
        <MemoryRouter initialEntries={['/']} keyLength={0}>
          <DevUIRoutes
            dataIndexUrl={'http://localhost:8180'}
            trustyServiceUrl={'http://localhost:8081'}
            navigate={'JobsManagement'}
          />
        </MemoryRouter>
      </DevUILayout>
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
});
