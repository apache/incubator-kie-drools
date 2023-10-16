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
import { render, screen, fireEvent } from '@testing-library/react';
import { PageNotFound } from '../PageNotFound';
import * as H from 'history';
import { match } from 'react-router';
import { BrowserRouter as Router } from 'react-router-dom';

const path = '/xy';
const match: match = {
  isExact: false,
  path,
  url: path,
  params: {}
};
const location = H.createLocation('/processInstances');

const props1 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location,
  history: H.createMemoryHistory({ keyLength: 0 }),
  match
};

const props2 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {
    state: {
      prev: '/processInstances',
      description: 'some description',
      buttonText: 'button'
    },
    pathname: '',
    search: '',
    hash: ''
  },
  history: H.createMemoryHistory({ keyLength: 0 }),
  match
};

describe('PageNotFound component tests', () => {
  it('snapshot testing without location object', () => {
    const { container } = render(
      <Router>
        <PageNotFound {...props1} />
      </Router>
    );
    expect(container).toMatchSnapshot();
  });

  it('snapshot testing with location object', () => {
    const { container } = render(
      <Router>
        <PageNotFound {...props2} />
      </Router>
    );
    expect(container).toMatchSnapshot();
  });
  /* tslint:disable */
  it('redirect button click', () => {
    const { container } = render(
      <Router>
        <PageNotFound {...props2} />
      </Router>
    );
    const button = screen.getByTestId('redirect-button');
    fireEvent.click(button);
    expect(container).toMatchSnapshot();
  });
});
