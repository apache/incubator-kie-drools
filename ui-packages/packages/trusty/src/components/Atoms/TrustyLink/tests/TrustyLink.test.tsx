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
import * as React from 'react';
import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';
import { TrustyContext } from '../../../Templates/TrustyApp/TrustyApp';
import TrustyLink from '../TrustyLink';

const counterfactualEnabled = true;
const explanationEnabled = true;
const url = '/sample-link';
const urlDescription = 'sample link';
const mockHistoryPush = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useHistory: () => ({
    push: mockHistoryPush
  })
}));

const setupWrapper = (useHrefLinks: boolean) => {
  return mount(
    <MemoryRouter>
      <TrustyContext.Provider
        value={{
          config: {
            counterfactualEnabled,
            explanationEnabled,
            basePath: '',
            useHrefLinks
          }
        }}
      >
        <TrustyLink url={url}>{urlDescription}</TrustyLink>
      </TrustyContext.Provider>
    </MemoryRouter>
  );
};

describe('TrustyLink', () => {
  test('renders a regular link with href attribute', () => {
    const wrapper = setupWrapper(true);

    expect(wrapper.find('Link')).toHaveLength(1);
    expect(wrapper.find('a').props().href).toMatch(url);
    expect(wrapper.find('a').text()).toMatch(urlDescription);
  });

  test('renders a link managed via onClick', () => {
    const wrapper = setupWrapper(false);

    expect(wrapper.find('Link')).toHaveLength(0);
    expect(wrapper.find('a').props().href).toBeUndefined();
    expect(wrapper.find('a').text()).toMatch(urlDescription);

    wrapper.find('a').at(0).simulate('click');

    expect(mockHistoryPush).toHaveBeenCalledTimes(1);
    expect(mockHistoryPush).toHaveBeenCalledWith(url);
  });
});
