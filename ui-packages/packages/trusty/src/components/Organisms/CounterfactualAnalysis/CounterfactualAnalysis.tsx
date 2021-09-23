import React, { useEffect, useReducer, useState } from 'react';
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
  Flex,
  FlexItem,
  PageSection,
  Stack,
  StackItem,
  Title
} from '@patternfly/react-core';
import {
  cfActions,
  cfInitState,
  cfReducer
} from '../../Templates/Counterfactual/counterfactualReducer';
import CounterfactualInputDomainEdit from '../CounterfactualInputDomainEdit/CounterfactualInputDomainEdit';
import CounterfactualOutcomesSelected from '../../Molecules/CounterfactualsOutcomesSelected/CounterfactualOutcomesSelected';
import CounterfactualExecutionInfo from '../../Molecules/CounterfactualExecutionInfo/CounterfactualExecutionInfo';
import CounterfactualHint from '../../Molecules/CounterfactualHint/CounterfactualHint';
import CounterfactualCompletedMessage from '../../Molecules/CounterfactualCompletedMessage/CounterfactualCompletedMessage';
import CounterfactualToolbar from '../CounterfactualToolbar/CounterfactualToolbar';
import CounterfactualTable from '../CounterfactualTable/CounterfactualTable';
import useCounterfactualExecution from './useCounterfactualExecution';
import {
  CFAnalysisResetType,
  CFExecutionStatus,
  CFSearchInput,
  ItemObject,
  Outcome
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
  const {
    executionId,
    inputs,
    outcomes,
    containerWidth,
    containerHeight
  } = props;
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

  const { runCFAnalysis, cfResults } = useCounterfactualExecution(executionId);

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
        result => result.status !== 'FAILED' && result.isValid
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
        result => result.stage === 'FINAL'
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
                            Counterfactual Analysis
                          </Title>
                        </FlexItem>
                        <FlexItem>
                          <CounterfactualOutcomesSelected goals={state.goals} />
                        </FlexItem>
                        {state.status.executionStatus ===
                          CFExecutionStatus.COMPLETED && (
                          <CounterfactualExecutionInfo
                            resultsCount={state.results.length}
                          />
                        )}
                      </Flex>
                    </StackItem>
                    <CounterfactualHint
                      isVisible={
                        state.status.executionStatus ===
                        CFExecutionStatus.NOT_STARTED
                      }
                    />
                    <CounterfactualCompletedMessage status={state.status} />
                    <StackItem isFilled={true} style={{ overflow: 'hidden' }}>
                      <CounterfactualToolbar
                        status={state.status}
                        goals={state.goals}
                        onRunAnalysis={onRunAnalysis}
                        onSetupNewAnalysis={onSetupNewAnalysis}
                      />
                      <CounterfactualTable
                        inputs={state.searchDomains}
                        results={state.results}
                        status={state.status}
                        onOpenInputDomainEdit={handleInputDomainEdit}
                        containerWidth={containerWidth}
                      />
                    </StackItem>
                  </Stack>
                </section>
              </PageSection>
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      )}
    </CFDispatch.Provider>
  );
};

export default CounterfactualAnalysis;

export const CFDispatch = React.createContext<React.Dispatch<cfActions>>(null);
