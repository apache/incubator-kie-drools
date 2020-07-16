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
import { OUIAProps, componentOuiaProps } from '../../../utils/OuiaUtils';

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

export const KogitoEmptyState: React.FC<IOwnProps & OUIAProps> = ({
  type,
  title,
  body,
  onClick,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Bullseye {...componentOuiaProps(ouiaId, 'kogito-empty-state', ouiaSafe)}>
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
