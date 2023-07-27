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
import {
  ActionList,
  ActionListItem
} from '@patternfly/react-core/dist/js/components/ActionList';
import { convertActionsToButton, FormAction } from '../utils';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import '../styles.css';

interface IOwnProps {
  actions?: FormAction[];
  enabled?: boolean;
  onSubmitForm?: () => void;
}

export const FormFooter: React.FC<IOwnProps & OUIAProps> = ({
  actions,
  enabled = true,
  onSubmitForm,
  ouiaId,
  ouiaSafe
}) => {
  if (_.isEmpty(actions)) {
    return null;
  }

  const actionItems = convertActionsToButton(
    actions,
    enabled,
    onSubmitForm
  ).map((button, index) => {
    return (
      <ActionListItem key={`form-action-${index}`}>{button}</ActionListItem>
    );
  });

  return (
    <div
      className="kogito-components-common__form-footer-padding-top"
      {...componentOuiaProps(ouiaId, 'form-footer', ouiaSafe)}
    >
      <ActionList>{actionItems}</ActionList>
    </div>
  );
};
