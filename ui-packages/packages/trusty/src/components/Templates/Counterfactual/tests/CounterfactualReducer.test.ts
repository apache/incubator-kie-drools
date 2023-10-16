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
import { cfInitState, cfReducer, CFState } from '../counterfactualReducer';
import {
  CFExecutionStatus,
  CFGoalRole,
  CFSearchInputStructure,
  CFSearchInputUnit,
  ItemObjectUnit
} from '../../../../types';

const initialState: CFState = {
  goals: [
    {
      id: '1',
      name: 'goal1',
      role: CFGoalRole.ORIGINAL,
      value: {
        kind: 'UNIT',
        type: 'string',
        value: null
      },
      originalValue: {
        kind: 'UNIT',
        type: 'string',
        value: 'originalValue'
      }
    }
  ],
  searchDomains: [
    {
      name: 'sd1',
      value: {
        kind: 'UNIT',
        type: 'number',
        fixed: true,
        originalValue: {
          kind: 'UNIT',
          type: 'number',
          value: 123
        }
      }
    }
  ],
  results: [],
  status: {
    executionStatus: CFExecutionStatus.NOT_STARTED,
    isDisabled: true,
    lastExecutionTime: null
  }
};

describe('InitialStateMapping', () => {
  test('SearchDomain::fixed::Number', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 123
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
  });

  test('SearchDomain::fixed::Boolean', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 123
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
  });

  test('SearchDomain::fixed::String', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'UNIT',
            type: 'string',
            value: 'value'
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeUndefined();
  });

  test('SearchDomain::fixed::String::EmptyArrayComponents', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'UNIT',
            type: 'string',
            value: 'value'
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeUndefined();
  });

  test('SearchDomain::fixed::Object', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'STRUCTURE',
            type: 'tStructure',
            value: {
              i2: {
                kind: 'UNIT',
                type: 'string',
                value: 'value'
              }
            }
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeUndefined();
  });

  test('SearchDomain::Object::JSON.stringify', () => {
    const checks = (object) => {
      const i2 = (object.searchDomains[0].value as CFSearchInputStructure)
        .value['i2'];
      expect(i2).not.toBeUndefined();
      expect(i2.type).toEqual('string');
      expect(i2.kind).toEqual('UNIT');

      const i2Unit = i2 as CFSearchInputUnit;
      expect(i2Unit.fixed).toBeUndefined();
      expect(i2Unit.domain).toBeUndefined();
      expect(i2Unit.originalValue.type).toEqual('string');
      expect(i2Unit.originalValue.kind).toEqual('UNIT');
      const i2UnitOriginalValue = i2Unit.originalValue as ItemObjectUnit;
      expect(i2UnitOriginalValue.value).toEqual('value');
    };

    // See https://issues.redhat.com/browse/FAI-662. In addition to the NPE in the Java code;
    // Axios HTTPClient was failing to stringify use of ES6 Map object defining the CFSearchInputStructure.
    // This lead to an additional error where the Search Domain structure differed from that for the
    // original inputs and Counterfactual execution was terminated.
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'STRUCTURE',
            type: 'tStructure',
            value: {
              i2: {
                kind: 'UNIT',
                type: 'string',
                value: 'value'
              }
            }
          }
        }
      ],
      outcomes: []
    });
    checks(initialState);

    // Round-trip to string and object to check it can be stringified correctly.
    const json = JSON.stringify(initialState);
    checks(JSON.parse(json));
  });

  test('SearchDomain::fixed::Collection', () => {
    const initialState = cfInitState({
      inputs: [
        {
          name: 'i1',
          value: {
            kind: 'STRUCTURE',
            type: 'tStructure',
            value: {
              i2: {
                kind: 'UNIT',
                type: 'string',
                value: 'value'
              }
            }
          }
        }
      ],
      outcomes: []
    });
    expect(initialState.searchDomains.length).toBe(1);
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeUndefined();
  });
});

