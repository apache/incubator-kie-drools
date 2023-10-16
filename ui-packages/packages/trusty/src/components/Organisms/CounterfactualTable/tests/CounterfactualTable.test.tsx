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
import { mount } from 'enzyme';
import CounterfactualTable from '../CounterfactualTable';
import {
  CFAnalysisResult,
  CFExecutionStatus,
  CFSearchInput,
  CFSearchInputUnit
} from '../../../../types';

describe('CounterfactualTable', () => {
  it('renders the table with CF initial state', () => {
    const wrapper = mount(
      <CounterfactualTable
        inputs={inputs}
        results={[]}
        status={{
          isDisabled: true,
          executionStatus: CFExecutionStatus.NOT_STARTED,
          lastExecutionTime: null
        }}
        containerWidth={900}
        onOpenInputDomainEdit={jest.fn()}
      />
    );

    const rows = wrapper.find(
      'tbody tr[data-ouia-component-type="PF4/TableRow"]'
    );
    expect(rows).toHaveLength(4);

    for (let i = 0; i < inputs.length; i++) {
      expect(rows.at(i).find('[data-label="Input"]').text()).toMatch(
        inputs[i].name
      );
      expect(rows.at(i).find('[data-label="Input Value"]').text()).toMatch(
        (inputs[i].value as CFSearchInputUnit).originalValue.value.toString()
      );
      expect(
        rows.at(i).find('[data-label="Counterfactual Result"]').text()
      ).toMatch('No available results');
    }

    expect(rows.at(0).find('[data-label="Constraint"]').text()).toMatch('');
    expect(
      rows.at(0).find('button.counterfactual-constraint-edit')
    ).toHaveLength(0);
    expect(
      rows.at(1).find('button.counterfactual-constraint-edit')
    ).toHaveLength(1);
    expect(
      rows.at(2).find('button.counterfactual-constraint-edit')
    ).toHaveLength(1);
    expect(
      rows.at(3).find('button.counterfactual-constraint-edit')
    ).toHaveLength(0);
    expect(rows.at(3).find('[data-label="Constraint"]').text()).toMatch(
      'Not yet supported'
    );
  });

  it('renders the table with CF results', () => {
    const wrapper = mount(
      <CounterfactualTable
        inputs={inputsWithSelection}
        results={results}
        status={{
          isDisabled: false,
          executionStatus: CFExecutionStatus.COMPLETED,
          lastExecutionTime: null
        }}
        containerWidth={900}
        onOpenInputDomainEdit={jest.fn()}
      />
    );

    const rows = wrapper.find(
      'tbody tr[data-ouia-component-type="PF4/TableRow"]'
    );

    for (let i = 0; i < results.length; i++) {
      expect(
        rows.at(0).find('[data-label="Counterfactual Result"]').at(i).text()
      ).toMatch(`ID #${results[i].solutionId}`);
    }

    expect(rows.at(1).text()).toMatch('');
    expect(rows.at(2).text()).toMatch('50-500');
    expect(rows.at(3).text()).toMatch('');
    expect(rows.at(4).text()).toMatch('Not yet supported');

    for (let i = 1; i <= inputsWithSelection.length; i++) {
      for (let j = 0; j < results.length; j++) {
        expect(
          rows.at(i).find('[data-label="Counterfactual Result"]').at(j).text()
        ).toMatch(results[j].inputs[i - 1].value.value.toString());
      }
    }

    expect(rows.at(1).find('td.cf-table__result-value--changed')).toHaveLength(
      2
    );
    expect(rows.at(2).find('td.cf-table__result-value--changed')).toHaveLength(
      2
    );
    expect(rows.at(3).find('td.cf-table__result-value--changed')).toHaveLength(
      0
    );
    expect(rows.at(4).find('td.cf-table__result-value--changed')).toHaveLength(
      0
    );
  });
});

const inputs: CFSearchInput[] = [
  {
    name: 'Prior refusal?',
    value: {
      kind: 'UNIT',
      type: 'boolean',
      originalValue: {
        kind: 'UNIT',
        type: 'boolean',
        value: false
      },
      fixed: true
    }
  },
  {
    name: 'Credit Score',
    value: {
      kind: 'UNIT',
      type: 'number',
      originalValue: {
        kind: 'UNIT',
        type: 'number',
        value: 738
      },
      fixed: true
    }
  },
  {
    name: 'Down Payment',
    value: {
      kind: 'UNIT',
      type: 'number',
      originalValue: {
        kind: 'UNIT',
        type: 'number',
        value: 70000
      },
      fixed: true
    }
  },
  {
    name: 'Favorite cheese',
    value: {
      kind: 'UNIT',
      type: 'string',
      originalValue: {
        kind: 'UNIT',
        type: 'string',
        value: 'Cheddar'
      }
    }
  }
];

const inputsWithSelection = [...inputs];
inputsWithSelection[0] = {
  ...inputsWithSelection[0],
  value: {
    ...(inputsWithSelection[0].value as CFSearchInputUnit),
    fixed: false
  }
};
inputsWithSelection[1] = {
  ...inputsWithSelection[1],
  value: {
    ...(inputsWithSelection[1].value as CFSearchInputUnit),
    fixed: false,
    domain: {
      type: 'RANGE',
      lowerBound: 50,
      upperBound: 500
    }
  }
};

const results: CFAnalysisResult[] = [
  {
    type: 'counterfactual',
    valid: true,
    executionId: 'ac6d2f5f-4eba-4557-9d78-22b1661a876a',
    sequenceId: 20,
    status: 'SUCCEEDED',
    statusDetails: '',
    counterfactualId: 'counterfactualId',
    solutionId: '1034',
    isValid: true,
    stage: 'FINAL',
    inputs: [
      {
        name: 'Prior refusal?',
        value: {
          kind: 'UNIT',
          type: 'boolean',
          value: true
        }
      },
      {
        name: 'Credit Score',
        value: {
          kind: 'UNIT',
          type: 'number',
          value: 698
        }
      },
      {
        name: 'Down Payment',
        value: {
          kind: 'UNIT',
          type: 'number',
          value: 70000
        }
      },
      {
        name: 'Favorite cheese',
        value: {
          kind: 'UNIT',
          type: 'string',
          value: 'Cheddar'
        }
      }
    ],
    outputs: []
  },
  {
    type: 'counterfactual',
    valid: true,
    executionId: 'ac6d2f5f-4eba-4557-9d78-22b1661a876a',
    sequenceId: 10,
    status: 'SUCCEEDED',
    statusDetails: '',
    counterfactualId: 'counterfactualId',
    solutionId: '1033',
    isValid: true,
    stage: 'INTERMEDIATE',
    inputs: [
      {
        name: 'Prior refusal?',
        value: {
          kind: 'UNIT',
          type: 'boolean',
          value: true
        }
      },
      {
        name: 'Credit Score',
        value: {
          kind: 'UNIT',
          type: 'number',
          value: 764
        }
      },
      {
        name: 'Down Payment',
        value: {
          kind: 'UNIT',
          type: 'number',
          value: 70000
        }
      },
      {
        name: 'Favorite cheese',
        value: {
          kind: 'UNIT',
          type: 'string',
          value: 'Cheddar'
        }
      }
    ],
    outputs: []
  }
];
