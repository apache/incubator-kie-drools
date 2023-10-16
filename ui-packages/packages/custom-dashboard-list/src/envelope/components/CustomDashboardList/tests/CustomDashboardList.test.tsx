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
import CustomDashboardList from '../CustomDashboardList';
import { MockedCustomDashboardListDriver } from '../../../tests/mocks/MockedCustomDashboardsListDriver';
import { act } from 'react-dom/test-utils';
import wait from 'waait';
import TestCustomDashboardListDriver from '../__mocks__/TestCustomDashboardListDriver';
import { dashboardList } from '../__mocks__/MockData';

describe('customDashboard list tests', () => {
  jest.mock('../../CustomDashboardsGallery/CustomDashboardsGallery');
  jest.mock('../../CustomDashboardListToolbar/CustomDashboardListToolbar');
  jest.mock('../../CustomDashboardsTable/CustomDashboardsTable');
  Date.now = jest.fn(() => 1487076708000);
  const driver = new MockedCustomDashboardListDriver();

  it('envelope not connected to channel', async () => {
    const props = {
      isEnvelopeConnectedToChannel: false,
      driver: null
    };

    await act(async () => {
      render(<CustomDashboardList {...props} />);
    });

    expect(screen.getByText('Loading Dashboard...')).toBeTruthy();
  });

  it('render customDashboard list - table', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver
    };
    let container;
    await act(async () => {
      container = render(<CustomDashboardList {...props} />).container;
    });
    expect(container).toMatchSnapshot();
  });

  /* Re-enable card view after thumbnails are available */
  /*it('render CustomDashboard list - gallery', async () => {
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: driver
    };
    let wrapper;
    // switches to gallery view
    await act(async () => {
      wrapper = mount(<CustomDashboardList {...props} />);
      wrapper.find(ToggleGroupItem).at(1).find('button').simulate('click');
    });
    await wait(0);
    await act(async () => {
      wrapper = wrapper.update();
    });
    expect(wrapper).toMatchSnapshot();

    // switches to table view
    await act(async () => {
      wrapper.find(ToggleGroupItem).at(0).find('button').simulate('click');
    });
    await wait(0);
    await act(async () => {
      wrapper = wrapper.update();
    });
    expect(wrapper.find('CustomDashboardList').exists()).toBeTruthy();
  });*/
});

let applyFilterMock;
let getCustomDashboardsQueryMock;
let props;
const getCustomDashboardListDriver = (
  items: number
): TestCustomDashboardListDriver => {
  const driver = new TestCustomDashboardListDriver(
    dashboardList.slice(0, items)
  );
  applyFilterMock = jest.spyOn(driver, 'applyFilter');
  getCustomDashboardsQueryMock = jest.spyOn(driver, 'getCustomDashboardsQuery');
  props.driver = driver;
  return driver;
};

describe('customDashboard list action tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    props = {
      isEnvelopeConnectedToChannel: true,
      driver: null
    };
  });

  it('CustomDashboard list - error page', async () => {
    getCustomDashboardListDriver(2);

    getCustomDashboardsQueryMock.mockImplementation(() => {
      throw new Error('404 error');
    });
    let wrapper;
    await act(async () => {
      const { container } = render(<CustomDashboardList {...props} />);
      wait();
      wrapper = container;
    });
    const error = wrapper.querySelector('h1')?.textContent;
    expect(error).toBe('Error fetching data');
  });

  it('CustomDashboard list - applyFilter and handleItemClick', async () => {
    getCustomDashboardListDriver(3);

    const { container } = render(<CustomDashboardList {...props} />);

    const toolbar = container.getElementsByClassName('pf-m-filter-group');

    expect(toolbar.length).toBe(1);

    /* Re-enable card view after thumbnails are available */
    /*const views = wrapper.find('ToggleGroupItem');
    expect(views.length).toBe(2);

    await act(() => {
      views.get(0).props['onChange'];
    });

    expect(views.get(0).props['isSelected']).toEqual(true);
    expect(views.get(1).props['isSelected']).toEqual(false);*/
  });
});
