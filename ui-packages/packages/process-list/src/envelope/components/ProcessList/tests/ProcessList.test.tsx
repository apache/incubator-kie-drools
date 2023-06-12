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
import { mount } from 'enzyme';
import React from 'react';
import TestProcessListDriver from './mocks/TestProcessListDriver';
import ProcessList from '../ProcessList';
import { processInstances } from './mocks/Mocks';
import wait from 'waait';
import { act } from 'react-dom/test-utils';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';

jest.mock('../../ProcessListTable/ProcessListTable');
jest.mock('../../ProcessListToolbar/ProcessListToolbar');

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    ServerErrors: () => {
      return <MockedComponent />;
    },
    KogitoEmptyState: () => {
      return <MockedComponent />;
    },
    LoadMore: () => {
      return <MockedComponent />;
    }
  })
);

let driverQueryMock;
let driverApplyFilterMock;
let driverApplySortingMock;

const getProcessListDriver = (items: number): TestProcessListDriver => {
  const driver = new TestProcessListDriver(
    processInstances.slice(0, items),
    []
  );
  jest.spyOn(driver, 'initialLoad');
  driverApplyFilterMock = jest.spyOn(driver, 'applyFilter');
  driverApplySortingMock = jest.spyOn(driver, 'applySorting');
  driverQueryMock = jest.spyOn(driver, 'query');
  props.driver = driver;
  return driver;
};
let props;

const getProcessListWrapper = () =>
  mount(<ProcessList {...props} />).find('ProcessList');
describe('ProcessList test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null
    };
  });

  it('Envelope not connected', async () => {
    const driver = getProcessListDriver(3);

    props.isEnvelopeConnectedToChannel = false;

    const wrapper = getProcessListWrapper();

    expect(wrapper).toMatchSnapshot();
    expect(driver.initialLoad).not.toHaveBeenCalled();
    expect(driver.query).not.toHaveBeenCalled();

    const toolbar = wrapper.find('MockedProcessListToolbar');
    expect(toolbar.exists()).toBeTruthy();

    const table = wrapper.find('MockedProcessListTable');
    expect(table.exists()).toBeTruthy();
    expect(table.props()['processInstances']).toHaveLength(0);
  });

  it('ProcessList initialLoad', async () => {
    const driver = getProcessListDriver(3);

    let wrapper;

    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();

    expect(driver.initialLoad).toHaveBeenCalled();
    expect(driver.query).toHaveBeenCalledWith(0, 10);

    const toolbar = wrapper.find('MockedProcessListToolbar');
    expect(toolbar.exists()).toBeTruthy();

    const table = wrapper.find('MockedProcessListTable');
    expect(table.exists()).toBeTruthy();
    expect(table.props().isLoading).toBeFalsy();
    expect(table.props().processInstances).toHaveLength(3);
    expect(Object.keys(table.props().sortBy)[0]).toBe('lastUpdate');
    expect(Object.values(table.props().sortBy)[0]).toBe('DESC');

    const loadMore = wrapper.find('LoadMore');
    expect(loadMore.exists()).toBeFalsy();
  });

  it('error page', async () => {
    const driver = getProcessListDriver(3);
    driverQueryMock.mockRejectedValue(
      JSON.parse('{"errorMessage":"404 error"}')
    );

    let wrapper;
    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();
    expect(driver.initialLoad).toHaveBeenCalled();
    const serverErrors = wrapper.find('ServerErrors');
    expect(serverErrors).toMatchSnapshot();
    expect(serverErrors.exists()).toBeTruthy();
  });

  it('apply filter', async () => {
    const driver = getProcessListDriver(3);
    let wrapper;
    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();
    expect(driver.initialLoad).toHaveBeenCalled();
    const toolbar = wrapper.find('MockedProcessListToolbar');
    const filters = [
      ProcessInstanceState.Active,
      ProcessInstanceState.Error,
      ProcessInstanceState.Completed
    ];
    await act(async () => {
      toolbar.props()['applyFilter'](filters);
    });
    expect(driverApplyFilterMock).toHaveBeenCalledWith(filters);
  });

  it('apply sorting', async () => {
    const driver = getProcessListDriver(3);
    let wrapper;
    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();
    expect(driver.initialLoad).toHaveBeenCalled();
    const processListTable = wrapper.find('MockedProcessListTable');
    await act(async () => {
      processListTable
        .props()
        ['onSort']({ target: { innerText: 'Status' } }, 1, 'asc');
      wait();
    });
    wrapper = wrapper.update();
    expect(driverApplySortingMock).toHaveBeenCalledWith({ state: 'ASC' });
  });

  it('do refresh', async () => {
    const driver = getProcessListDriver(3);
    let wrapper;
    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();
    expect(driver.initialLoad).toHaveBeenCalled();
    const toolbar = wrapper.find('MockedProcessListToolbar');
    await act(async () => {
      toolbar.props()['refresh']();
      wait();
    });
    wrapper = wrapper.update();
    expect(driverQueryMock).toHaveBeenCalledWith(0, 10);
  });
  it('load more', async () => {
    const driver = getProcessListDriver(10);
    let wrapper;
    await act(async () => {
      wrapper = getProcessListWrapper();
      wait();
    });

    wrapper = wrapper.update();
    expect(driver.initialLoad).toHaveBeenCalled();
    const loadMore = wrapper.find('LoadMore');
    expect(loadMore.exists()).toBeTruthy();
    await act(async () => {
      loadMore.props()['getMoreItems'](10, 13);
      wait();
    });
    expect(driverQueryMock).toHaveBeenCalledWith(10, 13);
  });
});
