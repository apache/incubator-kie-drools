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
import React, { useEffect, useMemo, useReducer, useState } from 'react';
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  Flex,
  FlexItem,
  PageSection,
  Stack,
  StackItem,
  Text,
  TextContent,
  TextVariants,
  Title
} from '@patternfly/react-core';
import { OutlinedMehIcon } from '@patternfly/react-icons';
import {
  cfActions,
  cfInitState,
  cfReducer
} from '../../Templates/Counterfactual/counterfactualReducer';
import CounterfactualInputDomainEdit from '../CounterfactualInputDomainEdit/CounterfactualInputDomainEdit';
import CounterfactualOutcomesSelected from '../../Molecules/CounterfactualsOutcomesSelected/CounterfactualOutcomesSelected';
import CounterfactualExecutionInfo from '../../Molecules/CounterfactualExecutionInfo/CounterfactualExecutionInfo';
import CounterfactualCompletedMessage from '../../Molecules/CounterfactualCompletedMessage/CounterfactualCompletedMessage';
import CounterfactualToolbar from '../CounterfactualToolbar/CounterfactualToolbar';
import CounterfactualTable from '../CounterfactualTable/CounterfactualTable';
import useCounterfactualExecution from './useCounterfactualExecution';
import CounterfactualError from '../../Molecules/CounterfactualError/CounterfactualError';
import {
  CFAnalysisResetType,
  CFExecutionStatus,
  CFSearchInput,
  ItemObject,
  Outcome,
  RemoteDataStatus
} from '../../../types';
import './CounterfactualAnalysis.scss';

type CounterfactualAnalysisProps = {
  inputs: ItemObject[];
  outcomes: Outcome[];
  executionId: string;
  containerWidth: number;
  containerHeight: number;
};

