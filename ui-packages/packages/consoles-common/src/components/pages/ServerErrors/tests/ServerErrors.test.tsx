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
import ServerErrors from '../ServerErrors';
import { BrowserRouter } from 'react-router-dom';
import { getWrapper } from '@kogito-apps/components-common';

const mockGoBack = jest.fn();
const props = {
  error: 'some error',
  variant: 'large',
  history: {
    goBack: mockGoBack
  }
};

const props2 = {
  error: 'error occured',
  variant: 'small',
  history: {
    goBack: mockGoBack
  }
};

describe('ServerErrors component tests', () => {
  it('snapshot testing ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>,
      'ServerErrors'
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('goback button click ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>,
      'ServerErrors'
    );
    wrapper
      .find('#goback-button')
      .first()
      .simulate('click');
    expect(window.location.pathname).toEqual('/');
  });

  /* tslint:disable */
  it('display error button click ', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ServerErrors {...props} />
      </BrowserRouter>
    );
    wrapper
      .find('#display-error')
      .first()
      .simulate('click');
    wrapper.update();
    expect(
      wrapper
        .find('#content-0')
        .find('pre')
        .props()['children']
    ).toEqual('"some error"');
  });
  it('snapshot testing with small variant ', () => {
    const wrapper = getWrapper(
      <BrowserRouter>
        <ServerErrors {...props2} />
      </BrowserRouter>,
      'ServerErrors'
    );
    expect(wrapper).toMatchSnapshot();
  });

  /* tslint:disable */
  it('display error button click with small variant ', () => {
    const wrapper = mount(
      <BrowserRouter>
        <ServerErrors {...props2} />
      </BrowserRouter>
    );
    wrapper
      .find('#display-error')
      .first()
      .simulate('click');
    expect(wrapper.find('pre').props()['children']).toEqual('"error occured"');
  });
});
