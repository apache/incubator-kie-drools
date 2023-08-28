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
import { render } from '@testing-library/react';
import { MockedMessageBusClientApi } from './mocks/Mocks';
import ProcessListEnvelopeView, {
  ProcessListEnvelopeViewApi
} from '../ProcessListEnvelopeView';

describe('ProcessListEnvelopeView tests', () => {
  it('Snapshot', async () => {
    const channelApi = new MockedMessageBusClientApi();

    const forwardRef = React.createRef<ProcessListEnvelopeViewApi>();

    const container = render(
      <ProcessListEnvelopeView channelApi={channelApi} ref={forwardRef} />
    );
    await act(async () => {
      if (forwardRef.current) {
        forwardRef.current.initialize({
          initialState: {
            filters: {
              status: []
            },
            sortBy: {}
          },
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows',
          isWorkflow: true
        });
      }
    });
    expect(container).toMatchSnapshot();
  });
});
