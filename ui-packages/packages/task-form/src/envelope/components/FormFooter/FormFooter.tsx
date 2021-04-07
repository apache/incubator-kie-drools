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

import React from 'react';
import _ from 'lodash';
import { ActionList, ActionListItem } from '@patternfly/react-core';
import { convertActionsToButton, FormAction } from '../utils';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/components-common';

interface IOwnProps {
  actions?: FormAction[];
  enabled?: boolean;
}

const FormFooter: React.FC<IOwnProps & OUIAProps> = ({
  actions,
  enabled = true,
  ouiaId,
  ouiaSafe
}) => {
  if (_.isEmpty(actions)) {
    return null;
  }

  const actionItems = convertActionsToButton(actions, enabled).map(
    (button, index) => {
      return (
        <ActionListItem key={`form-action-${index}`}>{button}</ActionListItem>
      );
    }
  );

  return (
    <ActionList {...componentOuiaProps(ouiaId, 'form-footer', ouiaSafe)}>
      {actionItems}
    </ActionList>
  );
};

export default FormFooter;
