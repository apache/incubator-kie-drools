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
import { render, screen } from '@testing-library/react';
import CustomDashboardViewPage from '../CustomDashboardViewPage';
import { BrowserRouter } from 'react-router-dom';

jest.mock(
  '../../../containers/CustomDashboardViewContainer/CustomDashboardViewContainer'
);

// Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useHistory: () => ({
    location: {
      state: {
        data: 'name'
      }
    }
  })
}));

describe('CustomDashboardViewPage tests', () => {
  it('Snapshot', () => {
    const { container } = render(
      <BrowserRouter>
        <CustomDashboardViewPage />
      </BrowserRouter>
    );

    expect(container).toMatchSnapshot();
  });
});
