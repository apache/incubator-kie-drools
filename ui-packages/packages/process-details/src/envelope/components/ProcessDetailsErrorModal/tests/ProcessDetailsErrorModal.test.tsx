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
import ProcessDetailsErrorModal from '../ProcessDetailsErrorModal';
import { Button } from '@patternfly/react-core';
import { setTitle } from '@kogito-apps/management-console-shared';
import { mount } from 'enzyme';

const MockedIcon = (): React.ReactElement => {
  return <></>;
};
jest.mock('@patternfly/react-icons', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-icons'), {
    InfoCircleIcon: () => {
      return <MockedIcon />;
    }
  })
);

const errorModalAction: JSX.Element[] = [
  <Button key="confirm-selection" variant="primary">
    OK
  </Button>
];
describe('Process details error modal tests', () => {
  const props = {
    errorString: '404-not found',
    errorModalOpen: true,
    errorModalAction: errorModalAction,
    handleErrorModal: jest.fn(),
    label: 'Error modal',
    title: setTitle('failure', 'Process Visualization')
  };

  it('Snapshot test with default props', () => {
    const wrapper = mount(<ProcessDetailsErrorModal {...props} />).find(
      'ProcessDetailsErrorModal'
    );
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('Text').children().text()).toEqual('404-not found');
  });
});
