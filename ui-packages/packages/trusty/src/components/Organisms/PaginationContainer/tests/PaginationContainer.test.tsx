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
import { mount } from 'enzyme';
import PaginationContainer from '../PaginationContainer';
import { PaginationVariant } from '@patternfly/react-core';

const defaultProps = {
  total: 10,
  page: 1,
  pageSize: 10,
  paginationId: 'pagination-id',
  onSetPage: jest.fn(),
  onSetPageSize: jest.fn(),
  position: PaginationVariant.top
};

describe('PaginationContainer', () => {
  test('handle page and page size changes', () => {
    const wrapper = mount(<PaginationContainer {...defaultProps} />);
    const page = 2;
    const pageSize = 50;

    wrapper.props().onSetPage(page);
    wrapper.props().onSetPageSize(pageSize);

    expect(defaultProps.onSetPage).toHaveBeenCalledTimes(1);
    expect(defaultProps.onSetPage).toHaveBeenCalledWith(page);
    expect(defaultProps.onSetPageSize).toHaveBeenCalledTimes(1);
    expect(defaultProps.onSetPageSize).toHaveBeenCalledWith(pageSize);
  });
});
