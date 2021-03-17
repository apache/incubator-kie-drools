/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Button } from '@patternfly/react-core';
import _ from 'lodash';
import React from 'react';

export interface FormAction {
  name: string;
  execute(): void;
}

interface FormButton {
  key: string;
  label: string;
  variant: 'primary' | 'secondary';
  onClick: () => void;
}

export const convertActionsToButton = (
  actions: FormAction[],
  enabled: boolean
) => {
  if (_.isEmpty(actions)) {
    return null;
  }

  const capitalize = label => {
    return label.charAt(0).toUpperCase() + label.slice(1);
  };

  const isPrimary = (label: string): boolean => {
    // Assuming that Complete will be the default act
    return label.toLowerCase() === 'complete';
  };

  const resolveButtonVariant = (action: FormAction) => {
    if (isPrimary(action.name)) {
      return 'primary';
    }
    return 'secondary';
  };

  const buttons: FormButton[] = actions.map(action => {
    return {
      key: `submit-${action.name}`,
      label: capitalize(action.name),
      variant: resolveButtonVariant(action),
      onClick: () => action.execute()
    };
  });

  buttons.sort((buttonA, buttonB) => {
    if (isPrimary(buttonA.label)) {
      return -1;
    }
    if (isPrimary(buttonB.label)) {
      return 2;
    }
    return buttonA.label.localeCompare(buttonB.label);
  });

  return buttons.map(button => {
    return (
      <Button
        type="submit"
        key={button.key}
        variant={button.variant}
        onClick={button.onClick}
        isDisabled={!enabled}
      >
        {button.label}
      </Button>
    );
  });
};
