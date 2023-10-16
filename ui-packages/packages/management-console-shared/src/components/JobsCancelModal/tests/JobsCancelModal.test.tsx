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
import { setTitle } from '../../../utils/Utils';
import { JobsCancelModal } from '../JobsCancelModal';
import { shallow } from 'enzyme';

jest.mock('../../BulkList/BulkList', () => {
  const originalModule = jest.requireActual('../../BulkList/BulkList');
  return {
    __esModule: true,
    ...originalModule,
    BulkList: jest.fn()
  };
});

const jobOperation = {
  CANCEL: {
    messages: {
      successMessage: 'Cancel Jobs',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noJobsMessage: 'No jobs were canceled'
    },
    functions: {
      perform: jest.fn()
    },
    results: {
      successJobs: {},
      failedJobs: {},
      IgnoredJobs: {}
    }
  }
};
const props = {
  actionType: 'Job Cancel',
  modalContent: 'The job was cancelled successfully',
  modalTitle: setTitle('success', 'Job cancel'),
  isModalOpen: true,
  handleModalToggle: jest.fn()
};

const props1 = {
  actionType: 'Job Cancel',
  modalTitle: setTitle('success', 'Job cancel'),
  modalContent: '',
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  jobOperation
};

describe('job cancel modal tests', () => {
  it('snapshot test - single cancel', () => {
    const wrapper = shallow(<JobsCancelModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot test - bulk cancel', () => {
    const wrapper = shallow(<JobsCancelModal {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
});
