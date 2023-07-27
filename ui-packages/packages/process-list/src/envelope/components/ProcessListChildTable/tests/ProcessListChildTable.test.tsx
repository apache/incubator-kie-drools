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
import { act } from 'react-dom/test-utils';
import TestProcessListDriver from '../../ProcessList/tests/mocks/TestProcessListDriver';
import { childProcessInstances } from './mocks/Mocks';
import ProcessListChildTable from '../ProcessListChildTable';
import { ProcessInstances } from '../../ProcessListTable/tests/mocks/Mocks';
import { Checkbox } from '@patternfly/react-core/dist/js/components/Checkbox';
Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/components-common/dist/components/ServerErrors', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    ServerErrors: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock(
  '@kogito-apps/components-common/dist/components/KogitoEmptyState',
  () =>
    Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
      KogitoEmptyState: () => {
        return <MockedComponent />;
      }
    })
);

jest.mock('@kogito-apps/components-common/dist/components/KogitoSpinner', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    KogitoSpinner: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@kogito-apps/components-common/dist/components/ItemDescriptor', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    ItemDescriptor: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock('@kogito-apps/components-common/dist/components/EndpointLink', () =>
  Object.assign({}, jest.requireActual('@kogito-apps/components-common'), {
    EndpointLink: () => {
      return <MockedComponent />;
    }
  })
);

jest.mock(
  '@kogito-apps/management-console-shared/dist/components/ProcessInfoModal',
  () =>
    Object.assign(
      {},
      jest.requireActual('@kogito-apps/management-console-shared'),
      {
        ProcessInfoModal: () => {
          return <MockedComponent />;
        }
      }
    )
);
describe('ProcessListChildTable test', () => {
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessListChildTable {...props} />).find(
        'ProcessListChildTable'
      );
    });
    wrapper = wrapper.update();
    expect(wrapper).toMatchSnapshot();
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessListChildTable {...props} />).find(
        'ProcessListChildTable'
      );
    });
    wrapper = wrapper.update();
    const serverError = wrapper.find('ServerErrors');
    expect(serverError).toMatchSnapshot();
    expect(serverError.exists()).toBeTruthy();
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessListChildTable {...props} />).find(
        'ProcessListChildTable'
      );
    });
    wrapper = wrapper.update();
    expect(driverGetChildQueryMock).toHaveBeenCalledWith(props.parentProcessId);
    const EmptyState = wrapper.find('KogitoEmptyState');
    expect(EmptyState.exists()).toBeTruthy();
    expect(EmptyState).toMatchSnapshot();
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessListChildTable {...props} />).find(
        'ProcessListChildTable'
      );
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper
        .find(Checkbox)
        .at(0)
        .find('input')
        .simulate('change', { target: { checked: true } });
    });
    wrapper = wrapper.update();
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
    let wrapper;
    await act(async () => {
      wrapper = mount(<ProcessListChildTable {...props} />).find(
        'ProcessListChildTable'
      );
    });
    wrapper = wrapper.update();
    await act(async () => {
      wrapper
        .find(Checkbox)
        .at(0)
        .find('input')
        .simulate('change', { target: { checked: false } });
    });
    wrapper = wrapper.update();
    expect(props.setSelectedInstances).toHaveBeenCalled();
  });
});
