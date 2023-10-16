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
import * as React from 'react';
import { mount } from 'enzyme';
import InputData from '../InputData';
import useInputData from '../useInputData';
import { MemoryRouter } from 'react-router';
import { ItemObject, RemoteDataStatus } from '../../../../types';

jest.mock('../useInputData');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    executionId: 'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
  }),
  useRouteMatch: () => ({
    path: '/audit/decision/b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000',
    url: '/audit/:executionType/:executionId/input-data'
  })
}));

describe('InputData', () => {
  test('renders inputs of an execution', () => {
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        inputs: [
          {
            name: 'Asset Score',
            type: 'number',
            value: 738,
            components: []
          }
        ] as ItemObject[]
      }
    };

    (useInputData as jest.Mock).mockReturnValue(inputData);

    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname:
              '/audit/decision/b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000/input-data',
            key: 'input-data'
          }
        ]}
      >
        {' '}
        <InputData />
      </MemoryRouter>
    );

    expect(useInputData).toHaveBeenCalledWith(
      'b2b0ed8d-c1e2-46b5-3ac54ff4beae-1000'
    );
    expect(wrapper.find('InputDataBrowser')).toHaveLength(1);
    expect(wrapper.find('InputDataBrowser').prop('inputData')).toStrictEqual(
      inputData
    );
  });
});
