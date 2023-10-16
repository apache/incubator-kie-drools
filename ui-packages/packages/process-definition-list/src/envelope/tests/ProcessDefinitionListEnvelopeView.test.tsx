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
import { act } from 'react-dom/test-utils';
import { render } from '@testing-library/react';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessDefinitionListEnvelopeView, {
  ProcessDefinitionListEnvelopeViewApi
} from '../ProcessDefinitionListEnvelopeView';

describe('ProcessDefinitionListEnvelopeView tests', () => {
  it('Snapshot', () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<ProcessDefinitionListEnvelopeViewApi>();

    const container = render(
      <ProcessDefinitionListEnvelopeView
        channelApi={channelApi}
        ref={forwardRef}
      />
    ).container;

    expect(container).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.initialize({ singularProcessLabel: 'Workflow' });
      }
    });

    const processDefinitionList = container.querySelector(
      '[data-ouia-component-type="process-definition-list"]'
    );

    expect(processDefinitionList).toBeTruthy();
    const checkIsEnvelopeConnectedToChannel = container.querySelector('h3');

    expect(checkIsEnvelopeConnectedToChannel?.textContent).toEqual(
      'Loading  definitions...'
    );
  });
});
