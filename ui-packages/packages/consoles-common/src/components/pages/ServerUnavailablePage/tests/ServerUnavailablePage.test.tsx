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
import { Button } from '@patternfly/react-core/dist/js/components/Button';
import { EmptyStateBody } from '@patternfly/react-core/dist/js/components/EmptyState';
import ServerUnavailablePage from '../ServerUnavailablePage';
import { act } from 'react-dom/test-utils';

const reload = jest.fn();
process.env.KOGITO_APP_NAME = 'Sample console';

describe('ServerUnavailablePage tests', () => {
  beforeEach(() => {
    reload.mockClear();
  });
  it('Snapshot with default name', () => {
    const wrapper = mount(<ServerUnavailablePage reload={reload} />).find(
      'ServerUnavailablePage'
    );

    expect(wrapper).toMatchSnapshot();

    const emptystates = wrapper.find(EmptyStateBody);

    expect(emptystates).toHaveLength(2);
    expect(emptystates.first().text()).toContain(
      `The ${process.env.KOGITO_APP_NAME} could not access the server to display content.`
    );

    act(() => {
      const reset = wrapper.find(Button);
      reset.simulate('click');
    });

    expect(reload).toHaveBeenCalled();
  });

  it('Snapshot with custom name', () => {
    const customDisplayName: string = 'My custom display Name';

    const wrapper = mount(
      <ServerUnavailablePage displayName={customDisplayName} reload={reload} />
    ).find('ServerUnavailablePage');

    expect(wrapper).toMatchSnapshot();

    const emptystates = wrapper.find(EmptyStateBody);

    expect(emptystates).toHaveLength(2);
    expect(emptystates.first().text()).toContain(
      `The ${customDisplayName} could not access the server to display content.`
    );

    act(() => {
      const reset = wrapper.find(Button);
      reset.simulate('click');
    });

    expect(reload).toHaveBeenCalled();
  });
});
