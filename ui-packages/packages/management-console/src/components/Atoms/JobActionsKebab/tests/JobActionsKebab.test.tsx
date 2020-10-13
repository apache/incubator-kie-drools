import React from 'react';
import JobActionsKebab from '../JobActionsKebab';
import { GraphQL } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { Dropdown, KebabToggle, DropdownItem } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
// tslint:disable: no-string-literal
// tslint:disable: no-unexpected-multiline

jest.mock('../../../Atoms/JobsRescheduleModal/JobsRescheduleModal');
const MockedIcon = (): React.ReactElement => {
  return <></>;
};

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () => ({
  ...jest.requireActual('@patternfly/react-core'),
  ModalBoxBody: () => <MockedComponent />
}));

jest.mock('@patternfly/react-icons', () => ({
  ...jest.requireActual('@patternfly/react-icons'),
  InfoCircleIcon: () => {
    return <MockedIcon />;
  },
  TimesIcon: () => {
    return <MockedIcon />;
  }
}));

const props = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: GraphQL.JobStatus.Executed,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: '2020-08-27T03:35:50.147Z',
    expirationTime: '2020-08-27T03:35:50.147Z'
  }
};
const prop2 = {
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: GraphQL.JobStatus.Scheduled,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: '2020-08-27T03:35:50.147Z',
    expirationTime: '2020-08-27T03:35:50.147Z'
  }
};
describe('job actions kebab tests', () => {
  it('dropdown open/close tests', async () => {
    let wrapper = mount(<JobActionsKebab {...props} />);
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
        .at(0)
        .find('button')
        .children()
        .contains('Details')
    ).toBeTruthy();
    expect(wrapper.find(Dropdown).prop('isOpen')).toBeTruthy();
    await act(async () => {
      wrapper
        .find(DropdownItem)
        .at(0)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find(Dropdown).prop('isOpen')).toBeFalsy();
  });
  it('test reschedule option', async () => {
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
      wrapper
        .find(DropdownItem)
        .at(1)
        .find('button')
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
  });
  it('trigger/test apply reschedule method', async () => {
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
      wrapper
        .find('#reschedule-option')
        .at(0)
        .simulate('click');
    });
    wrapper = wrapper.update();
    expect(wrapper.find('JobsRescheduleModal').props()['isModalOpen']).toEqual(
      true
    );
  });
});
