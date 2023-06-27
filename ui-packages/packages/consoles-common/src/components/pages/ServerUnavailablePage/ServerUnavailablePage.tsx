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
  Bullseye,
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  PageSection,
  Title
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

interface Props {
  displayName?: string;
  reload: () => void;
}

const ServerUnavailablePage: React.FC<Props & OUIAProps> = ({
  displayName,
  reload,
  ouiaId,
  ouiaSafe
}) => {
  const name = displayName || process.env.KOGITO_APP_NAME;

  return (
    <PageSection
      variant="light"
      {...componentOuiaProps(ouiaId, 'server-unavailable', ouiaSafe)}
    >
      <Bullseye>
        <EmptyState variant={EmptyStateVariant.full}>
          <EmptyStateIcon
            icon={ExclamationCircleIcon}
            color="var(--pf-global--danger-color--100)"
          />
          <Title headingLevel="h1" size="4xl">
            Error connecting server
          </Title>
          <EmptyStateBody>
            {`The ${name} could not access the server to display content.`}
          </EmptyStateBody>
          <EmptyStateBody>
            Try reloading the page, or contact your administrator for more
            information.
          </EmptyStateBody>
          <Button variant="primary" onClick={reload}>
            Refresh
          </Button>
        </EmptyState>
      </Bullseye>
    </PageSection>
  );
};

export default ServerUnavailablePage;
