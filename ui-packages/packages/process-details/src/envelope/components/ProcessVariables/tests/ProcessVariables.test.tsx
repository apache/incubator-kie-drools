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
import ProcessVariables from '../ProcessVariables';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared';

const processInstance = {
  id: 'tEE12-fo54-l665-mp112-akou112345566',
  processId: 'travels',
  businessKey: 'TEE12',
  parentProcessInstanceId: null,
  parentProcessInstance: null,
  processName: 'travels',
  roles: [],
  state: ProcessInstanceState.Completed,
  rootProcessInstanceId: null,
  addons: ['jobs-management', 'prometheus-monitoring', 'process-management'],
  start: new Date('2019-10-22T03:40:44.089Z'),
  lastUpdate: new Date('2019-10-22T03:40:44.089Z'),
  end: new Date('2019-10-22T05:40:44.089Z'),
  error: {
    nodeDefinitionId: '_2140F05A-364F-40B3-BB7B-B12927065DF8',
    message: 'Something went wrong'
  },
  serviceUrl: 'http://localhost:4000',
  endpoint: 'http://localhost:4000',
  variables:
    '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Bangalore","country":"India","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"hotel":{"address":{"city":"Bangalore","country":"India","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"traveller":{"address":{"city":"Bangalore","country":"US","street":"Bangalore","zipCode":"560093"},"email":"ajaganat@redhat.com","firstName":"Ajay","lastName":"Jaganathan","nationality":"US"}}',
  nodes: [
    {
      nodeId: '1',
      name: 'End Event 1',
      definitionId: 'EndEvent_1',
      id: '870bdda0-be04-4e59-bb0b-f9b665eaacc9',
      enter: new Date('2019-10-22T03:37:38.586Z'),
      exit: new Date('2019-10-22T03:37:38.586Z'),
      type: 'EndNode'
    }
  ],
  milestones: [],
  childProcessInstances: []
};
const MockedComponent = (): React.ReactElement => {
  return <></>;
};
jest.mock('react-json-view', () =>
  jest.fn((_props) => <MockedComponent {..._props} />)
);
jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedComponent />;
    }
  })
);
const props = {
  setUpdateJson: jest.fn(),
  displayLabel: false,
  updateJson: {
    trip: {
      begin: '2019-10-22T22:00:00Z[UTC]',
      city: 'Berlin',
      country: 'Germany',
      end: '2019-10-30T22:00:00Z[UTC]',
      visaRequired: false
    }
  },
  setDisplayLabel: jest.fn(),
  displaySuccess: false,
  processInstance: processInstance
};

const props2 = {
  setUpdateJson: jest.fn(),
  displayLabel: true,
  updateJson: {
    trip: {
      begin: '2019-10-22T22:00:00Z[UTC]',
      city: 'Berlin',
      country: 'Germany',
      end: '2019-10-30T22:00:00Z[UTC]',
      visaRequired: false
    }
  },
  setDisplayLabel: jest.fn(),
  displaySuccess: true,
  processInstance: { ...processInstance, state: ProcessInstanceState.Active }
};

describe('ProcessVariables component tests', () => {
  it('snapshot testing without variables', () => {
    const wrapper = shallow(<ProcessVariables {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing with variables', () => {
    const wrapper = mount(<ProcessVariables {...props2} />);
    expect(wrapper.find(ProcessVariables)).toMatchSnapshot();
    const onEdit = () => {
      return null;
    };
    const obj = {
      name: false,
      onEdit,
      src: {
        trip: {
          begin: '2019-10-22T22:00:00Z[UTC]',
          city: 'Berlin',
          country: 'Germany',
          end: '2019-10-30T22:00:00Z[UTC]',
          visaRequired: false
        }
      }
    };
    wrapper.find('mockConstructor').first().props()['onEdit'](obj);
    expect(props2.setUpdateJson).toHaveBeenCalled();
    expect(props2.setDisplayLabel).toHaveBeenCalled();
  });
});
