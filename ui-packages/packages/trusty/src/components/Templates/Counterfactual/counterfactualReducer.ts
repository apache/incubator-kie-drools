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
import {
  CFAnalysisResetType,
  CFAnalysisResult,
  CFExecutionStatus,
  CFGoal,
  CFGoalRole,
  CFSearchInput,
  CFSearchInputUnit,
  CFSearchInputValue,
  CFStatus,
  ItemObject,
  ItemObjectValue,
  Outcome
} from '../../../types';

export interface CFState {
  goals: CFGoal[];
  searchDomains: CFSearchInput[];
  status: CFStatus;
  results: CFAnalysisResult[];
}

export type cfActions =
  | { type: 'CF_SET_OUTCOMES'; payload: CFGoal[] }
  | {
      type: 'CF_TOGGLE_INPUT';
      payload: {
        searchInputIndex: number;
      };
    }
  | {
      type: 'CF_TOGGLE_ALL_INPUTS';
      payload: {
        selected: boolean;
      };
    }
  | {
      type: 'CF_SET_INPUT_DOMAIN';
      payload: {
        inputIndex: number;
        domain: CFSearchInputUnit['domain'];
      };
    }
  | {
      type: 'CF_SET_STATUS';
      payload: Partial<CFStatus>;
    }
  | {
      type: 'CF_SET_RESULTS';
      payload: {
        results: CFAnalysisResult[];
      };
    }
  | {
      type: 'CF_RESET_ANALYSIS';
      payload: {
        resetType: CFAnalysisResetType;
        inputs: ItemObject[];
        outcomes: Outcome[];
      };
    };

export const cfReducer = (state: CFState, action: cfActions): CFState => {
  switch (action.type) {
    case 'CF_SET_OUTCOMES': {
      const newState = { ...state, goals: action.payload };
      return updateCFStatus(newState);
    }

    case 'CF_TOGGLE_INPUT': {
      const newState = {
        ...state,
        searchDomains: state.searchDomains.map((input, index) =>
          index === action.payload.searchInputIndex
            ? {
                ...input,
                value: {
                  ...input.value,
                  fixed: !(input.value as CFSearchInputUnit).fixed
                }
              }
            : input
        )
      };
      return updateCFStatus(newState);
    }

    case 'CF_TOGGLE_ALL_INPUTS': {
      const newState = {
        ...state,
        searchDomains: state.searchDomains.map((input) =>
          isSearchInputTypeSupportedForCounterfactual(input)
            ? {
                ...input,
                value: { ...input.value, fixed: !action.payload.selected }
              }
            : input
        )
      };
      return updateCFStatus(newState);
    }

    case 'CF_SET_INPUT_DOMAIN': {
      const newState = {
        ...state,
        searchDomains: state.searchDomains.map((input, index) =>
          index === action.payload.inputIndex
            ? {
                ...input,
                value: { ...input.value, domain: action.payload.domain }
              }
            : input
        )
      };
      return updateCFStatus(newState);
    }

    case 'CF_SET_RESULTS':
      return {
        ...state,
        results: [...action.payload.results].sort(
          (a, b) => b.sequenceId - a.sequenceId
        )
      };

    case 'CF_SET_STATUS':
      return {
        ...state,
        status: { ...state.status, ...action.payload }
      };

    case 'CF_RESET_ANALYSIS':
      switch (action.payload.resetType) {
        case 'NEW':
          return cfInitState({
            inputs: action.payload.inputs,
            outcomes: action.payload.outcomes
          });
        case 'EDIT':
          return {
            ...state,
            status: {
              isDisabled: false,
              executionStatus: CFExecutionStatus.NOT_STARTED,
              lastExecutionTime: null
            },
            results: []
          };
      }
      break;
    default:
      throw new Error();
  }
};

export const cfInitState = (parameters: {
  inputs: ItemObject[];
  outcomes: Outcome[];
}): CFState => {
  const { inputs, outcomes } = parameters;
  const initialState: CFState = {
    goals: [],
    searchDomains: [],
    status: {
      isDisabled: true,
      executionStatus: CFExecutionStatus.NOT_STARTED,
      lastExecutionTime: null
    },
    results: []
  };
  initialState.goals = convertOutcomesToGoals(outcomes);
  initialState.searchDomains = convertInputToSearchDomain(inputs);

  return initialState;
};

