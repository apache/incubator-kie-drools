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
import { EmbeddedProcessList } from '../EmbeddedProcessList';
import { MockedProcessListDriver } from './utils/Mocks';
import { render } from '@testing-library/react';
import {
  ProcessInstanceState,
  OrderBy
} from '@kogito-apps/management-console-shared/dist/types';

describe('EmbeddedProcessList tests', () => {
  it('Snapshot', () => {
    const props = {
      targetOrigin: 'origin',
      envelopePath: 'path',
      driver: new MockedProcessListDriver(),
      initialState: {
        filters: {
          status: [ProcessInstanceState.Active],
          businessKey: []
        },
        sortBy: {
          lastUpdate: OrderBy.DESC
        }
      },
      singularProcessLabel: 'Workflow',
      pluralProcessLabel: 'Workflows',
      isWorkflow: true
    };

    const container = render(<EmbeddedProcessList {...props} />).container;

    expect(container).toMatchSnapshot();
    const contentDiv = container.querySelector('div');
    expect(contentDiv).toBeTruthy();
  });
});
