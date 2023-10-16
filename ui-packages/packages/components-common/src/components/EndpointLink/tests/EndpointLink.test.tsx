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
import { render, screen } from '@testing-library/react';
import { EndpointLink } from '../EndpointLink';

const props1 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: true,
  ouiaId: 'endpoint-link-1'
};

const props2 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: false,
  ouiaId: 'endpoint-link-2'
};

const props3 = {
  serviceUrl: 'http://localhost:4000/',
  isLinkShown: false,
  linkLabel: 'This is a label',
  ouiaId: 'endpoint-link-3'
};

const props4 = {
  serviceUrl: null,
  isLinkShown: false,
  ouiaId: 'endpoint-link-4'
};
describe('EndpointLink component tests', () => {
  it('snapshot testing for link shown', () => {
    const { container } = render(<EndpointLink {...props1} />);
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing for link hidden', () => {
    const { container } = render(<EndpointLink {...props2} />);
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing for link hidden with custom link label', () => {
    const { container } = render(<EndpointLink {...props3} />);
    expect(container).toMatchSnapshot();
  });
  it('snapshot testing no service URL and link hidden', () => {
    const { container } = render(<EndpointLink {...props4} />);
    expect(container).toMatchSnapshot();
  });
});
