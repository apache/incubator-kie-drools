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
import { AuditToolbarBottom } from '../AuditToolbar';
import { mount, shallow } from 'enzyme';

describe('Audit bottom toolbar', () => {
  test('renders correctly', () => {
    const wrapper = renderAuditToolbarBottom('shallow');
    expect(wrapper).toMatchSnapshot();
  });

  test('handles pagination', () => {
    const setPage = jest.fn();
    const setPageSize = jest.fn();
    const page = 2;
    const pageSize = 50;
    const wrapper = renderAuditToolbarBottom('mount', {
      setPage,
      setPageSize
    });

    wrapper.props().setPage(page);
    wrapper.props().setPageSize(pageSize);

    expect(setPage).toBeCalledTimes(1);
    expect(setPage).toBeCalledWith(page);
    expect(setPageSize).toBeCalledTimes(1);
    expect(setPageSize).toBeCalledWith(pageSize);
  });
});

const renderAuditToolbarBottom = (
  method: 'shallow' | 'mount',
  props?: Record<string, unknown>
) => {
  const defaultProps = {
    total: 20,
    page: 1,
    pageSize: 10,
    setPage: jest.fn(),
    setPageSize: jest.fn(),
    ...props
  };
  if (method === 'shallow') {
    return shallow(<AuditToolbarBottom {...defaultProps} />);
  } else {
    return mount(<AuditToolbarBottom {...defaultProps} />);
  }
};
