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
import ProcessDetails from '../ProcessDetails';
import { mount } from 'enzyme';
import { MockedProcessDetailsDriver } from '../../../../embedded/tests/mocks/Mocks';
import {
  Job,
  JobStatus,
  MilestoneStatus,
  ProcessInstance,
  ProcessInstanceState
} from '@kogito-apps/management-console-shared';
import wait from 'waait';

jest.mock('../../JobsPanel/JobsPanel');
jest.mock('../../ProcessDiagram/ProcessDiagram');
jest.mock('../../ProcessDetailsErrorModal/ProcessDetailsErrorModal');
jest.mock('../../ProcessVariables/ProcessVariables');
jest.mock('../../ProcessDetailsPanel/ProcessDetailsPanel');
jest.mock('../../ProcessDetailsMilestonesPanel/ProcessDetailsMilestonesPanel');
jest.mock('../../ProcessDetailsTimelinePanel/ProcessDetailsTimelinePanel');
jest.mock('../../ProcessDetailsNodeTrigger/ProcessDetailsNodeTrigger');
jest.mock('../../SwfCombinedEditor/SwfCombinedEditor');

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20

const mockMath = Object.create(global.Math);
mockMath.random = () => 0.5;
global.Math = mockMath;
describe('ProcessDetails tests', () => {
  describe('ProcessDetails tests with success results', () => {
    const data: ProcessInstance = {
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      processId: 'hotelBooking',
      processName: 'HotelBooking',
      businessKey: 'T1234HotelBooking01',
      parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      parentProcessInstance: null,
      roles: [],
      variables:
        '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
      state: ProcessInstanceState.Completed,
      start: new Date('2019-10-22T03:40:44.089Z'),
      lastUpdate: new Date('Thu, 22 Apr 2021 14:53:04 GMT'),
      end: new Date('2019-10-22T05:40:44.089Z'),
      addons: ['process-management'],
      endpoint: 'http://localhost:4000',
      serviceUrl: 'http://localhost:4000',
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
        message: 'some thing went wrong',
        __typename: 'ProcessInstanceError'
      },
      childProcessInstances: [],
      nodes: [
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
          nodeId: '1',
          name: 'End Event 1',
          enter: new Date('2019-10-22T03:37:30.798Z'),
          exit: new Date('2019-10-22T03:37:30.798Z'),
          type: 'EndNode',
          definitionId: 'EndEvent_1',
          __typename: 'NodeInstance'
        },
        {
          id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
          nodeId: '2',
          name: 'Book hotel',
          enter: new Date('2019-10-22T03:37:30.795Z'),
          exit: new Date('2019-10-22T03:37:30.798Z'),
          type: 'WorkItemNode',
          definitionId: 'ServiceTask_1',
          __typename: 'NodeInstance'
        },
        {
          id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
          nodeId: '2',
          name: 'StartProcess',
          enter: new Date('2019-10-22T03:37:30.793Z'),
          exit: new Date('2019-10-22T03:37:30.795Z'),
          type: 'StartNode',
          definitionId: 'StartEvent_1',
          __typename: 'NodeInstance'
        }
      ],
      milestones: [
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
          name: 'Manager decision',
          status: MilestoneStatus.Completed,
          __typename: 'Milestone'
        },
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
          name: 'Milestone 1: Order placed',
          status: MilestoneStatus.Active,
          __typename: 'Milestone'
        },
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
          name: 'Milestone 2: Order shipped',
          status: MilestoneStatus.Available,
          __typename: 'Milestone'
        }
      ]
    };
    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: MockedProcessDetailsDriver(),
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      processDetails: data,
      omittedProcessTimelineEvents: [],
      showSwfDiagram: true
    };

    const Jobs: Job[] = [
      {
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        endpoint: 'http://localhost:4000/jobs',
        executionCounter: 0,
        expirationTime: new Date('2020-08-29T04:35:54.631Z'),
        id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
        lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
        priority: 0,
        processId: 'travels',
        processInstanceId: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
        repeatInterval: null,
        repeatLimit: null,
        retries: 2,
        rootProcessId: '',
        scheduledId: null,
        status: JobStatus.Scheduled
      }
    ];
    const svgResponse =
      '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></g></g></svg>';

    const svgResults = { svg: svgResponse };

    beforeEach(() => {
      jest.clearAllMocks();
      (props.driver.jobsQuery as jest.Mock).mockImplementationOnce(() => Jobs);
      (props.driver.getProcessDiagram as jest.Mock).mockImplementationOnce(
        () => svgResults
      );
      (
        props.driver.handleProcessVariableUpdate as jest.Mock
      ).mockImplementationOnce(
        () =>
          new Promise((resolve, reject) => {
            resolve(data.variables);
          })
      );
    });
    it('Snapshot tests with default prop', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      expect(wrapper).toMatchSnapshot();
    });

    it('Initiaload with query responses', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('MockedJobsPanel')).toBeTruthy();
      expect(wrapper.find('MockedProcessDiagram')).toBeTruthy();
      expect(wrapper.find('MockedProcessVariables')).toBeTruthy();
    });

    it('handle save option', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('#save-button').at(1).simulate('click');
      });
    });

    it('handle refresh option', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('#refresh-button').at(1).simulate('click');
      });
      expect(props.driver.jobsQuery).toHaveBeenCalled();
    });
  });

  describe('ProcessDetails tests with error response', () => {
    const data: ProcessInstance = {
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      processId: 'hotelBooking',
      processName: 'HotelBooking',
      businessKey: 'T1234HotelBooking01',
      parentProcessInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
      parentProcessInstance: null,
      roles: [],
      variables:
        '{"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
      state: ProcessInstanceState.Completed,
      start: new Date('2019-10-22T03:40:44.089Z'),
      lastUpdate: new Date('Thu, 22 Apr 2021 14:53:04 GMT'),
      end: new Date('2019-10-22T05:40:44.089Z'),
      addons: ['process-management'],
      endpoint: 'http://localhost:4000',
      serviceUrl: 'http://localhost:4000',
      error: {
        nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-34578e904e6b',
        message: 'some thing went wrong',
        __typename: 'ProcessInstanceError'
      },
      childProcessInstances: [],
      nodes: [
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
          nodeId: '1',
          name: 'End Event 1',
          enter: new Date('2019-10-22T03:37:30.798Z'),
          exit: new Date('2019-10-22T03:37:30.798Z'),
          type: 'EndNode',
          definitionId: 'EndEvent_1',
          __typename: 'NodeInstance'
        },
        {
          id: '41b3f49e-beb3-4b5f-8130-efd28f82b971',
          nodeId: '2',
          name: 'Book hotel',
          enter: new Date('2019-10-22T03:37:30.795Z'),
          exit: new Date('2019-10-22T03:37:30.798Z'),
          type: 'WorkItemNode',
          definitionId: 'ServiceTask_1',
          __typename: 'NodeInstance'
        },
        {
          id: '4165a571-2c79-4fd0-921e-c6d5e7851b67',
          nodeId: '2',
          name: 'StartProcess',
          enter: new Date('2019-10-22T03:37:30.793Z'),
          exit: new Date('2019-10-22T03:37:30.795Z'),
          type: 'StartNode',
          definitionId: 'StartEvent_1',
          __typename: 'NodeInstance'
        }
      ],
      milestones: [
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75i86',
          name: 'Manager decision',
          status: MilestoneStatus.Completed,
          __typename: 'Milestone'
        },
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75m36',
          name: 'Milestone 1: Order placed',
          status: MilestoneStatus.Active,
          __typename: 'Milestone'
        },
        {
          id: '27107f38-d888-4edf-9a4f-11b9e6d75m66',
          name: 'Milestone 2: Order shipped',
          status: MilestoneStatus.Available,
          __typename: 'Milestone'
        }
      ]
    };

    const props = {
      isEnvelopeConnectedToChannel: true,
      driver: MockedProcessDetailsDriver(),
      id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
      processDetails: data,
      omittedProcessTimelineEvents: [],
      showSwfDiagram: false
    };

    const Jobs: Job[] = [
      {
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/dad3aa88-5c1e-4858-a919-6123c675a0fa_0',
        endpoint: 'http://localhost:4000/jobs',
        executionCounter: 0,
        expirationTime: new Date('2020-08-29T04:35:54.631Z'),
        id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
        lastUpdate: new Date('2020-06-29T03:35:54.635Z'),
        priority: 0,
        processId: 'travels',
        processInstanceId: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
        repeatInterval: null,
        repeatLimit: null,
        retries: 2,
        rootProcessId: '',
        scheduledId: null,
        status: JobStatus.Scheduled
      }
    ];

    const svgResults = { error: '404 - not found' };

    beforeEach(() => {
      jest.clearAllMocks();
      (props.driver.processDetailsQuery as jest.Mock).mockImplementationOnce(
        () => data
      );
      (props.driver.jobsQuery as jest.Mock).mockImplementationOnce(() => Jobs);
      (props.driver.getProcessDiagram as jest.Mock).mockImplementationOnce(
        () => svgResults
      );
      (
        props.driver.handleProcessVariableUpdate as jest.Mock
      ).mockImplementationOnce(
        () =>
          new Promise((resolve, reject) => {
            resolve(data.variables);
          })
      );
    });
    it('Test svg error modal', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper
          .find('MockedProcessDetailsErrorModal')
          .props()
          ['handleErrorModal']();
      });
      expect(
        wrapper.find('MockedProcessDetailsErrorModal').props()['errorModalOpen']
      ).toEqual(true);
    });

    it('Test process variable success modal', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('Modal').at(1).props()['onClose']();
      });
      wrapper = wrapper.update();

      expect(wrapper.find('Modal').at(1).props()['isOpen']).toEqual(true);
      await act(async () => {
        wrapper.find('#confirm-button').at(0).simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('Modal').at(1).props()['isOpen']).toEqual(false);
      await act(async () => {
        wrapper.find('Modal').at(1).props()['onClose']();
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('#cancel-button').at(0).simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('Modal').at(1).props()['isOpen']).toEqual(false);
    });

    it('Test process variable error modal', async () => {
      let wrapper;
      await act(async () => {
        wrapper = mount(<ProcessDetails {...props} />);
        await wait(0);
        wrapper = wrapper.update().find('ProcessDetails');
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('Modal').at(0).props()['onClose']();
      });
      wrapper = wrapper.update();

      expect(wrapper.find('Modal').at(0).props()['isOpen']).toEqual(true);
      await act(async () => {
        wrapper.find('#retry-button').at(0).simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('Modal').at(0).props()['isOpen']).toEqual(false);
      await act(async () => {
        wrapper.find('Modal').at(0).props()['onClose']();
      });
      wrapper = wrapper.update();
      await act(async () => {
        wrapper.find('#discard-button').at(0).simulate('click');
      });
      wrapper = wrapper.update();
      expect(wrapper.find('Modal').at(0).props()['isOpen']).toEqual(false);
    });
  });
});
