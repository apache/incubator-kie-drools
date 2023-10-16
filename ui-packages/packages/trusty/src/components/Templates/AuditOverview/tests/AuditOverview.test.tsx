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
import { mount, shallow } from 'enzyme';
import { MemoryRouter } from 'react-router-dom';
import AuditOverview from '../AuditOverview';
import useExecutions from '../useExecutions';
import { RemoteDataStatus } from '../../../../types';
import { TrustyContext } from '../../TrustyApp/TrustyApp';

jest.mock('../useExecutions');

describe('Audit overview', () => {
  test('renders correctly a list of executions', () => {
    const mockLoadExecutions = jest.fn();
    const executions = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        total: 1,
        limit: 10,
        offset: 0,
        headers: [
          {
            executionId: 'b2b0ed8d-c1e2-46b5-ad4f-3ac54ff4beae',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          }
        ]
      }
    };
    (useExecutions as jest.Mock).mockReturnValue({
      mockLoadExecutions,
      executions
    });
    const wrapper = shallow(
      <AuditOverview
        dateRangePreset={{ fromDate: '2020-01-01', toDate: '2020-02-01' }}
      />
    );
    expect(wrapper).toMatchSnapshot();
  });

  test('loads a list of executions from the last month by default', () => {
    const mockLoadExecutions = jest.fn();
    const executions = {
      status: RemoteDataStatus.SUCCESS,
      data: {
        total: 1,
        limit: 10,
        offset: 0,
        headers: [
          {
            executionId: 'b2b0ed8d-c1e2-46b5-ad4f-3ac54ff4beae',
            executionDate: '2020-06-01T12:33:57+0000',
            executionSucceeded: true,
            executorName: 'testUser',
            executedModelName: 'LoanEligibility',
            executionType: 'DECISION'
          }
        ]
      }
    };
    (useExecutions as jest.Mock).mockReturnValue({
      mockLoadExecutions,
      executions
    });
    const fixedDate = '2020-05-01T00:00:00.000Z';

    jest
      .spyOn(global.Date, 'now')
      .mockImplementation(() => new Date(fixedDate).getTime());

    const wrapper = mount(
      <TrustyContext.Provider
        value={{
          config: {
            counterfactualEnabled: true,
            explanationEnabled: true,
            basePath: '',
            useHrefLinks: true
          }
        }}
      >
        <MemoryRouter>
          <AuditOverview />
        </MemoryRouter>
      </TrustyContext.Provider>
    );

    expect(useExecutions).toHaveBeenCalledWith({
      searchString: '',
      from: '2020-04-01',
      to: '2020-05-01',
      limit: 10,
      offset: 0
    });
    expect(wrapper.find('ExecutionTable').props().data).toStrictEqual(
      executions
    );
  });
});