const CounterfactualAnalysis = (props: CounterfactualAnalysisProps) => {
  const { executionId, inputs, outcomes, containerWidth, containerHeight } =
    props;
  const [state, dispatch] = useReducer(
    cfReducer,
    { inputs, outcomes },
    cfInitState
  );
  const [isSidePanelExpanded, setIsSidePanelExpanded] = useState(false);
  const [inputDomainEdit, setInputDomainEdit] = useState<{
    input: CFSearchInput;
    inputIndex: number;
  }>();

  const { runCFAnalysis, cfAnalysis, cfResults } =
    useCounterfactualExecution(executionId);

  const handleInputDomainEdit = (input: CFSearchInput, inputIndex: number) => {
    setInputDomainEdit({ input, inputIndex });
    if (!isSidePanelExpanded) {
      setIsSidePanelExpanded(true);
    }
  };

  const onRunAnalysis = () => {
    runCFAnalysis({ goals: state.goals, searchDomains: state.searchDomains });
  };

  const onSetupNewAnalysis = (resetType: CFAnalysisResetType) => {
    dispatch({
      type: 'CF_RESET_ANALYSIS',
      payload: { resetType: resetType, inputs, outcomes }
    });
  };

  useEffect(() => {
    if (cfResults) {
      const succeededResults = cfResults.solutions.filter(
        (result) => result.status !== 'FAILED' && result.isValid
      );
      if (succeededResults.length) {
        dispatch({
          type: 'CF_SET_RESULTS',
          payload: {
            results: succeededResults
          }
        });
      }
      const finalResult = cfResults.solutions.find(
        (result) => result.stage === 'FINAL'
      );
      if (finalResult !== undefined) {
        let executionStatus;
        if (finalResult.status === 'FAILED') {
          executionStatus = CFExecutionStatus.FAILED;
        } else if (finalResult.isValid) {
          executionStatus = CFExecutionStatus.COMPLETED;
        } else {
          executionStatus = CFExecutionStatus.NO_RESULTS;
        }
        dispatch({
          type: 'CF_SET_STATUS',
          payload: {
            executionStatus
          }
        });
      }
    }
  }, [cfResults]);

  useEffect(() => {
    if (cfAnalysis?.status === RemoteDataStatus.FAILURE) {
      dispatch({
        type: 'CF_RESET_ANALYSIS',
        payload: { resetType: 'EDIT', inputs, outcomes }
      });
    }
  }, [cfAnalysis, inputs, outcomes]);

  const maxRunningTimeSeconds = useMemo(() => {
    if (cfAnalysis?.status === RemoteDataStatus.SUCCESS) {
      return cfAnalysis.data.maxRunningTimeSeconds;
    }
  }, [cfAnalysis]);

  const panelContent = (
    <DrawerPanelContent widths={{ default: 'width_33' }}>
      {inputDomainEdit && (
        <CounterfactualInputDomainEdit
          input={inputDomainEdit.input}
          inputIndex={inputDomainEdit.inputIndex}
          onClose={() => setIsSidePanelExpanded(false)}
        />
      )}
    </DrawerPanelContent>
  );

  return (
    <CFDispatch.Provider value={dispatch}>
      {containerHeight > 0 && (
        <>
          {cfAnalysis?.status === RemoteDataStatus.FAILURE && (
            <CounterfactualError />
          )}
          <Drawer
            isExpanded={isSidePanelExpanded}
            className="counterfactual__drawer"
          >
            <DrawerContent
              panelContent={panelContent}
              style={{ height: containerHeight }}
            >
              <DrawerContentBody
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  height: '100%'
                }}
              >
                <PageSection variant="light" isFilled={true}>
                  <section className="counterfactual__section">
                    <Stack hasGutter>
                      <StackItem>
                        <Flex spaceItems={{ default: 'spaceItemsXl' }}>
                          <FlexItem>
                            <Title headingLevel="h3" size="2xl">
                              Counterfactual analysis
                            </Title>
                          </FlexItem>
                          <FlexItem>
                            <CounterfactualOutcomesSelected
                              goals={state.goals}
                            />
                          </FlexItem>
                          {state.status.executionStatus ===
                            CFExecutionStatus.COMPLETED && (
                            <CounterfactualExecutionInfo
                              resultsCount={state.results.length}
                            />
                          )}
                        </Flex>
                      </StackItem>
                      {state.status.executionStatus ===
                        CFExecutionStatus.NOT_STARTED && (
                        <StackItem className={'counterfactual__hint'}>
                          <TextContent>
                            <Text component={TextVariants.p}>
                              Select a desired counterfactual outcome; one or
                              more data types, and modify the input constraints.
                            </Text>
                          </TextContent>
                        </StackItem>
                      )}
                      <CounterfactualCompletedMessage status={state.status} />
                      {containerWidth <= 880 && (
                        <EmptyState
                          variant={EmptyStateVariant.xs}
                          className={'counterfactual__unsupported-screen-size'}
                        >
                          <EmptyStateIcon icon={OutlinedMehIcon} />
                          <Title headingLevel="h4" size="md">
                            Screen size not supported
                          </Title>
                          <EmptyStateBody>
                            This is an experimental feature and it only supports
                            larger screen sizes at the moment.
                          </EmptyStateBody>
                        </EmptyState>
                      )}
                      {containerWidth > 880 && (
                        <StackItem
                          isFilled={true}
                          style={{ overflow: 'hidden' }}
                        >
                          <CounterfactualToolbar
                            status={state.status}
                            goals={state.goals}
                            onRunAnalysis={onRunAnalysis}
                            onSetupNewAnalysis={onSetupNewAnalysis}
                            maxRunningTimeSeconds={maxRunningTimeSeconds}
                          />
                          <CounterfactualTable
                            inputs={state.searchDomains}
                            results={state.results}
                            status={state.status}
                            onOpenInputDomainEdit={handleInputDomainEdit}
                            containerWidth={containerWidth}
                          />
                        </StackItem>
                      )}
                    </Stack>
                  </section>
                </PageSection>
              </DrawerContentBody>
            </DrawerContent>
          </Drawer>
        </>
      )}
    </CFDispatch.Provider>
  );
};

export default CounterfactualAnalysis;

export const CFDispatch = React.createContext<React.Dispatch<cfActions>>(null);
