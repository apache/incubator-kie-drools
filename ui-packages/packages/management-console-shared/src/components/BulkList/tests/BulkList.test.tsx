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
import { shallow } from 'enzyme';
import { BulkList } from '../BulkList';

const props1 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled',
      warningMessage:
        'Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.'
    },
    results: {
      successItems: [
        {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          name: 'travels',
          description: 'TT111'
        }
      ],
      failedItems: [],
      ignoredItems: [
        {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          name: 'travels1',
          description: 'T@123'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props2 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled'
    },
    results: {
      successItems: [],
      failedItems: [],
      ignoredItems: [
        {
          id: '2234dde-npce1-2908-b3131-6123c675a0fa_0',
          name: 'travels1',
          description: 'TT111'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  }
};

const props3 = {
  operationResult: {
    type: 'job',
    messages: {
      successMessage: 'Cancel job',
      ignoredMessage:
        'These jobs were ignored because they were executed or canceled',
      noItemsMessage: 'No jobs were canceled'
    },
    results: {
      successItems: [
        {
          id: 'eff4ee-11qw23-6675-pokau97-qwedjut45a0fa_0',
          name: 'travels',
          description: 'TT111'
        }
      ],
      failedItems: [
        {
          id: 'T3113e-vbg43-2234-lo89-cpmw3214ra0fa_0',
          name: 'travels1',
          description: 'T4433',
          errorMessage: '404 error'
        }
      ],
      ignoredItems: []
    },
    functions: {
      perform: jest.fn()
    }
  }
};

describe('JobsBulkLists component tests', () => {
  it('snapshot testing multi-cancel with canceled and skipped jobs ', () => {
    const wrapper = shallow(<BulkList {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no canceled jobs and only skipped jobs', () => {
    const wrapper = shallow(<BulkList {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('snapshot testing multi-cancel with no skipped jobs', () => {
    const wrapper = shallow(<BulkList {...props3} />);
    expect(wrapper).toMatchSnapshot();
  });
});
