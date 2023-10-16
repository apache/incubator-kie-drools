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
import { render, screen, fireEvent } from '@testing-library/react';
import TaskInboxSwitchUser from '../TaskInboxSwitchUser';
import DevUIAppContextProvider from '../../../../contexts/DevUIAppContextProvider';

describe('TaskInboxSwitchUser tests', () => {
  it('Snapshot test with default props', () => {
    const { container } = render(
      <DevUIAppContextProvider users={[{ id: 'John snow', groups: ['admin'] }]}>
        <TaskInboxSwitchUser user="John" />
      </DevUIAppContextProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('Trigger onSelect event', () => {
    const { container } = render(
      <DevUIAppContextProvider users={[{ id: 'John snow', groups: ['admin'] }]}>
        <TaskInboxSwitchUser user="John" />
      </DevUIAppContextProvider>
    );

    const checkButton = screen.getByLabelText('Applications');
    fireEvent.click(checkButton);

    const checkDropdownText = container.querySelector('a').textContent;
    expect(checkDropdownText).toEqual('John snow');
    fireEvent.click(container.querySelector('a'));
  });
});
