import React, { useEffect, useState } from 'react';
import {
  Select,
  SelectDirection,
  SelectOption,
  SelectOptionObject,
  SelectVariant
} from '@patternfly/react-core';
import { Outcome } from '../../../types';
import './OutcomeSwitch.scss';

type OutcomeSwitchProps = {
  currentExplanationId: string;
  onDecisionSelection: (outcomeId: string) => void;
  outcomesList: Outcome[];
};

const OutcomeSwitch = (props: OutcomeSwitchProps) => {
  const { outcomesList, onDecisionSelection, currentExplanationId } = props;
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState<string | SelectOptionObject>(
    currentExplanationId
  );

  const onToggle = (openStatus: boolean) => {
    setIsOpen(openStatus);
  };

  const onSelect = (
    event: React.MouseEvent | React.ChangeEvent,
    selection: string | SelectOptionObject
  ) => {
    setSelected(selection);
    onDecisionSelection(selection as string);
    setIsOpen(false);
  };

  useEffect(() => {
    setSelected(currentExplanationId);
  }, [currentExplanationId]);

  return (
    <div className="outcome-switch">
      <Select
        variant={SelectVariant.single}
        aria-label="Select Decision Outcome"
        onToggle={onToggle}
        onSelect={onSelect}
        selections={selected}
        isOpen={isOpen}
        direction={SelectDirection.down}
      >
        {outcomesList.map((item, index) => (
          <SelectOption key={index} value={item.outcomeId}>
            {item.outcomeName}
          </SelectOption>
        ))}
      </Select>
    </div>
  );
};

export default OutcomeSwitch;
