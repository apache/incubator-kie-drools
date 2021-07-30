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
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import {
  DataTable,
  LoadMore,
  KogitoEmptyState,
  ServerErrors
} from '@kogito-apps/components-common';
import { mount } from 'enzyme';
import TestTaskInboxDriver from './mocks/TestTaskInboxDriver';
import { userTasks } from './mocks/MockData';
import TaskInbox, { TaskInboxProps } from '../TaskInbox';
import TaskInboxToolbar from '../../TaskInboxToolbar/TaskInboxToolbar';
import {
  getDefaultActiveTaskStates,
  getDefaultTaskStates
} from '../../utils/TaskInboxUtils';

jest.mock('../../TaskInboxToolbar/TaskInboxToolbar');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () => ({
  ...jest.requireActual('@kogito-apps/components-common'),
  DataTable: () => {
    return <MockedComponent />;
  },
  LoadMore: () => {
    return <MockedComponent />;
  },
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  KogitoSpinner: () => {
    return <MockedComponent />;
  },
  ServerErrors: () => {
    return <MockedComponent />;
  }
}));

let driverQueryMock;
let driverApplyFilterMock;
let driverApplySortingMock;

const getTaskInboxDriver = (items: number): TestTaskInboxDriver => {
  const driver = new TestTaskInboxDriver(userTasks.slice(0, items));
  jest.spyOn(driver, 'setInitialState');
  driverApplyFilterMock = jest.spyOn(driver, 'applyFilter');
  driverApplySortingMock = jest.spyOn(driver, 'applySorting');
  driverQueryMock = jest.spyOn(driver, 'query');
  jest.spyOn(driver, 'openTask');
  props.driver = driver;
  return driver;
};

let props: TaskInboxProps;

const getTaskInboxWrapper = () => {
  return mount(<TaskInbox {...props} />).find('TaskInbox');
};

