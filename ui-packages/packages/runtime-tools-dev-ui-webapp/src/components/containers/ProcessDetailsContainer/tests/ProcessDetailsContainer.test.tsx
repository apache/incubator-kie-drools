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
import { mount } from 'enzyme';
import { ProcessInstance } from '@kogito-apps/management-console-shared';
import ProcessDetailsContainer from '../ProcessDetailsContainer';
import * as ProcessDetailsContext from '../../../../channel/ProcessDetails/ProcessDetailsContext';
import { ProcessDetailsGatewayApiImpl } from '../../../../channel/ProcessDetails/ProcessDetailsGatewayApi';
import { ProcessDetailsQueries } from '../../../../channel/ProcessDetails/ProcessDetailsQueries';
import * as RuntimeToolsDevUIAppContext from '../../../contexts/DevUIAppContext';

const getJobsMock = jest.fn();
const getProcessDetailsMock = jest.fn();
const handleProcessSkipMock = jest.fn();
const handleProcessAbortMock = jest.fn();
const handleProcessRetryMock = jest.fn();
const getSVGMock = jest.fn();
const jobCancelMock = jest.fn();
const rescheduleJobMock = jest.fn();
const getTriggerableNodesMock = jest.fn();
const handleNodeTriggerMock = jest.fn();
const handleProcessVariableUpdateMock = jest.fn();
const handleNodeInstanceCancelMock = jest.fn();
const handleNodeInstanceRetriggerMock = jest.fn();

const MockQueries = jest.fn<ProcessDetailsQueries, []>(() => ({
  getProcessDetails: getProcessDetailsMock,
  getJobs: getJobsMock,
  handleProcessSkip: handleProcessSkipMock,
  handleProcessAbort: handleProcessAbortMock,
  handleProcessRetry: handleProcessRetryMock,
  getSVG: getSVGMock,
  jobCancel: jobCancelMock,
  rescheduleJob: rescheduleJobMock,
  getTriggerableNodes: getTriggerableNodesMock,
  handleNodeTrigger: handleNodeTriggerMock,
  handleProcessVariableUpdate: handleProcessVariableUpdateMock,
  handleNodeInstanceCancel: handleNodeInstanceCancelMock,
  handleNodeInstanceRetrigger: handleNodeInstanceRetriggerMock
}));

jest
  .spyOn(ProcessDetailsContext, 'useProcessDetailsGatewayApi')
  .mockImplementation(
    () => new ProcessDetailsGatewayApiImpl(new MockQueries())
  );

  jest
  .spyOn(RuntimeToolsDevUIAppContext, 'useDevUIAppContext')
  .mockImplementation(() => {
    return {
      isWorkflow:jest.fn(),
      getIsStunnerEnabled: jest.fn()
    };
  });

const processInstance: ProcessInstance = {} as ProcessInstance;

describe('WebApp - ProcessDetailsContainer tests', () => {
  it('Snapshot test with default values', () => {
    const wrapper = mount(
      <ProcessDetailsContainer processInstance={processInstance} />
    );
    expect(wrapper).toMatchSnapshot();
  });
});
