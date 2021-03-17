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
import _ from 'lodash';
import { ActionGroup } from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/common';
import {
  convertActionsToButton,
  FormAction
} from '../../../util/uniforms/FormActionsUtils';

interface IOwnProps {
  actions?: FormAction[];
}

const FormFooter: React.FC<IOwnProps & OUIAProps> = ({
  actions,
  ouiaId,
  ouiaSafe
}) => {
  if (_.isEmpty(actions)) {
    return null;
  }

  return (
    <ActionGroup {...componentOuiaProps(ouiaId, 'form-footer', ouiaSafe)}>
      {convertActionsToButton(actions, true)}
    </ActionGroup>
  );
};

export default FormFooter;
