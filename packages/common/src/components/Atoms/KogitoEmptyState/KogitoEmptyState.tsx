import React from 'react';
import {
  Bullseye,
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  Title
} from '@patternfly/react-core';
import {
  ExclamationTriangleIcon,
  InfoCircleIcon,
  SearchIcon
} from '@patternfly/react-icons';
import '@patternfly/patternfly/patternfly-addons.css';

export enum KogitoEmptyStateType {
  Search,
  Refresh,
  Reset,
  Info
}

interface IOwnProps {
  type: KogitoEmptyStateType;
  title: string;
  body: string;
  onClick?: () => void;
}

export const KogitoEmptyState: React.FC<IOwnProps> = ({
  type,
  title,
  body,
  onClick
}) => {
  return (
    <Bullseye>
      <EmptyState variant={EmptyStateVariant.full}>
        {type === KogitoEmptyStateType.Search && (
          <EmptyStateIcon icon={SearchIcon} size="sm" />
        )}
        {(type === KogitoEmptyStateType.Refresh ||
          type === KogitoEmptyStateType.Reset) && (
          <EmptyStateIcon
            icon={ExclamationTriangleIcon}
            size="sm"
            color="var(--pf-global--warning-color--100)"
          />
        )}
        {type === KogitoEmptyStateType.Info && (
          <EmptyStateIcon
            icon={InfoCircleIcon}
            size="sm"
            color="var(--pf-global--info-color--100)"
          />
        )}

        <Title headingLevel="h5" size="lg">
          {title}
        </Title>

        <EmptyStateBody>{body}</EmptyStateBody>

        {type === KogitoEmptyStateType.Refresh && (
          <Button variant="primary" onClick={onClick}>
            Refresh
          </Button>
        )}

        {type === KogitoEmptyStateType.Reset && (
          <Button variant="link" onClick={onClick}>
            Reset to default
          </Button>
        )}
      </EmptyState>
    </Bullseye>
  );
};
