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
import { List, ListItem, ListVariant } from '@patternfly/react-core';
import { CFGoal, CFGoalRole } from '../../../types';

type CounterfactualOutcomesSelectedProps = {
  goals: CFGoal[];
};

const CounterfactualOutcomesSelected = ({
  goals
}: CounterfactualOutcomesSelectedProps) => {
  const selectedOutcomes = useMemo(
    () =>
      goals.filter(
        (goal) =>
          goal.role === CFGoalRole.FIXED || goal.role === CFGoalRole.FLOATING
      ),
    [goals]
  );

  return (
    <>
      {selectedOutcomes.length > 0 && (
        <List
          variant={ListVariant.inline}
          style={{ color: 'var(--pf-global--Color--200)' }}
        >
          <ListItem key="selected outcomes">
            <span>Selected Outcomes:</span>{' '}
            {selectedOutcomes.map((goal, index) => (
              <span key={goal.id}>
                {goal.role === CFGoalRole.FIXED && (
                  <span>
                    {goal.name}: {goal.value.value.toString()}
                  </span>
                )}
                {goal.role === CFGoalRole.FLOATING && (
                  <span>{goal.name}: Any</span>
                )}
                {index + 1 !== selectedOutcomes.length && <span>, </span>}
              </span>
            ))}
          </ListItem>
        </List>
      )}
    </>
  );
};

export default CounterfactualOutcomesSelected;
