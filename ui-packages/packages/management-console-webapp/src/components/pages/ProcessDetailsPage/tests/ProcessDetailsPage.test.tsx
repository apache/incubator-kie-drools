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
import * as H from 'history';
import { getWrapper } from '@kogito-apps/components-common';
import ProcessDetailsPage from '../ProcessDetailsPage';
import { BrowserRouter } from 'react-router-dom';

describe('WebApp - ProcessDetailsPage tests', () => {
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
  it('Snapshot test with default values', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ProcessDetailsPage {...props} />
      </BrowserRouter>,
      'ProcessDetailsPage'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
