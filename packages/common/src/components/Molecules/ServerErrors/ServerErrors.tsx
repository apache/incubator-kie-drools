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
import './ServerErrors.css';
import { withRouter } from 'react-router-dom';

const ServerErrors = props => {
  const [displayError, setDisplayError] = useState(false);

  return (
    <PageSection variant="light">
      <Bullseye>
        <EmptyState variant={EmptyStateVariant.full}>
          <EmptyStateIcon
            icon={ExclamationCircleIcon}
            size="md"
            color="var(--pf-global--danger-color--100)"
          />
          <Title headingLevel="h1" size="4xl">
            Error fetching data
          </Title>
          <EmptyStateBody>
            An error occurred while accessing data.{' '}
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
                {JSON.stringify(props.error)}
              </ClipboardCopy>
            </EmptyStateBody>
          )}
          <Button
            variant="primary"
            id="goback-button"
            onClick={() => props.history.goBack()}
          >
            Go back
          </Button>
        </EmptyState>
      </Bullseye>
    </PageSection>
  );
};

export default withRouter(ServerErrors);
