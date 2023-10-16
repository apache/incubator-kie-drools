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
import React, { useContext, useState } from 'react';
import {
  Button,
  ButtonVariant,
  Modal,
  ModalVariant,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
  Tooltip
} from '@patternfly/react-core';
import CounterfactualOutcomeSelection from '../CounterfactualOutcomeSelection/CounterfactualOutcomeSelection';
import { CFDispatch } from '../CounterfactualAnalysis/CounterfactualAnalysis';
import {
  CFAnalysisResetType,
  CFExecutionStatus,
  CFGoal,
  CFStatus
} from '../../../types';
import CounterfactualProgressBar from '../../Molecules/CounterfactualProgressBar/CounterfactualProgressBar';
import './CounterfactualToolbar.scss';

type CounterfactualToolbarProps = {
  goals: CFGoal[];
  status: CFStatus;
  onRunAnalysis: () => void;
  onSetupNewAnalysis: (resetType: CFAnalysisResetType) => void;
  maxRunningTimeSeconds: number;
};

const CounterfactualToolbar = (props: CounterfactualToolbarProps) => {
  const {
    goals,
    status,
    onRunAnalysis,
    onSetupNewAnalysis,
    maxRunningTimeSeconds
  } = props;
  const [isOutcomeSelectionOpen, setIsOutcomeSelectionOpen] = useState(false);
  const [CFResetType, setCFResetType] = useState<CFAnalysisResetType>();
  const [isConfirmNewCFDialogOpen, setIsConfirmNewCFDialogOpen] =
    useState(false);
  const dispatch = useContext(CFDispatch);

  const toggleOutcomeSelection = () => {
    setIsOutcomeSelectionOpen(!isOutcomeSelectionOpen);
  };

  const handleRun = () => {
    onRunAnalysis();
    dispatch({
      type: 'CF_SET_STATUS',
      payload: {
        executionStatus: CFExecutionStatus.RUNNING
      }
    });
  };

  const handleNewCF = () => {
    if (status.executionStatus === CFExecutionStatus.NO_RESULTS) {
      onSetupNewAnalysis('NEW');
    } else {
      setIsConfirmNewCFDialogOpen(true);
      setCFResetType('NEW');
    }
  };

  const handleEditSearchDomain = () => {
    if (status.executionStatus === CFExecutionStatus.NO_RESULTS) {
      onSetupNewAnalysis('EDIT');
    } else {
      setIsConfirmNewCFDialogOpen(true);
      setCFResetType('EDIT');
    }
  };

  const handleNewCFModalClose = () => {
    setIsConfirmNewCFDialogOpen(false);
    setCFResetType(undefined);
  };

  const setupNewCF = () => {
    onSetupNewAnalysis(CFResetType);
    handleNewCFModalClose();
  };

  const handleCFReset = () => {
    onSetupNewAnalysis('NEW');
  };

  return (
    <>
      {isOutcomeSelectionOpen && (
        <CounterfactualOutcomeSelection
          isOpen={isOutcomeSelectionOpen}
          onClose={toggleOutcomeSelection}
          goals={goals}
        />
      )}
      <Modal
        variant={ModalVariant.small}
        titleIconVariant="warning"
        title="Results will be cleared"
        isOpen={isConfirmNewCFDialogOpen}
        onClose={handleNewCFModalClose}
        actions={[
          <Button key="confirm" variant="primary" onClick={setupNewCF}>
            Continue
          </Button>,
          <Button key="cancel" variant="link" onClick={handleNewCFModalClose}>
            Cancel
          </Button>
        ]}
      >
        If you start a new counterfactual analysis, or edit the existing one,
        any results will be cleared and cannot be retrieved.
      </Modal>
      <Toolbar id="cf-toolbar">
        <ToolbarContent>
          {status.executionStatus === CFExecutionStatus.NOT_STARTED && (
            <>
              <ToolbarItem>
                <Tooltip
                  content={
                    status.isDisabled ? (
                      <div>
                        Select inputs, provide inputs constraints and set up
                        outcomes to run a counterfactual analysis.
                      </div>
                    ) : (
                      <div>
                        Run the counterfactual analysis based on selected inputs
                        and outcomes.
                      </div>
                    )
                  }
                >
                  <Button
                    variant={ButtonVariant.primary}
                    aria-label="Run counterfactual analysis"
                    onClick={handleRun}
                    isAriaDisabled={status.isDisabled}
                    id="counterfactual-run"
                  >
                    Run counterfactual
                  </Button>
                </Tooltip>
              </ToolbarItem>
              <ToolbarItem>
                <Tooltip
                  content={
                    <div>
                      Sets the desired decision outcomes for a counterfactual
                      analysis.
                    </div>
                  }
                >
                  <Button
                    variant="secondary"
                    onClick={toggleOutcomeSelection}
                    id="counterfactual-setup-outcomes"
                  >
                    Set up outcomes
                  </Button>
                </Tooltip>
              </ToolbarItem>
              <ToolbarItem variant="separator" />
              <ToolbarItem>
                <Tooltip
                  content={
                    <div>
                      Clear all selections and reverts them to their initial
                      state.
                    </div>
                  }
                >
                  <Button
                    variant="link"
                    isInline={true}
                    onClick={handleCFReset}
                    id="counterfactual-reset"
                  >
                    Reset
                  </Button>
                </Tooltip>
              </ToolbarItem>
            </>
          )}
          {(status.executionStatus === CFExecutionStatus.COMPLETED ||
            status.executionStatus === CFExecutionStatus.FAILED ||
            status.executionStatus === CFExecutionStatus.NO_RESULTS) && (
            <>
              <ToolbarItem>
                <Tooltip
                  content={
                    <div>
                      Clear all and set up a new Counterfactual analysis.
                    </div>
                  }
                >
                  <Button
                    id="counterfactual-new"
                    variant={ButtonVariant.primary}
                    aria-label="New counterfactual analysis"
                    onClick={handleNewCF}
                  >
                    New Counterfactual
                  </Button>
                </Tooltip>
              </ToolbarItem>
              <ToolbarItem>
                <Tooltip
                  content={
                    <div>
                      Edit Inputs and Outcomes to rerun a counterfactual
                      analysis.
                    </div>
                  }
                >
                  <Button
                    id="counterfactual-edit"
                    variant="secondary"
                    onClick={handleEditSearchDomain}
                  >
                    Edit Counterfactual
                  </Button>
                </Tooltip>
              </ToolbarItem>
            </>
          )}
          {status.executionStatus === CFExecutionStatus.RUNNING && (
            <ToolbarItem>
              <CounterfactualProgressBar
                maxRunningTimeSeconds={maxRunningTimeSeconds}
              />
            </ToolbarItem>
          )}
        </ToolbarContent>
      </Toolbar>
    </>
  );
};

export default CounterfactualToolbar;
