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
import { ProcessInfoModal } from '../ProcessInfoModal';
import { InfoCircleIcon } from '@patternfly/react-icons/dist/js/icons/info-circle-icon';

const props = {
  modalTitle: (
    <>
      <InfoCircleIcon
        className="pf-u-mr-sm"
        color="var(--pf-global--info-color--100)"
      />
      {'Abort operation'}
    </>
  ),
  isModalOpen: true,
  handleModalToggle: jest.fn(),
  processName: 'travels',
  modalContent: 'The process travels was aborted successfully',
  operationResult: {
    type: 'process_instance',
    messages: {
      successMessage: 'Aborted',
      ignoredMessage:
        'These processes were ignored because they were already completed or aborted',
      noItemsMessage: 'No processes were aborted'
    },
    results: {
      successItems: [
        {
          id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
          name: 'travels',
          description: 'TT@12'
        }
      ],
      failedItems: [],
      ignoredItems: [
        {
          id: 'e735128t-6tt7-4aa8-9ec0-e18e19809e0b',
          name: 'travels2',
          description: 'TT998'
        }
      ]
    },
    functions: {
      perform: jest.fn()
    }
  },
  resetSelected: jest.fn()
};

describe('ProcessBulkModal component tests', () => {
  it('snapshot testing', () => {
    const wrapper = shallow(<ProcessInfoModal {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('Ok click test', () => {
    const wrapper = shallow(<ProcessInfoModal {...props} />);
    wrapper.props()['actions'][0]['props']['onClick']();
    expect(props.resetSelected).toHaveBeenCalled();
    expect(props.handleModalToggle).toHaveBeenCalled();
  });
});
