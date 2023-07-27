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
import { shallow } from 'enzyme';
import { NoData } from '../NoData';
import { Button } from '@patternfly/react-core/dist/js/components/Button';

const props1 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {}
};

const props2 = {
  defaultPath: '/processInstances',
  defaultButton: '',
  location: {
    state: {
      title: 'NoData',
      prev: '/processInstances',
      description: 'some description',
      buttonText: 'button'
    }
  }
};

describe('NoData component tests', () => {
  it('snapshot tests with location object', () => {
    const wrapper = shallow(<NoData {...props1} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot tests without location object', () => {
    const wrapper = shallow(<NoData {...props2} />);
    expect(wrapper).toMatchSnapshot();
  });
  /* tslint:disable */
  it('redirect button click', () => {
    const wrapper = shallow(<NoData {...props2} />);
    wrapper.find(Button).simulate('click');
    expect(wrapper.find('Redirect').props()['to']).toEqual('/processInstances');
  });
});