describe('State::isDisabled::isolated', () => {
  test('Set Search Domain', () => {
    expect(setSearchDomain(initialState).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::UNSUPPORTED', () => {
    expect(setGoalUnsupported(initialState).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::ORIGINAL', () => {
    expect(setGoalOriginal(initialState).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::FIXED', () => {
    expect(setGoalFixed(initialState).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::FLOATING', () => {
    expect(setGoalFloating(initialState).status.isDisabled).toBeTruthy();
  });
});

describe('State::isDisabled::SearchDomain_Then_Goal', () => {
  test('Set Goal::UNSUPPORTED', () => {
    const state = setSearchDomain(initialState);
    expect(setGoalUnsupported(state).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::ORIGINAL', () => {
    const state = setSearchDomain(initialState);
    expect(setGoalOriginal(state).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::FIXED', () => {
    const state = setSearchDomain(initialState);
    expect(setGoalFixed(state).status.isDisabled).toBeFalsy();
  });

  test('Set Goal::FLOATING', () => {
    const state = setSearchDomain(initialState);
    expect(setGoalFloating(state).status.isDisabled).toBeFalsy();
  });
});

describe('State::isDisabled::Goal_Then_SearchDomain', () => {
  test('Set Goal::UNSUPPORTED', () => {
    const state = setGoalUnsupported(initialState);
    expect(setSearchDomain(state).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::ORIGINAL', () => {
    const state = setGoalOriginal(initialState);
    expect(setSearchDomain(state).status.isDisabled).toBeTruthy();
  });

  test('Set Goal::FIXED', () => {
    const state = setGoalFixed(initialState);
    expect(setSearchDomain(state).status.isDisabled).toBeFalsy();
  });

  test('Set Goal::FLOATING', () => {
    const state = setGoalFloating(initialState);
    expect(setSearchDomain(state).status.isDisabled).toBeFalsy();
  });
});

describe('ToggleAll', () => {
  test('InitialState::SingleSearchDomain', () => {
    expect(
      (initialState.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    const state1 = toggleSearchDomains(initialState, true);
    expect(
      (state1.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    const state2 = toggleSearchDomains(state1, false);
    expect(
      (state2.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
  });

  test('InitialState::MultipleSearchDomains', () => {
    const state1 = {
      ...initialState,
      searchDomains: [
        {
          name: 'sd1',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 123,
            fixed: true,
            originalValue: {
              kind: 'UNIT',
              type: 'number',
              value: 123
            }
          }
        },
        {
          name: 'sd2',
          value: {
            kind: 'UNIT',
            type: 'boolean',
            value: true,
            fixed: true,
            originalValue: {
              kind: 'UNIT',
              type: 'boolean',
              value: true
            }
          }
        }
      ]
    };
    expect(
      (state1.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    expect(
      (state1.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    const state2 = toggleSearchDomains(state1, true);
    expect(
      (state2.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    expect(
      (state2.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    const state3 = toggleSearchDomains(state2, false);
    expect(
      (state3.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    expect(
      (state3.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
  });

  test('InitialState::MultipleSearchDomains::WithUnsupportedType', () => {
    const state1 = {
      ...initialState,
      searchDomains: [
        {
          name: 'sd1',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 123,
            fixed: true,
            originalValue: {
              kind: 'UNIT',
              type: 'number',
              value: 123
            }
          }
        },
        {
          name: 'sd2',
          value: {
            kind: 'STRUCTURE',
            type: 'tObject',
            value: {}
          }
        }
      ]
    };
    expect(
      (state1.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    expect(
      (state1.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    const state2 = toggleSearchDomains(state1, true);
    expect(
      (state2.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    expect(
      (state2.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
    const state3 = toggleSearchDomains(state2, false);
    expect(
      (state3.searchDomains[0].value as CFSearchInputUnit).fixed
    ).toBeTruthy();
    expect(
      (state3.searchDomains[1].value as CFSearchInputUnit).fixed
    ).toBeFalsy();
  });
});

const setSearchDomain = (state) => {
  return setSearchDomainWithIndex(state, 0);
};

const setSearchDomainWithIndex = (state, searchInputIndex) => {
  //We need to first enable the Search Domain (i.e. checked in the UI)
  const enableSearchDomain = cfReducer(state, {
    type: 'CF_TOGGLE_INPUT',
    payload: { searchInputIndex: searchInputIndex }
  });
  //We can then set the values of the Search Domain
  return cfReducer(enableSearchDomain, {
    type: 'CF_SET_INPUT_DOMAIN',
    payload: {
      inputIndex: 0,
      domain: { type: 'RANGE', lowerBound: 1, upperBound: 2 }
    }
  });
};

const setGoalUnsupported = (state) => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        role: CFGoalRole.UNSUPPORTED,
        value: {
          kind: 'STRUCTURE',
          type: 'tStructure',
          value: {}
        },
        originalValue: {
          kind: 'STRUCTURE',
          type: 'tStructure',
          value: {}
        }
      }
    ]
  });
};

const setGoalOriginal = (state) => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        role: CFGoalRole.ORIGINAL,
        value: {
          kind: 'UNIT',
          type: 'string',
          value: 'originalValue'
        },
        originalValue: {
          kind: 'UNIT',
          type: 'string',
          value: 'originalValue'
        }
      }
    ]
  });
};

const setGoalFloating = (state) => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        role: CFGoalRole.FLOATING,
        value: {
          kind: 'UNIT',
          type: 'string',
          value: 'originalValue'
        },
        originalValue: {
          kind: 'UNIT',
          type: 'string',
          value: 'originalValue'
        }
      }
    ]
  });
};

const setGoalFixed = (state) => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        role: CFGoalRole.FIXED,
        value: {
          kind: 'UNIT',
          type: 'string',
          value: 'value'
        },
        originalValue: {
          kind: 'UNIT',
          type: 'string',
          value: 'value'
        }
      }
    ]
  });
};

const toggleSearchDomains = (state, selected) => {
  return cfReducer(state, {
    type: 'CF_TOGGLE_ALL_INPUTS',
    payload: { selected: selected }
  });
};
