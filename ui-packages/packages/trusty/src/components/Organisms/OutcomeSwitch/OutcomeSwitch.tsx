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
