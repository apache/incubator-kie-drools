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
import { mount } from 'enzyme';
import { ActionList, Button } from '@patternfly/react-core';
import FormFooter from '../FormFooter';
import { ActionType, FormAction } from '../../utils';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@patternfly/react-core', () =>
  Object.assign({}, jest.requireActual('@patternfly/react-core'), {
    Button: () => <MockedComponent />
  })
);

describe('Form Footer test', () => {
  it('showing actions', () => {
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

    const actionList = wrapper.find(ActionList);
    expect(actionList.exists()).toBeTruthy();

    const buttons = actionList.find(Button);
    expect(buttons).toHaveLength(3);

    const completeButton = buttons.get(0);

    expect(completeButton.props.type).toStrictEqual('submit');
    expect(completeButton.props.variant).toStrictEqual('primary');
    expect(completeButton.key).toStrictEqual('submit-Complete');
    expect(completeButton.props.children).toStrictEqual('Complete');
    expect(completeButton.props.isDisabled).toStrictEqual(false);

    const abortButton = buttons.get(1);
    expect(abortButton.props.type).toStrictEqual('submit');
    expect(abortButton.props.variant).toStrictEqual('secondary');
    expect(abortButton.key).toStrictEqual('submit-Abort');
    expect(abortButton.props.children).toStrictEqual('Abort');
    expect(abortButton.props.isDisabled).toStrictEqual(false);

    const releaseButton = buttons.get(2);
    expect(releaseButton.props.type).toStrictEqual('submit');
    expect(releaseButton.props.variant).toStrictEqual('secondary');
    expect(releaseButton.key).toStrictEqual('submit-Release');
    expect(releaseButton.props.children).toStrictEqual('Release');
    expect(releaseButton.props.isDisabled).toStrictEqual(false);
  });

  it('showing disabled', () => {
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

    const wrapper = mount(<FormFooter actions={actions} enabled={false} />);
    expect(wrapper).toMatchSnapshot();

    const actionList = wrapper.find(ActionList);
    expect(actionList.exists()).toBeTruthy();

    const buttons = actionList.find(Button);
    expect(buttons).toHaveLength(3);

    const completeButton = buttons.get(0);
    expect(completeButton.props.isDisabled).toStrictEqual(true);

    const abortButton = buttons.get(1);
    expect(abortButton.props.isDisabled).toStrictEqual(true);

    const releaseButton = buttons.get(2);
    expect(releaseButton.props.isDisabled).toStrictEqual(true);
  });

  it('showing empty actions', () => {
    const props = {
      actions: [],
      actionType: ActionType.SUBMIT
    };

    const wrapper = mount(<FormFooter {...props} />);
    expect(wrapper.children()).toHaveLength(0);
  });

  it('showing no actions', () => {
    const wrapper = mount(<FormFooter />);
    expect(wrapper.children()).toHaveLength(0);
  });

  it('action click', () => {
    const releaseAction = {
      name: 'Release',
      execute: jest.fn(),
      actionType: ActionType.SUBMIT
    };

    const completeAction = {
      name: 'Complete',
      execute: jest.fn(),
      actionType: ActionType.SUBMIT
    };

    const props = {
      actions: [releaseAction, completeAction],
      enabled: true,
      onSubmitForm: jest.fn()
    };

    const wrapper = mount(<FormFooter {...props} />);

    const releaseButton = wrapper.findWhere(
      node => node.key() === 'submit-Release'
    );
    releaseButton.props().onClick();

    expect(releaseAction.execute).toBeCalledTimes(1);

    const completeButton = wrapper.findWhere(
      node => node.key() === 'submit-Complete'
    );
    completeButton.props().onClick();
    
    expect(completeAction.execute).toBeCalledTimes(1);
    expect(props.onSubmitForm).toHaveBeenCalled();
  });
});
