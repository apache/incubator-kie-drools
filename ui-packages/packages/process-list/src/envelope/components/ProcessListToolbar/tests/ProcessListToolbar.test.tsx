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
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import ProcessListToolbar from '../ProcessListToolbar';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared/dist/types';
import { act } from 'react-dom/test-utils';
import { ProcessInstances } from '../../ProcessListTable/tests/mocks/Mocks';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
import axios from 'axios';
import _ from 'lodash';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const props = {
  filters: {
    status: [ProcessInstanceState.Active],
    businessKey: ['GTRR11']
  },
  setFilters: jest.fn(),
  applyFilter: jest.fn(),
  refresh: jest.fn(),
  processStates: [ProcessInstanceState.Active],
  setProcessStates: jest.fn(),
  selectedInstances: [ProcessInstances[0]],
  setSelectedInstances: jest.fn(),
  processInstances: ProcessInstances,
  setProcessInstances: jest.fn(),
  isAllChecked: false,
  setIsAllChecked: jest.fn(),
  driver: null,
  defaultStatusFilter: [ProcessInstanceState.Active],
  singularProcessLabel: 'Workflow',
  pluralProcessLabel: 'Workflows',
  isWorkflow: true,
  isTriggerCloudEventEnabled: true,
  isChecked: true
};
beforeEach(() => {
  props.setProcessStates.mockClear();
  props.setFilters.mockClear();
  props.isTriggerCloudEventEnabled = false;
});

