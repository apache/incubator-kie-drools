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
import { act } from 'react-dom/test-utils';
import { getWrapper } from '@kogito-apps/components-common';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessDetailsEnvelopeView, {
  ProcessDetailsEnvelopeViewApi
} from '../ProcessDetailsEnvelopeView';
import ProcessDetails from '../components/ProcessDetails/ProcessDetails';

jest.mock('../components/ProcessDetails/ProcessDetails');

describe('ProcessDetailsEnvelopeView tests', () => {
  it('Snapshot', () => {
    const initArg = { processId: 'a1e139d5-4e77-48c9-84ae-34578e904e5a' };
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<ProcessDetailsEnvelopeViewApi>();

    let wrapper = getWrapper(
      <ProcessDetailsEnvelopeView channelApi={channelApi} ref={forwardRef} />,
      'ProcessDetailsEnvelopeView'
    );

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize(initArg.processId);
      }
    });

    wrapper = wrapper.update().find(ProcessDetailsEnvelopeView);

    expect(wrapper).toMatchSnapshot();

    const ProcessDetailsWrapper = wrapper.find(ProcessDetails);

    expect(ProcessDetailsWrapper.exists()).toBeTruthy();
    expect(
      ProcessDetailsWrapper.props().isEnvelopeConnectedToChannel
    ).toBeTruthy();
    expect(ProcessDetailsWrapper.props().driver).not.toBeNull();
  });
});
