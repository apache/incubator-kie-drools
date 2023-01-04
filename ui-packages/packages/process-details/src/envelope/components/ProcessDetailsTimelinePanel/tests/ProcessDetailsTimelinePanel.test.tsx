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
import { shallow, mount } from 'enzyme';
import ProcessDetailsTimelinePanel from '../ProcessDetailsTimelinePanel';
import {
  JobStatus,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import { act } from 'react-dom/test-utils';
import { Dropdown, DropdownItem, KebabToggle } from '@patternfly/react-core';
import wait from 'waait';
import * as Utils from '../../../../utils/Utils';
import TestProcessDetailsDriver from '../../../tests/mocks/TestProcessDetailsDriver';

const MockedIcon = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    UserIcon: () => {
      return <MockedIcon />;
    },
    CheckCircleIcon: () => {
      return <MockedIcon />;
    },
    ErrorCircleOIcon: () => {
      return <MockedIcon />;
    },
    OnRunningIcon: () => {
      return <MockedIcon />;
    },
    OutlinedClockIcon: () => {
      return <MockedIcon />;
    }
  })
);

const driver = new TestProcessDetailsDriver(
  '2d962eef-45b8-48a9-ad4e-9cde0ad6af89'
);

const props1 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    serviceUrl: 'http://localhost:4000',
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '111-555-898',
        name: 'Confirm travel',
        definitionId: 'abc-efg-hij',
        id: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9r25e',
        enter: new Date('2019-10-22T03:40:44.089Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'Join'
      },
      {
        nodeId: '111-555-898',
        name: 'Confirm travel',
        definitionId: 'abc-efg-hij',
        id: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9',
        enter: new Date('2019-10-22T03:40:44.089Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'HumanTaskNode'
      },
      {
        name: 'End Event 1',
        definitionId: '_7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: new Date('2019-10-22T04:43:01.144Z'),
        type: 'StartNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000',
      nodeInstanceId: '69e0a0f5-2360-4174-a8f8-a892a31fc2f9'
    },
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Scheduled,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000',
      nodeInstanceId: '2f588da5-a323-4111-9017-3093ef9319d1'
    }
  ],
  driver,
  omittedProcessTimelineEvents: []
};

const props2 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    rootProcessInstanceId: null,
    serviceUrl: 'http://localhost:4000',
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000'
    }
  ],
  driver,
  omittedProcessTimelineEvents: []
};

const props3 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Completed,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [
    {
      id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      processId: 'travels',
      processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
      rootProcessId: null,
      status: JobStatus.Executed,
      priority: 0,
      callbackEndpoint:
        'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
      repeatInterval: null,
      repeatLimit: null,
      scheduledId: '0',
      retries: 0,
      lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
      expirationTime: null,
      endpoint: 'http://localhost:4000'
    }
  ],
  driver,
  omittedProcessTimelineEvents: ['StartProcess']
};

const props4 = {
  data: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
    processId: 'travels',
    parentProcessInstanceId: null,
    parentProcessInstance: null,
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Completed,
    rootProcessInstanceId: null,
    serviceUrl: null,
    endpoint: 'http://localhost:4000',
    addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
    error: {
      nodeDefinitionId: 'abc-efg-hij',
      message: 'Something went wrong'
    },
    start: new Date('2019-10-22T03:40:44.089Z'),
    end: null,
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        name: 'End Event 1',
        definitionId: 'abc-efg-hij',
        id: '7244ba1b-75ec-4789-8c65-499a0c5b1a6f',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      },
      {
        name: 'Book flight',
        definitionId: 'ServiceTask_1',
        id: '2f588da5-a323-4111-9017-3093ef9319d1',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'WorkItemNode'
      },
      {
        name: 'StartProcess',
        definitionId: 'StartEvent_1',
        id: '6ed7aa17-4bb1-48e3-b34a-5a4c5773dff2',
        nodeId: '123-456-789',
        enter: new Date('2019-10-22T04:43:01.144Z'),
        exit: null,
        type: 'HumanTaskNode'
      }
    ],
    childProcessInstances: []
  },
  jobs: [],
  driver,
  omittedProcessTimelineEvents: []
};