describe('ProcessListToolbar test', () => {
  it('Snapshot tests', () => {
    const container = render(<ProcessListToolbar {...props} />);
    expect(container).toMatchSnapshot();
  });

  it('Snapshot tests with trigger cloud event', async () => {
    props.isTriggerCloudEventEnabled = true;
    const container = render(<ProcessListToolbar {...props} />);
    expect(container).toMatchSnapshot();
    const triggerButton = screen.getByTestId('trigger-cloud-event');
    expect(triggerButton).not.toBeNull();
  });

  it('on select status', async () => {
    render(<ProcessListToolbar {...props} />);
    await act(async () => {
      fireEvent.click(screen.getAllByText('Status')[0]);
    });
    await waitFor(() => screen.getAllByText('ACTIVE'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('ACTIVE')[0]);
    });
    await act(async () => {
      fireEvent.click(screen.getAllByText('COMPLETED')[0]);
    });
    expect(props.setProcessStates.mock.calls[1][0]).toStrictEqual([
      'ACTIVE',
      'COMPLETED'
    ]);
    await act(async () => {
      fireEvent.click(screen.getAllByText('ACTIVE')[0]);
    });
    expect(props.setProcessStates).toHaveBeenCalled();
  });

  it('delete a status chip', async () => {
    const container = render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(container.querySelector('.pf-c-chip button')!);
    });
    expect(props.applyFilter).toHaveBeenCalled();
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setProcessStates).toHaveBeenCalled();
    expect(props.setProcessStates.mock.calls[0][0]).toStrictEqual([]);
  });

  it('delete a status chip', async () => {
    const container = render(
      <ProcessListToolbar
        {...{
          ...props,
          filters: { ...props.filters, businessKey: ['GR1122', 'MTY11'] }
        }}
      />
    ).container;
    await act(async () => {
      fireEvent.click(container.querySelectorAll('.pf-c-chip button')[1]!);
    });
    expect(props.applyFilter).toHaveBeenCalled();
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[0][0]['businessKey'][0]).toEqual(
      'MTY11'
    );
  });

  it('reset filters', async () => {
    render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(screen.getAllByText('Status')[0]);
    });
    await waitFor(() => screen.getAllByText('ACTIVE'));
    await act(async () => {
      fireEvent.click(screen.getAllByText('ACTIVE')[0]);
    });
    await act(async () => {
      fireEvent.click(screen.getAllByText('COMPLETED')[0]);
    });
    await act(async () => {
      fireEvent.click(screen.getAllByText('Reset to default')[0]);
    });
    expect(props.setProcessStates.mock.calls[2][0]).toEqual(['ACTIVE']);
    expect(props.setFilters.mock.calls[0][0]).toEqual({
      status: ['ACTIVE'],
      businessKey: []
    });
  });

  it('apply filter click', async () => {
    render(<ProcessListToolbar {...props} />);
    await act(async () => {
      fireEvent.click(screen.getByTestId('apply-filter-button'));
    });
    expect(props.setFilters).toHaveBeenCalled();
    expect(props.setFilters.mock.calls[0][0]).toStrictEqual({
      status: ['ACTIVE'],
      businessKey: ['GTRR11']
    });
  });

  it('none selected click', async () => {
    const container1 = render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(container1.querySelector('#bulk-select div div button')!);
    });
    await waitFor(() => screen.findAllByText('Select none'));
    await act(async () => {
      fireEvent.click(container1.querySelector('#none a')!);
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });

  it('parent selected click', async () => {
    const container1 = render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(container1.querySelector('#bulk-select div div button')!);
    });
    await waitFor(() => screen.findAllByText('Select all parent processes'));
    await act(async () => {
      fireEvent.click(container1.querySelector('#all-parent a')!);
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });

  it('all selected click', async () => {
    const container1 = render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(container1.querySelector('#bulk-select div div button')!);
    });
    await waitFor(() => screen.findAllByText('Select all processes'));
    await act(async () => {
      fireEvent.click(container1.querySelector('#all-parent-child a')!);
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });

  it('bulk select checkbox check', async () => {
    const container1 = render(<ProcessListToolbar {...props} />).container;
    await act(async () => {
      fireEvent.click(container1.querySelector('#select-all-checkbox')!);
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
  it('bulk select checkbox uncheck', async () => {
    const container1 = render(
      <ProcessListToolbar {...{ ...props, isAllChecked: true }} />
    ).container;
    await act(async () => {
      fireEvent.click(container1.querySelector('#select-all-checkbox')!);
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });

  it('multi abort click', async () => {
    const abortProps = _.cloneDeep(props);
    abortProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      abortProps.driver,
      'handleProcessMultipleAction'
    );
    abortProps.selectedInstances = [ProcessInstances[0]];
    mockedAxios.delete.mockResolvedValue({});
    const container = render(<ProcessListToolbar {...abortProps} />).container;
    await act(async () => {
      fireEvent.click(
        container.querySelector(
          '#process-management-buttons div div div button'
        )!
      );
    });
    await waitFor(() => screen.findAllByText('Abort selected'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('multi-abort'));
    });
    expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
  });

  it('multi skip click', async () => {
    const skipProps = _.cloneDeep(props);
    skipProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      skipProps.driver,
      'handleProcessMultipleAction'
    );
    skipProps.selectedInstances = [ProcessInstances[0]];
    mockedAxios.post.mockResolvedValue({});
    const container = render(<ProcessListToolbar {...skipProps} />).container;
    await act(async () => {
      fireEvent.click(
        container.querySelector(
          '#process-management-buttons div div div button'
        )!
      );
    });
    await waitFor(() => screen.findAllByText('Skip selected'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('multi-skip'));
    });
    expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
  });

  it('multi retry click', async () => {
    const retryProps = _.cloneDeep(props);
    retryProps.driver = new TestProcessListDriver([], []);
    const driverhandleProcessMultipleActionMock = jest.spyOn(
      retryProps.driver,
      'handleProcessMultipleAction'
    );
    retryProps.selectedInstances = [ProcessInstances[0]];
    mockedAxios.post.mockResolvedValue({});
    const container = render(<ProcessListToolbar {...retryProps} />).container;
    await act(async () => {
      fireEvent.click(
        container.querySelector(
          '#process-management-buttons div div div button'
        )!
      );
    });
    await waitFor(() => screen.findAllByText('Retry selected'));
    await act(async () => {
      fireEvent.click(screen.getByTestId('multi-retry'));
    });
    expect(driverhandleProcessMultipleActionMock).toHaveBeenCalled();
  });
});
