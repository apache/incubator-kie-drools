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
import JobActionsKebab from '../JobActionsKebab';
import { mount } from 'enzyme';
import {
  Dropdown,
  KebabToggle,
  DropdownItem
} from '@patternfly/react-core/dist/js/components/Dropdown';
import { act } from 'react-dom/test-utils';
import { MockedProcessDetailsDriver } from '../../../../embedded/tests/mocks/Mocks';
import { JobStatus } from '@kogito-apps/management-console-shared';

const MockedIcon = (): React.ReactElement => {
  return <></>;
};

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

const MockedBulkList = (): React.ReactElement => {
  return <></>;
};

const MockedJobsRescheduleModal = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    ModalBoxBody: () => <MockedComponent />
  })
);

jest.mock('@kogito-apps/management-console-shared', () =>
  Object.assign(
    {},
    jest.requireActual('@kogito-apps/management-console-shared'),
    {
      BulkList: () => <MockedBulkList />,
      JobsRescheduleModal: () => <MockedJobsRescheduleModal />
    }
  )
);

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedIcon />;
    },
    TimesIcon: () => {
      return <MockedIcon />;
    }
  })
);

const props = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Canceled,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  driver: MockedProcessDetailsDriver()
};
const prop2 = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Scheduled,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z')
  },
  driver: MockedProcessDetailsDriver()
};
describe('job actions kebab tests', () => {
  it('dropdown open/close tests and details click', async () => {
    let wrapper = mount(<JobActionsKebab {...props} />);
    await act(async () => {
      wrapper.find(Dropdown).find(KebabToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper).toMatchSnapshot();
    expect(
      wrapper
        .find(DropdownItem)
        .at(0)
        .find('button')
        .children()
        .contains('Details')
    ).toBeTruthy();
    expect(wrapper.find(Dropdown).prop('isOpen')).toBeTruthy();
    await act(async () => {
      wrapper.find(DropdownItem).at(0).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find(Dropdown).prop('isOpen')).toBeFalsy();
  });
  it('test reschedule option', async () => {
    const modalTitle = 'success';
    const modalContent =
      'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is rescheduled successfully';
    (prop2.driver.rescheduleJob as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({ modalTitle, modalContent })
    );
    let wrapper = mount(<JobActionsKebab {...prop2} />);
    const repeatInterval = 0;
    const repeatLimit = 2;
    const scheduleDate = new Date('2020-08-27T03:35:50.147Z');
    await act(async () => {
      wrapper
        .find(Dropdown)
        .find(KebabToggle)
        .find('#kebab-toggle')
        .at(2)
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper
        .find(DropdownItem)
        .at(1)
        .find('button')
        .children()
        .contains('Reschedule')
    ).toBeTruthy();
    await act(async () => {
      wrapper.find(DropdownItem).at(1).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
    await act(async () => {
      wrapper
        .find('JobsRescheduleModal')
        .props()
        ['handleJobReschedule'](repeatInterval, repeatLimit, scheduleDate);
    });
    expect(prop2.driver.rescheduleJob).toHaveBeenCalledWith(
      prop2.job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  });
  it('trigger/test apply reschedule method', async () => {
    const modalTitle = 'failure';
    const modalContent = 'The job reschedule is failed';
    (prop2.driver.rescheduleJob as jest.Mock).mockImplementationOnce(() =>
      Promise.resolve({ modalTitle, modalContent })
    );
    const repeatInterval = 0;
    const repeatLimit = 2;
    const scheduleDate = new Date('2020-08-27T03:35:50.147Z');
    let wrapper = mount(<JobActionsKebab {...prop2} />);

    await act(async () => {
      wrapper
        .find(Dropdown)
        .find(KebabToggle)
        .find('#kebab-toggle')
        .at(2)
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(
      wrapper
        .find(DropdownItem)
        .at(1)
        .find('button')
        .children()
        .contains('Reschedule')
    ).toBeTruthy();
    await act(async () => {
      wrapper.find('#reschedule-option').at(0).simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
    await act(async () => {
      wrapper
        .find('JobsRescheduleModal')
        .props()
        ['handleJobReschedule'](repeatInterval, repeatLimit, scheduleDate);
    });
    expect(prop2.driver.rescheduleJob).toHaveBeenCalledWith(
      prop2.job,
      repeatInterval,
      repeatLimit,
      scheduleDate
    );
  });

  describe('trigger/test job cancel action', () => {
    it('cancel success', async () => {
      const modalTitle = 'success';
      const modalContent =
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is canceled successfully';
      (prop2.driver.cancelJob as jest.Mock).mockImplementationOnce(() =>
        Promise.resolve({ modalTitle, modalContent })
      );
      let wrapper = mount(<JobActionsKebab {...prop2} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(
        wrapper
          .find(DropdownItem)
          .at(2)
          .find('button')
          .children()
          .contains('Cancel')
      ).toBeTruthy();
      await act(async () => {
        wrapper.find(DropdownItem).at(2).find('button').simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('JobsCancelModal').props()['isModalOpen']).toEqual(
        true
      );
      expect(wrapper.find('JobsCancelModal').props()['modalContent']).toEqual(
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is canceled successfully'
      );
    });

    it('cancel failure', async () => {
      const modalTitle = 'failure';
      const modalContent =
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 failed. Message: 404 not found';
      (prop2.driver.cancelJob as jest.Mock).mockImplementationOnce(() =>
        Promise.resolve({ modalTitle, modalContent })
      );
      let wrapper = mount(<JobActionsKebab {...prop2} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
      });
      wrapper = wrapper.update();
      expect(
        wrapper
          .find(DropdownItem)
          .at(2)
          .find('button')
          .children()
          .contains('Cancel')
      ).toBeTruthy();
      await act(async () => {
        wrapper.find(DropdownItem).at(2).find('button').simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('JobsCancelModal').props()['isModalOpen']).toEqual(
        true
      );
      expect(wrapper.find('JobsCancelModal').props()['modalContent']).toEqual(
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 failed. Message: 404 not found'
      );
    });
  });
});
