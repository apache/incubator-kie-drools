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
import {
  EmptyState,
  EmptyStateIcon
} from '@patternfly/react-core/dist/js/components/EmptyState';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import { Spinner } from '@patternfly/react-core/dist/js/components/Spinner';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

interface KogitoSpinnerProps {
  spinnerText: string;
}
export const KogitoSpinner: React.FC<KogitoSpinnerProps & OUIAProps> = ({
  spinnerText,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <EmptyState {...componentOuiaProps(ouiaId, 'kogito-spinner', ouiaSafe)}>
      <EmptyStateIcon variant="container" component={Spinner} />
      <Title size="lg" headingLevel="h3">
        {spinnerText}
      </Title>
    </EmptyState>
  );
};
