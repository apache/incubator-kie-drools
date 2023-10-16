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
import React, { useCallback, useContext, useState } from 'react';
import {
  Alert,
  Button,
  Form,
  Modal,
  ModalVariant,
  Title,
  TitleSizes,
  Tooltip
} from '@patternfly/react-core';
import { CFGoal, CFGoalRole } from '../../../types';
import { CFDispatch } from '../CounterfactualAnalysis/CounterfactualAnalysis';
import './CounterfactualOutcomeSelection.scss';
import { InfoCircleIcon } from '@patternfly/react-icons';
import CounterfactualOutcome from '../../Molecules/CounterfactualOutcome/CounterfactualOutcome';
import CounterfactualOutcomeEdit from '../../Molecules/CounterfactualOutcomeEdit/CounterfactualOutcomeEdit';

type CounterfactualOutcomeSelectionProps = {
  isOpen: boolean;
  onClose: () => void;
  goals: CFGoal[];
};

const CounterfactualOutcomeSelection = (
  props: CounterfactualOutcomeSelectionProps
) => {
  const { isOpen, onClose, goals } = props;
  const [editingGoals, setEditingGoals] = useState(goals);
  const dispatch = useContext(CFDispatch);

  const isDesiredOutcomeDefined = useCallback(
    () =>
      editingGoals.filter((goal) => goal.role === CFGoalRole.FIXED).length > 0,
    [editingGoals]
  );

  const areAllDesiredOutcomeFloating = useCallback(
    () =>
      editingGoals
        .filter((goal) => goal.role !== CFGoalRole.UNSUPPORTED)
        .every((goal) => goal.role === CFGoalRole.FLOATING),
    [editingGoals]
  );

  const updateGoal = (updatedGoal: CFGoal) => {
    const updatedGoals = editingGoals.map((goal) => {
      if (goal.id !== updatedGoal.id) {
        return goal;
      }
      if (goal.role === CFGoalRole.UNSUPPORTED) {
        return goal;
      }
      let updatedRole = CFGoalRole.FLOATING;
      if (updatedGoal.role !== CFGoalRole.FLOATING) {
        updatedRole = CFGoalRole.FIXED;
        if (updatedGoal.value.value === updatedGoal.originalValue.value) {
          updatedRole = CFGoalRole.ORIGINAL;
        }
      }
      return {
        ...updatedGoal,
        role: updatedRole
      };
    });
    setEditingGoals(updatedGoals);
  };

  const handleApply = () => {
    // removing checked goals with no changed values
    const cleanedGoals = editingGoals.map((goal) => {
      if (goal.role === CFGoalRole.UNSUPPORTED) {
        return goal;
      }
      if (goal.role === CFGoalRole.FLOATING) {
        return goal;
      }
      if (goal.originalValue === goal.value) {
        return { ...goal, role: CFGoalRole.ORIGINAL };
      }
      return goal;
    });
    setEditingGoals(cleanedGoals);
    dispatch({ type: 'CF_SET_OUTCOMES', payload: cleanedGoals });
    onClose();
  };

  const modalActions = useCallback(() => {
    const actions = isDesiredOutcomeDefined()
      ? [
          <Button
            id="confirm-outcome-selection"
            key="confirm"
            variant="primary"
            aria-label="Confirm outcome selection"
            onClick={handleApply}
          >
            Confirm
          </Button>
        ]
      : [
          // See https://github.com/patternfly/patternfly-react/pull/5991.
          // We will currently get a memory leak error. Fixed in @patternfly/react-core@4.135.6
          <Tooltip
            key="cf-outcomes-confirm"
            position="top"
            content={
              <div>
                At least one desired outcome must have a value that is different
                from the original outcome.
              </div>
            }
          >
            <Button
              id="confirm-outcome-selection"
              key="confirm"
              variant="primary"
              aria-label="Confirm outcome selection"
              isAriaDisabled={!isDesiredOutcomeDefined()}
            >
              Confirm
            </Button>
          </Tooltip>
        ];
    actions.push(
      <Button key="cancel" variant="link" onClick={onClose}>
        Cancel
      </Button>
    );
    return actions;
  }, [isDesiredOutcomeDefined]);

  return (
    <>
      <Modal
        variant={ModalVariant.large}
        aria-label="Counterfactual desired outcomes"
        title="Specify desired outcomes"
        isOpen={isOpen}
        onClose={onClose}
        description="Specify desired counterfactual outcomes for one or more original decision outcomes."
        actions={modalActions()}
      >
        <>
          {areAllDesiredOutcomeFloating() && (
            <Alert
              variant="info"
              isInline={true}
              title="At least one desired counterfactual outcome cannot be automatically adjusted."
              className="counterfactual__outcomes-alert"
            />
          )}
          <Form className="counterfactual__outcomes-form">
            <div className="counterfactual__outcomes-grid">
              <div className="counterfactual__outcomes-grid__row counterfactual__outcomes-form__original">
                <Title headingLevel="h4" size={TitleSizes.md}>
                  Original decision outcome
                  <Tooltip
                    content={<div>Outcome of the original decision.</div>}
                  >
                    <Button variant="plain">
                      <InfoCircleIcon
                        color={'var(--pf-global--info-color--100)'}
                      />
                    </Button>
                  </Tooltip>
                </Title>
              </div>
              <div className="counterfactual__outcomes-grid__row">
                <Title headingLevel="h4" size={TitleSizes.md}>
                  Desired counterfactual outcome
                  <Tooltip
                    content={
                      <div>
                        Outcome to be fulfilled by the counterfactual
                        processing.
                      </div>
                    }
                  >
                    <Button variant="plain">
                      <InfoCircleIcon
                        color={'var(--pf-global--info-color--100)'}
                      />
                    </Button>
                  </Tooltip>
                </Title>
              </div>
              {editingGoals.map((goal, index) => (
                <React.Fragment key={index}>
                  <div className="counterfactual__outcomes-grid__row counterfactual__outcomes-form__original">
                    <CounterfactualOutcome key={index} goal={goal} />
                  </div>
                  <div className="counterfactual__outcomes-grid__row counterfactual__outcomes-form__desired">
                    <CounterfactualOutcomeEdit
                      key={index}
                      goal={goal}
                      index={index}
                      onUpdateGoal={updateGoal}
                    />
                  </div>
                </React.Fragment>
              ))}
            </div>
          </Form>
        </>
      </Modal>
    </>
  );
};

export default CounterfactualOutcomeSelection;
