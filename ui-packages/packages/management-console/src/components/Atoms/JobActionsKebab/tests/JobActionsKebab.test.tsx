import React from 'react';
import JobActionsKebab from '../JobActionsKebab';
import { GraphQL } from '@kogito-apps/common';
import { mount } from 'enzyme';
import { Dropdown, KebabToggle, DropdownItem } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';

jest.mock('../../JobsPanelDetailsModal/JobsPanelDetailsModal');

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
});
