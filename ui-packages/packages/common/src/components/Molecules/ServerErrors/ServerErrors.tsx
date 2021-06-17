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
import '../../styles.css';
import { withRouter, RouteComponentProps } from 'react-router-dom';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

interface IOwnProps {
  error: any;
  variant: string;
}
const ServerErrors: React.FC<IOwnProps & RouteComponentProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const [displayError, setDisplayError] = useState(false);

  const renderContent = () => (
    <>
      <EmptyStateIcon
        icon={ExclamationCircleIcon}
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
    </>
  );

  const renderBullseye = (renderButton: boolean) => (
    <Bullseye {...componentOuiaProps(ouiaId, 'server-errors', ouiaSafe)}>
      <EmptyState variant={EmptyStateVariant.full}>
        {renderContent()}
        {renderButton && (
          <Button
            variant="primary"
            id="goback-button"
            onClick={() => props.history.goBack()}
          >
            Go back
          </Button>
        )}
      </EmptyState>
    </Bullseye>
  );

  return (
    <>
      {props.variant === 'large' && (
        <PageSection variant="light">{renderBullseye(true)}</PageSection>
      )}
      {props.variant === 'small' && renderBullseye(false)}
    </>
  );
};

export default withRouter(ServerErrors);
