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