const convertOutcomesToGoals = (outcomes: Outcome[]): CFGoal[] => {
  return outcomes
    .filter((outcome) => outcome.evaluationStatus === 'SUCCEEDED')
    .map((outcome) => {
      return {
        id: outcome.outcomeId,
        name: outcome.outcomeName,
        role: isOutcomeTypeSupported(outcome)
          ? CFGoalRole.ORIGINAL
          : CFGoalRole.UNSUPPORTED,
        value: outcome.outcomeResult,
        originalValue: outcome.outcomeResult
      };
    });
};

const convertInputToSearchDomain = (inputs: ItemObject[]): CFSearchInput[] => {
  const addIsFixed = (input: ItemObject): CFSearchInput => {
    const cfSearchInput = {
      name: input.name,
      value: { ...convertInputToSearchDomainValue(input.value) }
    };
    if (
      cfSearchInput.value.kind === 'UNIT' &&
      isInputTypeSupportedForCounterfactual(input)
    ) {
      return {
        ...cfSearchInput,
        value: { ...cfSearchInput.value, fixed: true }
      };
    }
    return cfSearchInput;
  };
  return inputs.map((input) => {
    return addIsFixed(input);
  });
};

const convertInputToSearchDomainValue = (
  value: ItemObjectValue
): CFSearchInputValue => {
  const map = {};
  switch (value.kind) {
    case 'UNIT':
      return {
        kind: 'UNIT',
        type: value.type,
        originalValue: value
      };
    case 'COLLECTION':
      return {
        kind: 'COLLECTION',
        type: value.type,
        value: value.value.map((v) => convertInputToSearchDomainValue(v))
      };
    case 'STRUCTURE':
      Object.entries(value.value).forEach(([key, value]) => {
        map[key] = convertInputToSearchDomainValue(value);
      });

      return {
        kind: 'STRUCTURE',
        type: value.type,
        value: map
      };
  }
};

const updateCFStatus = (state: CFState): CFState => {
  if (areRequiredParametersSet(state)) {
    if (state.status.isDisabled) {
      return { ...state, status: { ...state.status, isDisabled: false } };
    }
  } else {
    if (!state.status.isDisabled) {
      return { ...state, status: { ...state.status, isDisabled: true } };
    }
  }
  return state;
};

const areRequiredParametersSet = (state: CFState): boolean => {
  return (
    areInputsSelected(state.searchDomains) && areGoalsSelected(state.goals)
  );
};

const areInputsSelected = (inputs: CFSearchInput[]) => {
  // filtering all non fixed inputs
  const selectedInputValues: CFSearchInputUnit[] = inputs
    .filter((input) => input.value.kind === 'UNIT')
    .map((input) => input.value as CFSearchInputUnit)
    .filter((input) => input.fixed === false);
  // checking if all inputs have a domain specified, with the exception of
  // booleans (do not require one)
  return (
    selectedInputValues.length > 0 &&
    selectedInputValues.every(
      (inputValue) =>
        inputValue.domain || typeof inputValue.originalValue.value === 'boolean'
    )
  );
};

const areGoalsSelected = (goals: CFGoal[]) => {
  return (
    goals.filter(
      (goal) =>
        !(
          goal.role == CFGoalRole.ORIGINAL ||
          goal.role == CFGoalRole.UNSUPPORTED
        )
    ).length > 0
  );
};

export const isInputTypeSupportedForCounterfactual = (
  input: ItemObject
): boolean => {
  //Structures, Collections and Strings are not supported
  if (input.value.kind === 'UNIT') {
    switch (typeof input.value.value) {
      case 'boolean':
      case 'number':
        return true;
    }
  }
  return false;
};

export const isSearchInputTypeSupportedForCounterfactual = (
  searchInput: CFSearchInput
): boolean => {
  //Structures, Collections and Strings are not supported
  if (searchInput.value.kind === 'UNIT') {
    if (searchInput.value.originalValue.kind === 'UNIT') {
      switch (typeof searchInput.value.originalValue.value) {
        case 'boolean':
        case 'number':
          return true;
      }
    }
  }
  return false;
};

export const isInputConstraintSupported = (
  searchInput: CFSearchInput
): boolean => {
  //Structures, Collections and Strings are not supported. Constraints selection
  //not allowed for Booleans.
  if (searchInput.value.kind === 'UNIT') {
    switch (typeof searchInput.value.originalValue.value) {
      case 'number':
        return true;
    }
  }
  return false;
};

export const isOutcomeTypeSupported = (outcome: Outcome): boolean => {
  //Structures and Collections are not supported
  if (outcome.outcomeResult.kind === 'UNIT') {
    switch (typeof outcome.outcomeResult.value) {
      case 'boolean':
      case 'number':
      case 'string':
        return true;
    }
  }
  return false;
};
