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
  const [isConfirmNewCFDialogOpen, setIsConfirmNewCFDialogOpen] = useState(
    false
  );
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
        If you start a New Counterfactual analysis, or Edit the existing one,
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
                        Select Inputs, provide inputs constraints and set up
                        Outcomes to run a counterfactual analysis.
                      </div>
                    ) : (
                      <div>
                        Run the counterfactual analysis based on selected Inputs
                        and Outcomes.
                      </div>
                    )
                  }
                >
                  <Button
                    variant={ButtonVariant.primary}
                    aria-label="Run Counterfactual Analysis"
                    onClick={handleRun}
                    isAriaDisabled={status.isDisabled}
                    id="counterfactual-run"
                  >
                    Run Counterfactual
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
                    Set Up Outcomes
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
                    aria-label="New Counterfactual Analysis"
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
