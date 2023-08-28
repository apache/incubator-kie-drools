/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { render } from '@testing-library/react';
import React from 'react';
import {
  EmbeddedWorkflowForm,
  EmbeddedWorkflowFormProps
} from '../EmbeddedWorkflowForm';
import { MockedWorkflowFormDriver } from './mocks/Mocks';

describe('EmbeddedWorkflowForm tests', () => {
  it('Snapshot', () => {
    const props: EmbeddedWorkflowFormProps = {
      driver: new MockedWorkflowFormDriver(),
      workflowDefinition: null,
      targetOrigin: 'origin'
    };

    const container = render(<EmbeddedWorkflowForm {...props} />).container;

    expect(container).toMatchSnapshot();

    const contentDiv = container.querySelector('div');

    expect(contentDiv).toBeTruthy();
  });
});
