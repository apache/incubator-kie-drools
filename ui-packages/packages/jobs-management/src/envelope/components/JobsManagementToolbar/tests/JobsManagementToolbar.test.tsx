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
import { act } from 'react-dom/test-utils';
import JobsManagementToolbar from '../JobsManagementToolbar';
import {
  Job,
  JobStatus
} from '@kogito-apps/management-console-shared/dist/types';
import { MockedJobsManagementDriver } from '../../../../api/mocks/MockedJobsManagementDriver';

describe('Jobs Management toolbar tests', () => {
  const Jobs: Job = {
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
    endpoint: 'http://localhost:4000/jobs',
    executionCounter: 0,
    expirationTime: new Date('2020-08-29T04:35:54.631Z'),
    id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
    lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
    priority: 0,
    processId: 'travels',
    processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    repeatInterval: null,
    repeatLimit: null,
    retries: 2,
    rootProcessId: '',
    scheduledId: null,
    status: JobStatus.Scheduled
  };

  const jobOperation: any = {
    CANCEL: {
      messages: {
        successMessage: 'Cancel Jobs',
        ignoredMessage:
          'These jobs were ignored because they were executed or canceled',
        noItemsMessage: 'No jobs were canceled'
      },
      functions: {
        perform: jest.fn()
      },
      results: {
        successItems: [],
        failedItems: [],
        ignoredItems: []
      }
    }
  };

  const props = {
    chips: [JobStatus.Scheduled],
    driver: new MockedJobsManagementDriver(),
    doQueryJobs: jest.fn(),
    jobOperations: jobOperation,
    onResetToDefault: jest.fn(),
    onRefresh: jest.fn(),
    selectedStatus: [JobStatus.Scheduled],
    selectedJobInstances: [Jobs],
    setSelectedJobInstances: jest.fn(),
    setSelectedStatus: jest.fn(),
    setChips: jest.fn(),
    setDisplayTable: jest.fn(),
    setIsLoading: jest.fn()
  };
  it('Snapshot test with default props', () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    expect(wrapper).toMatchSnapshot();
  });

  it('Test clearAllFilters', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    await act(async () => {
      wrapper
        .find('#data-toolbar-with-chip-groups')
        .first()
        .props()
        ['clearAllFilters']();
    });
    expect(props.chips).toEqual([JobStatus.Scheduled]);
  });

  it('Test Refresh button', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    await act(async () => {
      wrapper.find('#refresh-button').at(0).simulate('click');
    });
    expect(props.setSelectedJobInstances).toHaveBeenCalledWith([]);
  });

  it('Test apply filter button', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    await act(async () => {
      wrapper.find('#apply-filter').at(0).simulate('click');
    });
    expect(props.setChips).toHaveBeenCalled();
  });

  it('Test chips delete with more chips', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    const type = 'Status';
    const id = 'CANCELED';
    await act(async () => {
      wrapper
        .find('.kogito-jobs-management__state-dropdown-list')
        .at(0)
        .props()
        ['deleteChip'](type, id);
    });
    expect(props.setSelectedJobInstances).toHaveBeenCalledWith([]);
    expect(props.setChips).toHaveBeenCalled();
    expect(props.setSelectedStatus).toHaveBeenCalled();
  });

  it('Test chips delete with empty chips', async () => {
    const chips = [];
    const wrapper = mount(
      <JobsManagementToolbar {...{ ...props, chips }} />
    ).find('JobsManagementToolbar');
    const type = 'Status';
    const id = 'CANCELED';
    await act(async () => {
      wrapper
        .find('.kogito-jobs-management__state-dropdown-list')
        .at(0)
        .props()
        ['deleteChip'](type, id);
    });
    expect(props.setDisplayTable).toHaveBeenCalledWith(false);
  });

  it('Test filter dropdown for selection', async () => {
    let wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    const event: any = {
      target: {
        id: 'pf-random-id-2-ERROR'
      }
    };
    await act(async () => {
      wrapper.find('#status-select').at(0).props()['onToggle']();
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find('#status-select').at(0).props()['onSelect'](event);
    });
    wrapper = wrapper.update();
    expect(props.setSelectedStatus).toHaveBeenCalled();
  });

  it('Test filter dropdown for deselection', async () => {
    let wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    const event: any = {
      target: {
        id: 'pf-random-id-2-SCHEDULED'
      }
    };
    await act(async () => {
      wrapper.find('#status-select').at(0).props()['onToggle']();
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper.find('#status-select').at(0).props()['onSelect'](event);
    });
    wrapper = wrapper.update();
    expect(props.setSelectedStatus).toHaveBeenCalled();
  });

  it('Test selections on dropdown', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    const event: any = {
      target: {}
    };
    await act(async () => {
      wrapper.find('Dropdown').at(0).props()['onSelect'](event);
    });
  });

  it('Test toggles on dropdown', async () => {
    const wrapper = mount(<JobsManagementToolbar {...props} />).find(
      'JobsManagementToolbar'
    );
    await act(async () => {
      wrapper.find('KebabToggle').at(0).props()['onToggle']();
    });
  });
});
