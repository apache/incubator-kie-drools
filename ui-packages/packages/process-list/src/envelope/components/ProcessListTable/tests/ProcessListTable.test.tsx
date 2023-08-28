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
import { ProcessInstances } from './mocks/Mocks';
import ProcessListTable, { ProcessListTableProps } from '../ProcessListTable';
import _ from 'lodash';
import axios from 'axios';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
import { OrderBy } from '@kogito-apps/management-console-shared/dist/types';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const props: ProcessListTableProps = {
  processInstances: ProcessInstances,
  isLoading: false,
  expanded: {
    0: false
  },
  setExpanded: jest.fn(),
  driver: new TestProcessListDriver([], []),
  onSort: jest.fn(),
  sortBy: { lastUpdate: OrderBy.DESC },
  setProcessInstances: jest.fn(),
  selectedInstances: [],
  setSelectedInstances: jest.fn(),
  selectableInstances: 0,
  setSelectableInstances: jest.fn(),
  setIsAllChecked: jest.fn(),
  singularProcessLabel: 'Workflow',
  pluralProcessLabel: 'Workflows',
  isTriggerCloudEventEnabled: true
};
describe('ProcessListTable test', () => {
  beforeEach(() => {
    jest.spyOn(global.Math, 'random').mockReturnValue(0.123456789);
  });
  afterEach(() => {
    jest.spyOn(global.Math, 'random').mockRestore();
  });
  it('initial render with data', () => {
    const container = render(<ProcessListTable {...props} />);
    expect(container).toMatchSnapshot();
  });
  it('loading state', () => {
    const container = render(
      <ProcessListTable
        {...{ ...props, isLoading: true, processInstances: [] }}
      />
    );
    expect(container).toMatchSnapshot();
    expect(
      screen.getAllByText('Loading process instances...').length
    ).not.toEqual(0);
  });

  it('no results found state', () => {
    const container = render(
      <ProcessListTable {...{ ...props, processInstances: [] }} />
    );
    expect(container).toMatchSnapshot();
    expect(screen.getAllByText('No results found').length).not.toEqual(0);
  });

  it('expand parent process', async () => {
    const container = render(
      <ProcessListTable {...{ ...props, expanded: { 0: false } }} />
    );
    await waitFor(() => screen.findAllByText('travels'));
    await act(async () => {
      fireEvent.click(container.container.querySelector('#expand-toggle0')!);
    });
    await act(async () => {
      fireEvent.click(
        container.container.querySelector('.kogito-process-list__link')!
      );
    });
    expect(container).toMatchSnapshot();
    expect(
      container.container.querySelector('.pf-c-table__expandable-row-content')
    ).toBeTruthy();
  });

  it('snapshot test for process list - with expanded', async () => {
    const clonedProps = _.cloneDeep(props);
    clonedProps.expanded = {
      0: true,
      1: false
    };
    clonedProps.selectedInstances = [{ ...ProcessInstances[0] }];
    clonedProps.selectableInstances = 1;

    const container = render(<ProcessListTable {...clonedProps} />);
    await waitFor(() => screen.findAllByText('travels'));
    await act(async () => {
      fireEvent.click(container.container.querySelector('#expand-toggle0')!);
    });
    expect(container).toMatchSnapshot();
    expect(
      container.container.querySelector('.pf-c-table__expandable-row-content')
    ).toBeTruthy();
  });
  it('checkbox click tests - selected/unselected', async () => {
    const clonedProps = _.cloneDeep(props);
    render(<ProcessListTable {...clonedProps} />);
    await waitFor(() => screen.findAllByText('travels'));
    await act(async () => {
      fireEvent.click(
        screen.getAllByTestId(
          'checkbox-538f9feb-5a14-4096-b791-2055b38da7c6'
        )[0]
      );
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
    await act(async () => {
      fireEvent.click(
        screen.getAllByTestId(
          'checkbox-538f9feb-5a14-4096-b791-2055b38da7c6'
        )[0]
      );
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
  it('on skip success', async () => {
    const container = render(<ProcessListTable {...props} />);
    await waitFor(() => screen.findAllByText('travels'));
    mockedAxios.post.mockResolvedValue({});
    await act(async () => {
      fireEvent.click(screen.getAllByTestId('kebab-toggle')[0]);
    });
    await waitFor(() => screen.findAllByText('Skip'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('Skip')[0]);
    });
    expect(container).toMatchSnapshot();
  });

  it('on retry success', async () => {
    const container = render(<ProcessListTable {...props} />);
    await waitFor(() => screen.findAllByText('travels'));
    mockedAxios.post.mockResolvedValue({});
    await act(async () => {
      fireEvent.click(screen.getAllByTestId('kebab-toggle')[0]);
    });
    await waitFor(() => screen.findAllByText('Retry'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('Retry')[0]);
    });
    expect(container).toMatchSnapshot();
  });
  it('on Abort success', async () => {
    mockedAxios.delete.mockResolvedValue({});
    const container = render(<ProcessListTable {...props} />);
    await waitFor(() => screen.findAllByText('travels'));
    mockedAxios.post.mockResolvedValue({});
    await act(async () => {
      fireEvent.click(screen.getAllByTestId('kebab-toggle')[0]);
    });
    await waitFor(() => screen.findAllByText('Abort'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('Abort')[0]);
    });
    expect(container).toMatchSnapshot();
  });
});
