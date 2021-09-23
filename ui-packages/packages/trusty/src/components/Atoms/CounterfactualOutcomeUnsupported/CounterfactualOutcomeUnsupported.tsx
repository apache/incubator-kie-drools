import React from 'react';
import { CFGoal } from '../../../types';
import { FormGroup, Text, TextVariants } from '@patternfly/react-core';
import './CounterfactualOutcomeUnsupported.scss';

type CounterfactualOutcomeUnsupportedProps = {
  goal: CFGoal;
};

const CounterfactualOutcomeUnsupported = (
  props: CounterfactualOutcomeUnsupportedProps
) => {
  const { goal } = props;

  return (
    <FormGroup label={goal.name} fieldId={goal.id}>
      <Text
        component={TextVariants.p}
        className="counterfactual-outcome__unsupported"
      >
        Not yet supported
      </Text>
    </FormGroup>
  );
};

export default CounterfactualOutcomeUnsupported;
