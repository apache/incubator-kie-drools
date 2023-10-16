/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import { render } from '@testing-library/react';
import DisablePopup from '../DisablePopup';
import { Checkbox } from '@patternfly/react-core/dist/js/components/Checkbox';
import { ProcessInstanceState } from '@kogito-apps/management-console-shared/dist/types';

const props = {
  processInstanceData: {
    id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0blmnop',
    processId: 'travels',
    businessKey: 'Tr1122',
    processName: 'travels',
    roles: [],
    state: ProcessInstanceState.Active,
    endpoint: 'http://localhost:4000',
    serviceUrl: null as unknown as undefined,
    addons: ['process-management'],
    error: {
      nodeDefinitionId: 'a1e139d5-4e77-48c9-84ae-3459188e90433n',
      message: 'Something went wrong'
    },
    start: new Date('2019-12-22T03:40:44.089Z'),
    lastUpdate: new Date('2019-12-22T03:40:44.089Z'),
    variables:
      '{"flight":{"arrival":"2019-10-30T22:00:00Z[UTC]","departure":"2019-10-22T22:00:00Z[UTC]","flightNumber":"MX555"},"hotel":{"address":{"city":"Berlin","country":"Germany","street":"street","zipCode":"12345"},"bookingNumber":"XX-012345","name":"Perfect hotel","phone":"09876543"},"trip":{"begin":"2019-10-22T22:00:00Z[UTC]","city":"Berlin","country":"Germany","end":"2019-10-30T22:00:00Z[UTC]","visaRequired":false},"traveller":{"address":{"city":"Karkow","country":"Poland","street":"palna","zipCode":"200300"},"email":"rob@redhat.com","firstName":"Rob","lastName":"Rob","nationality":"Polish"}}',
    nodes: [
      {
        nodeId: '1',
        name: 'Book Flight',
        definitionId: 'CallActivity_2',
        id: '7cdeba99-cd36-4425-980d-e59d44769a3e',
        enter: new Date('2019-10-22T04:43:01.143Z'),
        exit: new Date('2019-10-22T04:43:01.146Z'),
        type: 'SubProcessNode'
      }
    ],
    childProcessInstances: []
  },
  component: <Checkbox id="test" />
};

describe('DisablePopup component tests', () => {
  it('snapshot testing for no service URL and only process-management addon', () => {
    const container = render(<DisablePopup {...props} />).container;
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing for no service URL and no process-management addon', () => {
    const container = render(
      <DisablePopup
        {...{
          ...props,
          processInstanceData: { ...props.processInstanceData, addons: [] }
        }}
      />
    ).container;
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing for service URL and process-management addon available', () => {
    const container = render(
      <DisablePopup
        {...{
          ...props,
          processInstanceData: {
            ...props.processInstanceData,
            addons: ['process-management'],
            serviceUrl: 'http://localhost:4000'
          }
        }}
      />
    ).container;
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing for no process-management addon and only service URL', () => {
    const container = render(
      <DisablePopup
        {...{
          ...props,
          processInstanceData: {
            ...props.processInstanceData,
            addons: [],
            serviceUrl: 'http://localhost:4000'
          }
        }}
      />
    ).container;
    expect(container).toMatchSnapshot();
  });
});