describe('ProcessDetailsTimelinePanel component tests', () => {
  it('Snapshot testing for service url available', () => {
    const wrapper = shallow(<ProcessDetailsTimelinePanel {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing for no service url', () => {
    const wrapper = shallow(<ProcessDetailsTimelinePanel {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('Snapshot testing for completed state', () => {
    const wrapper = shallow(<ProcessDetailsTimelinePanel {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('onKebabToggle click test', async () => {
    let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
    await act(async () => {
      wrapper
        .find(Dropdown)
        .at(0)
        .find(KebabToggle)
        .find('button')
        .simulate('click');
      await wait(0);
      wrapper = wrapper.update();
    });
    expect(wrapper.find(Dropdown).at(0).props()['isOpen']).toBeTruthy();
    await act(async () => {
      wrapper
        .find(Dropdown)
        .at(0)
        .find(KebabToggle)
        .find('button')
        .simulate('click');
      await wait(0);
      wrapper = wrapper.update();
    });
    expect(wrapper.find(Dropdown).at(0).props()['isOpen']).toBeFalsy();
  });

  describe('handleSkip tests', () => {
    const handleSkipSpy = jest.spyOn(Utils, 'handleSkip');
    it('success test', async () => {
      const mockDriverHandleSkipSuccess = jest.spyOn(
        driver,
        'handleProcessSkip'
      );

      mockDriverHandleSkipSuccess.mockResolvedValue();
      // let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find('Dropdown')
          .at(0)
          .find('KebabToggle')
          .find('button')
          .simulate('click');
      });
      // await wait(0);
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('DropdownItem').at(1).simulate('click');
      });
      // await wait(0);
      wrapper = wrapper.update();
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Confirm travel was successfully skipped.'
      );
      expect(handleSkipSpy).toHaveBeenCalled();
    });
    it('failure', async () => {
      // mockedAxios.post.mockRejectedValue({ message: '403 error' });
      const mockDriverHandleSkipFalied = jest.spyOn(
        driver,
        'handleProcessSkip'
      );
      mockDriverHandleSkipFalied.mockRejectedValue(new Error('403 error'));
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(0)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(1).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Confirm travel failed to skip. Message: "403 error"'
      );
      expect(handleSkipSpy).toHaveBeenCalled();
    });
  });

  describe('nodeInstanceRetrigger tests', () => {
    const handleNodeInstanceRetriggerSpy = jest.spyOn(
      Utils,
      'handleNodeInstanceRetrigger'
    );
    it('success test', async () => {
      const handleNodeInstanceRetriggerSuccess = jest.spyOn(
        driver,
        'handleNodeInstanceRetrigger'
      );
      handleNodeInstanceRetriggerSuccess.mockResolvedValue();
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();

        wrapper.find(DropdownItem).at(0).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Book flight was successfully retriggered.'
      );
      expect(handleNodeInstanceRetriggerSpy).toHaveBeenCalled();
    });
    it('failure', async () => {
      const handleNodeInstanceRetriggerFailed = jest.spyOn(
        driver,
        'handleNodeInstanceRetrigger'
      );
      handleNodeInstanceRetriggerFailed.mockRejectedValue(
        new Error('403 error')
      );
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(0).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Book flight failed to retrigger. Message: "403 error"'
      );
      expect(handleNodeInstanceRetriggerSpy).toHaveBeenCalled();
    });
  });

  describe('nodeInstanceCancel tests', () => {
    const handleNodeInstanceCancelSpy = jest.spyOn(
      Utils,
      'handleNodeInstanceCancel'
    );
    it('success test', async () => {
      const handleNodeInstanceCancelSuccess = jest.spyOn(
        driver,
        'handleNodeInstanceCancel'
      );
      handleNodeInstanceCancelSuccess.mockResolvedValue();
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(1).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Book flight was successfully canceled.'
      );
      expect(handleNodeInstanceCancelSpy).toHaveBeenCalled();
    });
    it('failure', async () => {
      const handleNodeInstanceCancelFailed = jest.spyOn(
        driver,
        'handleNodeInstanceCancel'
      );
      handleNodeInstanceCancelFailed.mockRejectedValue(new Error('403 error'));
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(1).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Book flight failed to cancel. Message: "403 error"'
      );
      expect(handleNodeInstanceCancelSpy).toHaveBeenCalled();
    });
  });

  describe('handleRetry tests', () => {
    const handleRetrySpy = jest.spyOn(Utils, 'handleRetry');
    it('success test', async () => {
      const handleRetrySpySuccess = jest.spyOn(driver, 'handleProcessRetry');
      handleRetrySpySuccess.mockResolvedValue();
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(0)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(0).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Confirm travel was successfully re-executed.'
      );
      expect(handleRetrySpy).toHaveBeenCalled();
    });
    it('failure', async () => {
      const handleRetrySpyFailed = jest.spyOn(driver, 'handleProcessRetry');
      handleRetrySpyFailed.mockRejectedValue(new Error('403 error'));
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(0)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
        wrapper.find(DropdownItem).at(0).simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(wrapper.find('ProcessInfoModal').props()['modalContent']).toEqual(
        'The node Confirm travel failed to re-execute. Message: "403 error"'
      );
      expect(handleRetrySpy).toHaveBeenCalled();
    });
  });
  describe('test job actions on nodes', () => {
    it('test job details action', async () => {
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(0)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      await act(async () => {
        wrapper.find('#job-details').first().simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(
        wrapper.find('JobsDetailsModal').props()['isModalOpen']
      ).toBeTruthy();
    });
    it('test job reschedule action', async () => {
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      await act(async () => {
        wrapper.find('#job-reschedule').first().simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(
        wrapper.find('JobsRescheduleModal').props()['isModalOpen']
      ).toBeTruthy();
    });
    it('test job cancel action with success response', async () => {
      const modalTitle = 'success';
      const modalContent =
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 is canceled successfully';
      const cancelJobSuccess = jest.spyOn(driver, 'cancelJob');
      cancelJobSuccess.mockImplementationOnce(() =>
        Promise.resolve({ modalTitle, modalContent })
      );
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      await act(async () => {
        wrapper.find('#job-cancel').first().simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(
        wrapper.find('JobsCancelModal').props()['isModalOpen']
      ).toBeTruthy();
    });
    it('test job cancel action with error response', async () => {
      const modalTitle = 'failure';
      const modalContent =
        'The job: 6e74a570-31c8-4020-bd70-19be2cb625f3_0 failed. Message: 404 not found';
      const cancelJobFailed = jest.spyOn(driver, 'cancelJob');
      cancelJobFailed.mockImplementationOnce(() =>
        Promise.resolve({ modalTitle, modalContent })
      );
      let wrapper = mount(<ProcessDetailsTimelinePanel {...props1} />);
      await act(async () => {
        wrapper
          .find(Dropdown)
          .at(1)
          .find(KebabToggle)
          .find('button')
          .simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      await act(async () => {
        wrapper.find('#job-cancel').first().simulate('click');
        await wait(0);
        wrapper = wrapper.update();
      });
      expect(
        wrapper.find('JobsCancelModal').props()['isModalOpen']
      ).toBeTruthy();
    });
    it('test options when there are no jobs', async () => {
      const wrapper = mount(<ProcessDetailsTimelinePanel {...props4} />);
      expect(wrapper).toMatchSnapshot();
    });
  });
});
