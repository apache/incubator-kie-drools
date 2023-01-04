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
import { shallow, mount } from 'enzyme';
import LoadMore from '../LoadMore';
import {
  DropdownToggle,
  DropdownItem,
  DropdownToggleAction
} from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import { CheckIcon } from '@patternfly/react-icons';

describe('LoadMore component tests with isLoading false', () => {
  const props = {
    offset: 0,
    setOffset: jest.fn(),
    getMoreItems: jest.fn(),
    pageSize: 10,
    isLoadingMore: false,
    ouiaId: 'load-more-ouia-id',
    setLoadMoreClicked: jest.fn()
  };
  it('snapshot testing', () => {
    const wrapper = shallow(<LoadMore {...props} />);
    expect(wrapper).toMatchSnapshot();
  });
  it('select dropdown options tests', async () => {
    let wrapper = mount(<LoadMore {...props} />);
    await act(async () => {
      wrapper.find(DropdownToggle).find('button').at(1).simulate('click');
    });
    wrapper = wrapper.update();
    // length of dropdown is 4(10,20,50,100)
    expect(wrapper.find(DropdownItem).length).toBe(4);
    await act(async () => {
      wrapper.find(DropdownItem).at(1).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    // change selection to 20
    expect(wrapper.find(DropdownItem).at(1).find(CheckIcon)).toBeDefined();

    await act(async () => {
      wrapper.find(DropdownItem).at(2).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    // change selection to 50
    expect(wrapper.find(DropdownItem).at(2).find(CheckIcon)).toBeDefined();

    await act(async () => {
      wrapper.find(DropdownItem).at(3).find('button').simulate('click');
    });
    wrapper = wrapper.update();
    // change selection to 100
    expect(wrapper.find(DropdownItem).at(3).find(CheckIcon)).toBeDefined();
  });

  it('click loadmore button', async () => {
    const wrapper = mount(<LoadMore {...props} />);
    await act(async () => {
      wrapper.find(DropdownToggleAction).find('button').simulate('click');
    });
    expect(props.getMoreItems).toHaveBeenCalled();
    expect(props.setLoadMoreClicked).toHaveBeenCalled();
    expect(props.setOffset).toHaveBeenCalled();
  });

  it('simulate loading state in button', async () => {
    let wrapper = mount(
      <LoadMore {...{ ...props, isLoadingMore: true, ouiaSafe: true }} />
    );
    expect(
      wrapper
        .find(DropdownToggleAction)
        .find('button')
        .children()
        .at(0)
        .contains('Loading...')
    ).toBeTruthy();
    wrapper = wrapper.find(DropdownToggleAction);
    expect(wrapper).toMatchSnapshot();
  });
});
