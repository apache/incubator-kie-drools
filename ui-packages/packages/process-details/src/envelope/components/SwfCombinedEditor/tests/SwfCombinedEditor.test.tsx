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

import React from 'react';
import { render } from '@testing-library/react';
import SwfCombinedEditor from '../SwfCombinedEditor';

describe('SwfCombinedEditor component tests', () => {
  it('snapshot of the emedded editor json', () => {
    const props = {
      workflowInstance: {
        source:
          '{\n  "id": "hello_world",\n  "version": "1.0",\n  "specVersion": "0.8",\n  "name": "Hello World Workflow",\n  "description": "JSON based hello world workflow",\n  "start": "Inject Hello World",\n  "states": [\n    {\n      "name": "Inject Hello World",\n      "type": "inject",\n      "data": {\n        "greeting": "Hello World"\n      },\n      "transition": "Inject Mantra"\n    },\n    {\n      "name": "Inject Mantra",\n      "type": "inject",\n      "data": {\n        "mantra": "Serverless Workflow is awesome!"\n      },\n      "end": true\n    }\n  ]\n}',
        error: {
          nodeDefinitionId: '2',
          message: 'some thing went wrong'
        },
        nodes: [
          {
            nodeId: '1',
            name: 'Start 1',
            definitionId: 'StartEvent_1',
            id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
            enter: new Date('2019-10-22T03:37:30.798Z'),
            exit: new Date('2019-10-22T03:37:30.798Z'),
            type: 'StartNode'
          },
          {
            nodeId: '2',
            name: 'Some Event 1',
            definitionId: 'SomeEvent_1',
            id: '27107f38-d888-4edf-9a4f-11b9e6d751b6',
            enter: new Date('2019-10-22T03:37:30.798Z'),
            exit: new Date('2019-10-22T03:37:30.798Z'),
            type: 'SomeNode'
          }
        ]
      },
      height: 600,
      width: 600,
      isStunnerEnabled: true
    };
    const container = render(<SwfCombinedEditor {...props} />).container;
    expect(container).toMatchSnapshot();
  });

  it('snapshot of the emedded editor yaml', () => {
    const props = {
      workflowInstance: {
        source:
          "---\nid: hello_world\nversion: '1.0'\nspecVersion: '0.8'\nname: Hello World Workflow\ndescription: JSON based hello world workflow\nstart: Inject Hello World\nstates:\n- name: Inject Hello World\n  type: inject\n  data:\n    greeting: Hello World\n  transition: Inject Mantra\n- name: Inject Mantra\n  type: inject\n  data:\n    mantra: Serverless Workflow is awesome!\n  end: true\n",
        error: undefined,
        nodes: []
      },
      height: 600,
      width: 600,
      isStunnerEnabled: true
    };
    const container = render(<SwfCombinedEditor {...props} />).container;
    expect(container).toMatchSnapshot();
  });
});
