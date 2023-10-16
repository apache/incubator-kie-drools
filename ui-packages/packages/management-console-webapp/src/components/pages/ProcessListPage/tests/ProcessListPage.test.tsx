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
import { mount } from 'enzyme';
import ProcessListPage from '../ProcessListPage';
import { BrowserRouter } from 'react-router-dom';
import * as H from 'history';

jest.mock('../../../containers/ProcessListContainer/ProcessListContainer');

describe('ProcessListPage tests', () => {
  const props = {
    match: {
      params: {
        instanceID: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
      },
      url: '',
      isExact: false,
      path: ''
    },
    location: H.createLocation(''),
    history: H.createBrowserHistory()
  };
  it('Snapshot', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ProcessListPage {...props} />
      </BrowserRouter>
    ).find('ProcessListPage');

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('MockedProcessListContainer').exists()).toBeTruthy();
  });
});
