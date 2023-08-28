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
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import React from 'react';
import { act } from 'react-dom/test-utils';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
import { childProcessInstances } from './mocks/Mocks';
import ProcessListChildTable from '../ProcessListChildTable';
import { ProcessInstances } from '../../ProcessListTable/tests/mocks/Mocks';

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

describe('ProcessListChildTable test', () => {
  beforeEach(() => {
    jest.spyOn(global.Math, 'random').mockReturnValue(0.123456789);
  });
  afterEach(() => {
    jest.spyOn(global.Math, 'random').mockRestore();
  });
  it('render table', async () => {
    const driver = new TestProcessListDriver([], childProcessInstances);
    const driverGetChildQueryMock = jest.spyOn(
      driver,
      'getChildProcessesQuery'
    );
    const props = {
      driver,
      parentProcessId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processInstances: [
        ...ProcessInstances,
        { ...ProcessInstances[0], id: 'e4448857-fa0c-403b-ad69-f0a353458b9d' }
      ],
      setProcessInstances: jest.fn(),
      selectedInstances: [],
      setSelectedInstances: jest.fn(),
      setSelectableInstances: jest.fn(),
      onSkipClick: jest.fn(),
      onRetryClick: jest.fn(),
      onAbortClick: jest.fn(),
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows'
    };
    driverGetChildQueryMock.mockImplementation(() => {
      return Promise.resolve(props.processInstances);
    });
    let container;
    await act(async () => {
      container = render(<ProcessListChildTable {...props} />);
    });
    await waitFor(() => screen.getAllByText('travels'));
    expect(container).toMatchSnapshot();
    await act(async () => {
      fireEvent.click(
        container.container.querySelector('.kogito-process-list__link')
      );
    });
    expect(driverGetChildQueryMock).toHaveBeenCalledWith(props.parentProcessId);
  });

  it('error in query', async () => {
    const driver = new TestProcessListDriver([], childProcessInstances);
    const driverGetChildQueryMock = jest.spyOn(
      driver,
      'getChildProcessesQuery'
    );
    const props = {
      driver,
      parentProcessId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processInstances: [
        ...ProcessInstances,
        { ...ProcessInstances[0], id: 'e4448857-fa0c-403b-ad69-f0a353458b9d' }
      ],
      setProcessInstances: jest.fn(),
      selectedInstances: [],
      setSelectedInstances: jest.fn(),
      setSelectableInstances: jest.fn(),
      onSkipClick: jest.fn(),
      onRetryClick: jest.fn(),
      onAbortClick: jest.fn(),
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows'
    };
    driverGetChildQueryMock.mockImplementation(() => {
      throw new Error('404 error');
    });
    let container;
    await act(async () => {
      container = render(<ProcessListChildTable {...props} />);
    });
    await waitFor(() => screen.getAllByText('Error fetching data'));
    expect(container).toMatchSnapshot();
    expect(screen.getAllByText('Error fetching data').length).not.toEqual(0);
  });

  it('no results found', async () => {
    const driver = new TestProcessListDriver([], childProcessInstances);
    const driverGetChildQueryMock = jest.spyOn(
      driver,
      'getChildProcessesQuery'
    );
    const props = {
      driver,
      parentProcessId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processInstances: [
        ...ProcessInstances,
        {
          ...ProcessInstances[0],
          id: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
          childProcessInstances: []
        }
      ],
      setProcessInstances: jest.fn(),
      selectedInstances: [],
      setSelectedInstances: jest.fn(),
      setSelectableInstances: jest.fn(),
      onSkipClick: jest.fn(),
      onRetryClick: jest.fn(),
      onAbortClick: jest.fn(),
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows'
    };
    driverGetChildQueryMock.mockImplementation(() => {
      return Promise.resolve([]);
    });
    let container;
    await act(async () => {
      container = render(<ProcessListChildTable {...props} />);
    });
    await waitFor(() => screen.getAllByText('No child workflow instances'));
    expect(container).toMatchSnapshot();
    expect(
      screen.getAllByText('This workflow has no related sub workflows').length
    ).not.toEqual(0);
  });

  it('checkbox selected - true', async () => {
    const driver = new TestProcessListDriver([], childProcessInstances);
    const driverGetChildQueryMock = jest.spyOn(
      driver,
      'getChildProcessesQuery'
    );
    const props = {
      driver,
      parentProcessId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processInstances: [
        ...ProcessInstances,
        { ...ProcessInstances[0], id: 'e4448857-fa0c-403b-ad69-f0a353458b9d' }
      ],
      setProcessInstances: jest.fn(),
      selectedInstances: [],
      setSelectedInstances: jest.fn(),
      setSelectableInstances: jest.fn(),
      onSkipClick: jest.fn(),
      onRetryClick: jest.fn(),
      onAbortClick: jest.fn(),
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows'
    };
    driverGetChildQueryMock.mockImplementation(() => {
      return Promise.resolve(props.processInstances);
    });
    let container;
    await act(async () => {
      container = render(<ProcessListChildTable {...props} />);
    });
    await waitFor(() => screen.getAllByText('travels'));
    await act(async () => {
      fireEvent.click(
        container.getByTestId('checkbox-e4448857-fa0c-403b-ad69-f0a353458b9d')
      );
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });

  it('checkbox selected - false', async () => {
    const driver = new TestProcessListDriver([], childProcessInstances);
    const driverGetChildQueryMock = jest.spyOn(
      driver,
      'getChildProcessesQuery'
    );
    const props = {
      driver,
      parentProcessId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      processInstances: [
        ...ProcessInstances,
        { ...ProcessInstances[0], id: 'e4448857-fa0c-403b-ad69-f0a353458b9d' }
      ],
      setProcessInstances: jest.fn(),
      selectedInstances: [],
      setSelectedInstances: jest.fn(),
      setSelectableInstances: jest.fn(),
      onSkipClick: jest.fn(),
      onRetryClick: jest.fn(),
      onAbortClick: jest.fn(),
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows'
    };
    driverGetChildQueryMock.mockImplementation(() => {
      return Promise.resolve(props.processInstances);
    });
    let container;
    await act(async () => {
      container = render(<ProcessListChildTable {...props} />);
    });
    await waitFor(() => screen.getAllByText('travels'));
    await act(async () => {
      fireEvent.click(
        container.getByTestId('checkbox-e4448857-fa0c-403b-ad69-f0a353458b9d')
      );
    });
    await act(async () => {
      fireEvent.click(
        container.getByTestId('checkbox-e4448857-fa0c-403b-ad69-f0a353458b9d')
      );
    });
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
});
