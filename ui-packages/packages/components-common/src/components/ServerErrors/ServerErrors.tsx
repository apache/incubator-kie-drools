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

import React, { useState } from 'react';

import {
  PageSection,
  Bullseye,
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  Button,
  EmptyStateBody,
  Title,
  ClipboardCopy,
  ClipboardCopyVariant
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';

interface IOwnProps {
  error: any;
  variant: string;
  children?: React.ReactElement;
}
const ServerErrors: React.FC<IOwnProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const [displayError, setDisplayError] = useState(false);

  const getErrorSubTitle = () => {
    try {
      const errorObject = JSON.parse(props.error);
      if (
        (errorObject.networkError && errorObject.networkError.name) ||
        (errorObject.networkError &&
          !errorObject.networkError.name &&
          errorObject.graphQLErrors &&
          !(errorObject.graphQLErrors.size > 0) &&
          errorObject.message === 'Network error: Failed to fetch')
      ) {
        return 'An error occurred while accessing data. It is possible the data index is still being loaded, please try again in a few moments.';
      } else {
        return 'An error occurred while accessing data.';
      }
    } catch (error) {
      return 'An error occurred while accessing data.';
    }
  };

  const getErrorContent = () => {
    try {
      const errorObject = JSON.parse(props.error);
      return errorObject.networkError && errorObject.networkError.name
        ? JSON.stringify(errorObject.networkError)
        : errorObject.graphQLErrors && errorObject.graphQLErrors.size > 0
        ? JSON.stringify(errorObject.graphQLErrors)
        : JSON.stringify(props.error);
    } catch (error) {
      return props.error;
    }
  };

  const renderContent = () => (
    <Bullseye {...componentOuiaProps(ouiaId, 'server-errors', ouiaSafe)}>
      <EmptyState variant={EmptyStateVariant.full}>
        <EmptyStateIcon
          icon={ExclamationCircleIcon}
          color="var(--pf-global--danger-color--100)"
        />
        <Title headingLevel="h1" size="4xl">
          Error fetching data
        </Title>
        <EmptyStateBody>
          {getErrorSubTitle()}{' '}
          <Button
            variant="link"
            isInline
            id="display-error"
            onClick={() => setDisplayError(!displayError)}
          >
            See more details
          </Button>
        </EmptyStateBody>
        {displayError && (
          <EmptyStateBody>
            <ClipboardCopy
              isCode
              variant={ClipboardCopyVariant.expansion}
              isExpanded={true}
              className="pf-u-text-align-left"
            >
              {getErrorContent()}
            </ClipboardCopy>
          </EmptyStateBody>
        )}
        {props.children}
      </EmptyState>
    </Bullseye>
  );

  return (
    <>
      {props.variant === 'large' && (
        <PageSection variant="light">{renderContent()}</PageSection>
      )}
      {props.variant === 'small' && renderContent()}
    </>
  );
};

export default ServerErrors;
