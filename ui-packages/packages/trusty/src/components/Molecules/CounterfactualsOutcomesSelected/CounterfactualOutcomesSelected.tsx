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
        goal =>
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
