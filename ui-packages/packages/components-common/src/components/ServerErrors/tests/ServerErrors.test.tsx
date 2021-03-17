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
import { getWrapper } from '@kogito-apps/components-common';
import { Button } from '@patternfly/react-core';
import ServerErrors from '../ServerErrors';

const props = {
  error: 'some error',
  variant: 'large'
};

const props2 = {
  error: 'error occured',
  variant: 'small'
};

describe('ServerErrors component tests', () => {
  it('snapshot testing ', () => {
    const wrapper = getWrapper(<ServerErrors {...props} />, 'ServerErrors');

    expect(wrapper).toMatchSnapshot();
  });

  it('snapshot with children ', () => {
    const onClickMock = jest.fn();

    const wrapper = getWrapper(
      <ServerErrors {...props}>
        <Button onClick={onClickMock}>Go back</Button>
      </ServerErrors>,
      'ServerErrors'
    );

    expect(wrapper).toMatchSnapshot();

    const backButton = wrapper
      .find(Button)
      .findWhere(button => button.text() === 'Go back')
      .first();

    expect(backButton.exists()).toBeTruthy();

    backButton.simulate('click');

    expect(onClickMock).toHaveBeenCalled();
  });

  it('display error button click ', () => {
    let wrapper = getWrapper(<ServerErrors {...props} />, 'ServerErrors');

    wrapper
      .find('#display-error')
      .first()
      .simulate('click');

    wrapper = wrapper.update();

    expect(
      wrapper
        .find('#content-0')
        .find('pre')
        .props()['children']
    ).toEqual('"some error"');
  });

  it('snapshot testing with small variant ', () => {
    const wrapper = getWrapper(<ServerErrors {...props2} />, 'ServerErrors');

    expect(wrapper).toMatchSnapshot();
  });

  it('display error button click with small variant ', () => {
    let wrapper = getWrapper(<ServerErrors {...props2} />, 'ServerErrors');

    wrapper
      .find('#display-error')
      .first()
      .simulate('click');

    wrapper = wrapper.update();

    expect(wrapper.find('pre').props()['children']).toEqual('"error occured"');
  });
});
