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
import { Flex, FlexItem } from '@patternfly/react-core/dist/js/layouts/Flex';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

export interface IOwnProps {
  title: any;
  extra?: JSX.Element;
}

export const PageTitle: React.FC<IOwnProps & OUIAProps> = ({
  title,
  extra,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Flex {...componentOuiaProps(ouiaId, 'page-title', ouiaSafe)}>
      <FlexItem spacer={{ default: 'spacerSm' }}>
        <Title headingLevel="h1" size="4xl">
          {title}
        </Title>
      </FlexItem>
      {extra ? (
        <FlexItem spacer={{ default: 'spacerSm' }}>{extra}</FlexItem>
      ) : null}
    </Flex>
  );
};
