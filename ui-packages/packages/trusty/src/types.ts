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
export interface TrustyContextValue {
  config: {
    counterfactualEnabled: boolean;
    explanationEnabled: boolean;
    serverRoot: string;
    basePath: string;
    useHrefLinks: boolean;
  };
}

export type RemoteData<E, D> =
  | { status: RemoteDataStatus.NOT_ASKED }
  | { status: RemoteDataStatus.LOADING }
  | { status: RemoteDataStatus.FAILURE; error: E }
  | { status: RemoteDataStatus.SUCCESS; data: D };

export enum RemoteDataStatus {
  NOT_ASKED,
  LOADING,
  FAILURE,
  SUCCESS
}

export interface Execution {
  executionId: string;
  executionDate: string;
  executedModelName: string;
  executionType: string;
  executionSucceeded: boolean;
  executorName: string;
}

export interface Executions {
  headers: Execution[];
  total: number;
}

export interface ExecutionRouteParams {
  executionId: string;
  executionType: string;
}

export interface ItemObject {
  name: string;
  value: ItemObjectValue;
}

export type ItemObjectValue =
  | ItemObjectUnit
  | ItemObjectCollection
  | ItemObjectStructure;

export interface ItemObjectUnit {
  kind: 'UNIT';
  type: string;
  value: string | number | boolean | Array<string | number | boolean> | null;
}

export interface ItemObjectCollection {
  kind: 'COLLECTION';
  type: string;
  value: ItemObjectValue[] | null;
}

export type ItemObjectMap = { [key: string]: ItemObjectValue };

export interface ItemObjectStructure {
  kind: 'STRUCTURE';
  type: string;
  value: ItemObjectMap;
}

export interface InputRow {
  inputLabel: string;
  inputValue?: ItemObject['value'];
  key: string;
  category: string;
}

export enum evaluationStatus {
  EVALUATING = 'Evaluating',
  FAILED = 'Failed',
  NOT_EVALUATED = 'Not evaluated',
  SKIPPED = 'Skipped',
  SUCCEEDED = 'Succeeded'
}

export type evaluationStatusStrings = keyof typeof evaluationStatus;

export interface Outcome {
  outcomeId: string;
  outcomeName: string;
  outcomeResult: ItemObjectValue;
  evaluationStatus: evaluationStatusStrings;
  hasErrors: boolean;
  messages: string[];
}

export interface FeatureScores {
  featureName: string;
  featureScore: number;
}

export interface Saliency {
  outcomeId: string;
  featureImportance: FeatureScores[];
}

export enum SaliencyStatus {
  SUCCEEDED = 'SUCCEEDED',
  FAILED = 'FAILED'
}

export type SaliencyStatusStrings = keyof typeof SaliencyStatus;

export interface Saliencies {
  status: SaliencyStatusStrings;
  statusDetail: string;
  saliencies: Saliency[];
}

export interface ServiceIdentifier {
  groupId?: string;
  artifactId?: string;
  version?: string;
}

export interface ModelData {
  deploymentDate?: string;
  modelId?: string;
  name: string;
  namespace: string;
  modelVersion: string;
  dmnVersion: string;
  serviceIdentifier: ServiceIdentifier;
  model: string;
}

export interface CFSearchInput {
  name: string;
  value: CFSearchInputValue;
}

export type CFSearchInputValue =
  | CFSearchInputUnit
  | CFSearchInputCollection
  | CFSearchInputStructure;

export interface CFSearchInputUnit {
  kind: 'UNIT';
  type: string;
  fixed?: boolean;
  domain?: CFNumericalDomain | CFCategoricalDomain;
  originalValue: ItemObject['value'];
}

export interface CFNumericalDomain {
  type: 'RANGE';
  lowerBound?: number;
  upperBound?: number;
}

export interface CFCategoricalDomain {
  type: 'CATEGORICAL';
  categories: string[];
}

export interface CFSearchInputCollection {
  kind: 'COLLECTION';
  type: string;
  value: Array<CFSearchInputValue> | null;
}

export type CFSearchInputValueMap = { [key: string]: CFSearchInputValue };

export interface CFSearchInputStructure {
  kind: 'STRUCTURE';
  type: string;
  value: CFSearchInputValueMap | null;
}

export enum CFGoalRole {
  UNSUPPORTED,
  ORIGINAL,
  FIXED,
  FLOATING
}

export interface CFGoal {
  id: string;
  name: string;
  role: CFGoalRole;
  value: ItemObjectValue;
  originalValue: CFGoal['value'];
}

export type CFResult = Array<unknown>;

export interface CFStatus {
  isDisabled: boolean;
  executionStatus: CFExecutionStatus;
  lastExecutionTime: null | string;
}

export enum CFExecutionStatus {
  COMPLETED,
  RUNNING,
  NOT_STARTED,
  FAILED,
  NO_RESULTS
}

export type CFAnalysisResetType = 'NEW' | 'EDIT';

export interface CFAnalysisExecution {
  executionId: string;
  counterfactualId: string;
  maxRunningTimeSeconds: number;
}

export interface CFAnalysisResult {
  executionId: string;
  counterfactualId: string;
  type: 'counterfactual';
  valid: boolean;
  status: 'SUCCEEDED' | 'FAILED';
  statusDetails: string;
  solutionId: string;
  isValid: boolean;
  stage: 'INTERMEDIATE' | 'FINAL';
  inputs: ItemObject[];
  outputs: ItemObject[];
  sequenceId: number;
}

export interface CFAnalysisResultsSets extends CFAnalysisExecution {
  goals: CFGoal[];
  searchDomains: CFSearchInput[];
  solutions: CFAnalysisResult[];
}

export type CFSupportMessage = {
  id: string;
  message: string;
};
