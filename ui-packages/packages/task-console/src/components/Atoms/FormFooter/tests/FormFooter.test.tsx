/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { mount } from 'enzyme';
import FormFooter from '../FormFooter';
import { ActionGroup } from '@patternfly/react-core';
import { FormAction } from '../../../../util/uniforms/FormActionsUtils';

describe('Form Footer test', () => {
  it('testing showing actions', () => {
    const actions: FormAction[] = [
      {
        name: 'Abort',
        execute: jest.fn()
      },
      {
        name: 'Release',
        execute: jest.fn()
      },
      {
        name: 'Complete',
        execute: jest.fn()
      }
    ];

    const wrapper = mount(<FormFooter actions={actions} />);
    expect(wrapper).toMatchSnapshot();

    const actionGroup = wrapper.find(ActionGroup);
    expect(actionGroup.exists()).toBeTruthy();

    const buttons = actionGroup.getElement().props.children;
    expect(buttons).toHaveLength(3);

    expect(buttons[0].props.type).toStrictEqual('submit');
    expect(buttons[0].props.variant).toStrictEqual('primary');
    expect(buttons[0].key).toStrictEqual('submit-Complete');
    expect(buttons[0].props.children).toStrictEqual('Complete');

    expect(buttons[1].props.type).toStrictEqual('submit');
    expect(buttons[1].props.variant).toStrictEqual('secondary');
    expect(buttons[1].key).toStrictEqual('submit-Abort');
    expect(buttons[1].props.children).toStrictEqual('Abort');

    expect(buttons[2].props.type).toStrictEqual('submit');
    expect(buttons[2].props.variant).toStrictEqual('secondary');
    expect(buttons[2].key).toStrictEqual('submit-Release');
    expect(buttons[2].props.children).toStrictEqual('Release');
  });

  it('testing showing empty actions', () => {
    const props = {
      actions: []
    };

    const wrapper = mount(<FormFooter {...props} />);
    expect(wrapper).toMatchSnapshot();
  });

  it('testing showing no actions', () => {
    const wrapper = mount(<FormFooter />);
    expect(wrapper).toMatchSnapshot();
  });

  it('testing action click', () => {
    const releaseAction = {
      name: 'Release',
      execute: jest.fn()
    };

    const completeAction = {
      name: 'Complete',
      execute: jest.fn()
    };

    const props = {
      actions: [releaseAction, completeAction]
    };

    const wrapper = mount(<FormFooter {...props} />);
    expect(wrapper).toMatchSnapshot();

    const button1 = wrapper.findWhere(node => node.key() === 'submit-Release');
    button1.simulate('click');

    expect(releaseAction.execute).toBeCalledTimes(1);

    const button2 = wrapper.findWhere(node => node.key() === 'submit-Complete');
    button2.simulate('click');

    expect(completeAction.execute).toBeCalledTimes(1);
  });
});
