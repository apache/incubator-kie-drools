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
import { mount } from 'enzyme';
import DevUILayout from '../DevUILayout';
import DevUIRoutes from '../../DevUIRoutes/DevUIRoutes';
import { ApolloClient } from 'apollo-client';
import { MemoryRouter } from 'react-router-dom';

jest.mock('../../DevUIRoutes/DevUIRoutes');

jest.mock('apollo-client');
const ApolloClientMock = ApolloClient as jest.MockedClass<typeof ApolloClient>;

describe('DevUILayout tests', () => {
  it('Snapshot test with default props', () => {
    // eslint-disable-next-line
    const client = new ApolloClientMock();
    const props = {
      apolloClient: client,
      users: [{ id: 'John snow', groups: ['admin'] }],
      devUIUrl: 'http://localhost:8080',
      openApiPath: '/docs/opeapi.json'
    };
    const wrapper = mount(
      <DevUILayout {...props}>
        <MemoryRouter initialEntries={['/']} keyLength={0}>
          <DevUIRoutes navigate={'JobsManagement'} />
        </MemoryRouter>
      </DevUILayout>
    );
    expect(wrapper.find('PageLayout')).toMatchSnapshot();
  });
});
