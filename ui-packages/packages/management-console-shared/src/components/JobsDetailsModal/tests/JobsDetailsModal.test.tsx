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
import JobsDetailsModal from '../JobsDetailsModal';
import { JobStatus } from '../../../types';
import { mount } from 'enzyme';
import { InfoCircleIcon } from '@patternfly/react-icons/dist/js/icons/info-circle-icon';
import { Button } from '@patternfly/react-core/dist/js/components/Button';

jest.mock('react-datetime-picker');
const props = {
  actionType: 'Job Details',
  job: {
    id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    processId: 'travels',
    processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
    status: JobStatus.Executed,
    priority: 0,
    callbackEndpoint:
      'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
    repeatInterval: 1,
    repeatLimit: 3,
    scheduledId: '0',
    retries: 0,
    lastUpdate: new Date('2020-08-27T03:35:50.147Z'),
    expirationTime: new Date('2020-08-27T03:35:50.147Z'),
    executionCounter: 6
  },
  modalTitle: (
    <>
      <InfoCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--info-color--100)"
      />
      {'Jobs Details'}
    </>
  ),
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  modalAction: [
    <Button key="confirm-selection" variant="primary">
      OK
    </Button>
  ]
};

Date.now = jest.fn(() => 1592000000000); // UTC Fri Jun 12 2020 22:13:20
describe('Job details modal tests', () => {
  it('Snapshot testing', () => {
    const wrapper = mount(<JobsDetailsModal {...props} />).find(
      'JobsDetailsModal'
    );
    expect(wrapper).toMatchSnapshot();
  });
});