describe('TaskInbox tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null,
      allTaskStates: getDefaultTaskStates(),
      activeTaskStates: getDefaultActiveTaskStates(),
      currentUser: 'John'
    };
  });

  it('Envelope not connected', async () => {
    const driver = getTaskInboxDriver(0);

    props.isEnvelopeConnectedToChannel = false;

    const wrapper = getTaskInboxWrapper();

    expect(wrapper).toMatchSnapshot();

    expect(driver.setInitialState).not.toHaveBeenCalled();
    expect(driver.query).not.toHaveBeenCalled();

    const toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();

    const table = wrapper.find(DataTable);
    expect(table.exists()).toBeTruthy();
    expect(table.props().isLoading).toBeTruthy();
    expect(table.props().data).toHaveLength(0);

    const loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeFalsy();
  });

  it('TaskInbox without LoadMore', async () => {
    const driver = getTaskInboxDriver(5);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    const toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();

    const table = wrapper.find(DataTable);
    expect(table.exists()).toBeTruthy();
    expect(table.props().isLoading).toBeFalsy();
    expect(table.props().data).toHaveLength(5);
    expect(table.props().sortBy.index).toBe(5);
    expect(table.props().sortBy.direction).toBe('desc');

    const loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeFalsy();
  });

  it('TaskInbox with LoadMore', async () => {
    const driver = getTaskInboxDriver(15);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    const toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();

    let dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().isLoading).toBeFalsy();
    expect(dataTable.props().data).toHaveLength(10);

    let loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeTruthy();

    await act(async () => {
      loadMore.props().getMoreItems(10, 10);
      wait();
    });

    wrapper = wrapper.update().find(TaskInbox);

    expect(driver.query).toHaveBeenCalledWith(10, 10);

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().isLoading).toBeFalsy();
    expect(dataTable.props().data).toHaveLength(15);

    loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeFalsy();
  });

  it('TaskInbox with initialState', async () => {
    const driver = getTaskInboxDriver(15);

    props.initialState = {
      filters: {
        taskNames: ['App'],
        taskStates: ['Ready']
      },
      currentPage: {
        offset: 10,
        limit: 2
      },
      sortBy: {
        property: 'lastUpdate',
        direction: 'desc'
      }
    };

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 20);

    const toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();
    expect(toolbar.props().activeFilter.taskStates).toHaveLength(2);
    expect(toolbar.props().activeFilter.taskStates).toEqual([
      'Ready',
      'Reserved'
    ]);
    expect(toolbar.props().activeFilter.taskNames).toHaveLength(0);
    expect(toolbar.props().activeFilter.taskNames).toEqual([]);

    const dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().isLoading).toBeFalsy();
    expect(dataTable.props().data).toHaveLength(10);

    const loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeFalsy();
  });

  it('TaskInbox with error', async () => {
    const driver = getTaskInboxDriver(15);

    driverQueryMock.mockImplementation(args => {
      throw new Error('error');
    });
    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(wrapper).toMatchSnapshot();

    expect(driver.query).toHaveBeenCalled();

    const serverError = wrapper.find(ServerErrors);

    expect(serverError.exists()).toBeTruthy();

    const dataTable = wrapper.find(DataTable);

    expect(dataTable.exists()).toBeFalsy();
  });

  it('TaskInbox with sorting', async () => {
    const driver = getTaskInboxDriver(10);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    // sort by state
    await act(async () => {
      wrapper
        .find(DataTable)
        .props()
        ['onSorting'](3, 'asc');

      wait();
    });

    expect(driver.applySorting).toHaveBeenCalled();

    const sort = driverApplySortingMock.mock.calls[0][0];
    expect(sort).toHaveProperty('property', 'state');
    expect(sort).toHaveProperty('direction', 'asc');

    expect(driver.query).toHaveBeenLastCalledWith(0, 10);
  });

  it('TaskInbox with LoadMore and sorting', async () => {
    const driver = getTaskInboxDriver(15);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    let dataTable = wrapper.find(DataTable);
    expect(dataTable.props().data).toHaveLength(10);

    const loadMore = wrapper.find(LoadMore);
    expect(loadMore.exists()).toBeTruthy();

    await act(async () => {
      loadMore.props().getMoreItems(10, 10);
      wait();
    });

    wrapper = wrapper.update().find(TaskInbox);

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(15);

    // sort by state
    await act(async () => {
      dataTable.props().onSorting(3, 'asc');
      wait();
    });

    wrapper = wrapper.update().find(TaskInbox);

    expect(driver.applySorting).toHaveBeenCalled();

    const sort = driverApplySortingMock.mock.calls[0][0];
    expect(sort).toHaveProperty('property', 'state');
    expect(sort).toHaveProperty('direction', 'asc');

    expect(driver.query).toHaveBeenLastCalledWith(0, 10);

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().data).toHaveLength(10);
  });

  it('TaskInbox apply filter', async () => {
    const driver = getTaskInboxDriver(15);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    const toolbar = wrapper.find(TaskInboxToolbar);

    expect(toolbar.exists()).toBeTruthy();

    await act(async () => {
      toolbar.props().applyFilter({
        taskNames: ['App', 'Conf'],
        taskStates: ['Completed']
      });
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.applyFilter).toHaveBeenCalled();

    const filter = driverApplyFilterMock.mock.calls[0][0];
    expect(filter).toHaveProperty('taskNames', ['App', 'Conf']);
    expect(filter).toHaveProperty('taskStates', ['Completed']);

    expect(driver.query).toHaveBeenCalledTimes(3);

    const dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
  });

  it('TaskInbox apply empty filter', async () => {
    const driver = getTaskInboxDriver(15);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    const toolbar = wrapper.find(TaskInboxToolbar);

    expect(toolbar.exists()).toBeTruthy();

    await act(async () => {
      toolbar.props().applyFilter({
        taskNames: [],
        taskStates: []
      });
      wait();
    });

    wrapper = wrapper.update();

    let emptyState = wrapper.find(KogitoEmptyState);

    expect(emptyState.exists()).toBeTruthy();

    expect(driver.applyFilter).not.toHaveBeenCalled();

    let dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeFalsy();

    await act(async () => {
      emptyState.props().onClick();
      wait();
    });

    wrapper = wrapper.update();

    emptyState = wrapper.find(KogitoEmptyState);
    expect(emptyState.exists()).toBeFalsy();

    expect(driver.applyFilter).toHaveBeenCalled();

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
  });

  it('TaskInbox refresh', async () => {
    const driver = getTaskInboxDriver(15);

    let wrapper;

    await act(async () => {
      wrapper = getTaskInboxWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.setInitialState).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    let toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();

    let dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().isLoading).toBeFalsy();
    expect(dataTable.props().data).toHaveLength(10);

    await act(async () => {
      toolbar.props().refresh();
      wait();
    });

    wrapper = wrapper.update();

    toolbar = wrapper.find(TaskInboxToolbar);
    expect(toolbar.exists()).toBeTruthy();

    dataTable = wrapper.find(DataTable);
    expect(dataTable.exists()).toBeTruthy();
    expect(dataTable.props().isLoading).toBeFalsy();
    expect(dataTable.props().data).toHaveLength(10);

    expect(driverQueryMock).toHaveBeenLastCalledWith(0, 10);
  });
});
