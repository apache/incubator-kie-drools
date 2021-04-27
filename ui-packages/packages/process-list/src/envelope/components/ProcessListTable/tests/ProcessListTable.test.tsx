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
import { ProcessInstances } from '../mocks/Mocks';
import { OrderBy } from '../../.././../api';
import { getWrapper } from '@kogito-apps/components-common';
import ProcessListTable from '../ProcessListTable';
import { Button } from '@patternfly/react-core';

jest.mock('../../ProcessListChildTable/ProcessListChildTable');
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common', () => ({
  ...jest.requireActual('@kogito-apps/components-common'),
  LoadMore: () => {
    return <MockedComponent />;
  },
  KogitoEmptyState: () => {
    return <MockedComponent />;
  },
  KogitoSpinner: () => {
    return <MockedComponent />;
  }
}));
const props = {
  processInstances: ProcessInstances,
  isLoading: false,
  expanded: {
    0: false
  },
  setExpanded: jest.fn(),
  driver: null,
  onSort: jest.fn(),
  sortBy: { lastUpdate: OrderBy.DESC }
};
describe('ProcessListTable test', () => {
  it('initial render with data', () => {
    const wrapper = getWrapper(
      <ProcessListTable {...props} />,
      'ProcessListTable'
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('loading state', () => {
    const wrapper = getWrapper(
      <ProcessListTable {...{ ...props, isLoading: true }} />,
      'ProcessListTable'
    );
    const kogitoSpinner = wrapper.find('KogitoSpinner');
    expect(kogitoSpinner.exists()).toBeTruthy();
    expect(kogitoSpinner).toMatchSnapshot();
  });

  it('no results found state', () => {
    const wrapper = getWrapper(
      <ProcessListTable {...{ ...props, processInstances: [] }} />,
      'ProcessListTable'
    );
    const kogitoEmptyState = wrapper.find('KogitoEmptyState');
    expect(kogitoEmptyState.exists()).toBeTruthy();
    expect(kogitoEmptyState).toMatchSnapshot();
  });
  it('expand parent process', async () => {
    let wrapper = getWrapper(
      <ProcessListTable {...{ ...props, expanded: { 0: true } }} />,
      'ProcessListTable'
    );
    await act(async () => {
      wrapper
        .find('td')
        .at(0)
        .find(Button)
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find('MockedProcessListChildTable').exists()).toBeTruthy();
  });
});
