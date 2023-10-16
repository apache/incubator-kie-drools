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
import { AuditToolbarTop } from '../AuditToolbar';
import { mount, shallow } from 'enzyme';

const defaultProps = {
  setSearchString: jest.fn(),
  fromDate: '2020-01-01',
  setFromDate: jest.fn(),
  toDate: '2020-02-01',
  setToDate: jest.fn(),
  total: 20,
  page: 1,
  pageSize: 10,
  setPage: jest.fn(),
  setPageSize: jest.fn(),
  onRefresh: jest.fn()
};

describe('Audit top toolbar', () => {
  test('renders correctly', () => {
    const wrapper = shallow(<AuditToolbarTop {...defaultProps} />);
    expect(wrapper).toMatchSnapshot();
  });

  test('allows search by ID', () => {
    const setSearchString = jest.fn();
    const searchString = '12345';
    const wrapper = mount(
      <AuditToolbarTop {...{ ...defaultProps, setSearchString }} />
    );
    const searchInput = wrapper.find('input#audit-search-input');
    const searchButton = wrapper.find('button#audit-search');
    const inputNode = searchInput.getDOMNode<HTMLInputElement>();

    inputNode.value = searchString;
    searchButton.simulate('click');

    expect(setSearchString).toBeCalledTimes(1);
    expect(setSearchString).toBeCalledWith(searchString);

    searchInput.simulate('keydown', { key: 'Enter' });

    expect(setSearchString).toBeCalledTimes(2);
    expect(setSearchString).toBeCalledWith(searchString);
  });

  test('handles from date filter', () => {
    const setFromDate = jest.fn();
    const fromDate = '2020-02-01';
    const wrapper = mount(
      <AuditToolbarTop {...{ ...defaultProps, setFromDate }} />
    );

    wrapper.props().setFromDate(fromDate);

    expect(setFromDate).toBeCalledTimes(1);
    expect(setFromDate).toBeCalledWith(fromDate);
  });

  test('handles to date filter', () => {
    const setToDate = jest.fn();
    const toDate = '2020-04-01';
    const wrapper = mount(
      <AuditToolbarTop {...{ ...defaultProps, setToDate }} />
    );

    wrapper.props().setToDate(toDate);

    expect(setToDate).toBeCalledTimes(1);
    expect(setToDate).toBeCalledWith(toDate);
  });

  test('handles pagination', () => {
    const setPage = jest.fn();
    const setPageSize = jest.fn();
    const page = 2;
    const pageSize = 50;
    const wrapper = mount(
      <AuditToolbarTop {...{ ...defaultProps, setPage, setPageSize }} />
    );

    wrapper.props().setPage(page);
    wrapper.props().setPageSize(pageSize);

    expect(setPage).toBeCalledTimes(1);
    expect(setPage).toBeCalledWith(page);
    expect(setPageSize).toBeCalledTimes(1);
    expect(setPageSize).toBeCalledWith(pageSize);
  });

  test('handles data refresh', () => {
    const onRefresh = jest.fn();
    const wrapper = mount(
      <AuditToolbarTop {...{ ...defaultProps, onRefresh }} />
    );

    wrapper.find('button#executions-refresh').simulate('click');

    expect(onRefresh).toBeCalledTimes(1);
  });
});
