import { cfReducer, CFState } from '../counterfactualReducer';
import { CFExecutionStatus, CFGoalRole } from '../../../../types';

const initialState: CFState = {
  goals: [
    {
      id: '1',
      name: 'goal1',
      kind: 'UNIT',
      typeRef: 'string',
      role: CFGoalRole.ORIGINAL,
      value: null,
      originalValue: 'originalValue'
    }
  ],
  searchDomains: [
    {
      name: 'sd1',
      kind: 'UNIT',
      typeRef: 'number',
      value: null,
      components: null,
      fixed: true
    }
  ],
  results: [],
  status: {
    executionStatus: CFExecutionStatus.NOT_STARTED,
    isDisabled: true,
    lastExecutionTime: null
  }
};

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

const setSearchDomain = state => {
  //We need to first enable the Search Domain (i.e. checked in the UI)
  const enableSearchDomain = cfReducer(state, {
    type: 'CF_TOGGLE_INPUT',
    payload: { searchInputIndex: 0 }
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

const setGoalUnsupported = state => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        kind: 'STRUCTURE',
        role: CFGoalRole.UNSUPPORTED,
        typeRef: 'string',
        value: 'value',
        originalValue: 'originalValue'
      }
    ]
  });
};

const setGoalOriginal = state => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        kind: 'UNIT',
        role: CFGoalRole.ORIGINAL,
        typeRef: 'string',
        value: 'originalValue',
        originalValue: 'originalValue'
      }
    ]
  });
};

const setGoalFloating = state => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        kind: 'UNIT',
        role: CFGoalRole.FLOATING,
        typeRef: 'string',
        value: 'originalValue',
        originalValue: 'originalValue'
      }
    ]
  });
};

const setGoalFixed = state => {
  return cfReducer(state, {
    type: 'CF_SET_OUTCOMES',
    payload: [
      {
        id: '1',
        name: 'goal1',
        kind: 'UNIT',
        role: CFGoalRole.FIXED,
        typeRef: 'string',
        value: 'value',
        originalValue: 'originalValue'
      }
    ]
  });
};
