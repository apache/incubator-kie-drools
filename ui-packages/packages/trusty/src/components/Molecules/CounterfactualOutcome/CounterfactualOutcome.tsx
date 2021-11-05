import React, { useMemo } from 'react';
import { FormGroup, Text, TextVariants } from '@patternfly/react-core';
import { CFGoal, CFGoalRole } from '../../../types';
import CounterfactualOutcomeUnsupported from '../../Atoms/CounterfactualOutcomeUnsupported/CounterfactualOutcomeUnsupported';
import './CounterfactualOutcome.scss';

type CounterfactualOutcomeProps = {
  goal: CFGoal;
};

const CounterfactualOutcome = (props: CounterfactualOutcomeProps) => {
  const { goal } = props;

  return (
    <>
      {goal.role === CFGoalRole.UNSUPPORTED ? (
        <CounterfactualOutcomeUnsupported goal={goal} />
      ) : (
        <FormGroup fieldId={goal.id} label={goal.name}>
          <CounterfactualOutcomeValue goal={goal} />
        </FormGroup>
      )}
    </>
  );
};

export default CounterfactualOutcome;

const CounterfactualOutcomeValue = (props: CounterfactualOutcomeProps) => {
  const { goal } = props;

  const value = useMemo(() => {
    if (typeof goal.originalValue.value === 'boolean') {
      return goal.originalValue.value ? 'True' : 'False';
    } else {
      return goal.originalValue.value;
    }
  }, [goal]);

  return (
    <Text component={TextVariants.p} className="counterfactual-outcome__text">
      {value}
    </Text>
  );
};
