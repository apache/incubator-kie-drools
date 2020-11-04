/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import { ActionGroup, Button } from '@patternfly/react-core';
import { IFormAction } from '../../../util/uniforms/FormSubmitHandler/FormSubmitHandler';

interface IOwnProps {
  actions?: IFormAction[];
}

interface FormButton {
  key: string;
  label: string;
  variant: 'primary' | 'secondary';
  onClick: () => void;
}

const FormFooter: React.FC<IOwnProps> = ({ actions }) => {
  if (!actions || actions.length == 0) {
    return null;
  }

  const capitalize = label => {
    return label.charAt(0).toUpperCase() + label.slice(1);
  };

  const isPrimary = (label: string): boolean => {
    return label.toLowerCase() === 'complete';
  };

  const resolveButtonVariant = (action: IFormAction) => {
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
      onClick: () => {
        if (action.execute) {
          action.execute();
        }
      }
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

  return (
    <ActionGroup>
      {buttons.map(button => {
        return (
          <Button
            type="submit"
            key={button.key}
            variant={button.variant}
            onClick={button.onClick}
          >
            {button.label}
          </Button>
        );
      })}
    </ActionGroup>
  );
};

export default FormFooter;
