import React from 'react';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  Bullseye,
  Button,
  EmptyStateVariant
} from '@patternfly/react-core';
import {
  SearchIcon,
  ExclamationTriangleIcon,
  InfoCircleIcon
} from '@patternfly/react-icons';
import '@patternfly/patternfly/patternfly-addons.css';

interface IOwnProps {
  iconType: string;
  title: string;
  body: string;
  filterClick?: any;
  setFilters?: any;
  setCheckedArray?: any;
  refetch?: any;
}
const EmptyStateComponent: React.FC<IOwnProps> = ({
  iconType,
  title,
  body,
  filterClick,
  setFilters,
  setCheckedArray,
  refetch
}) => {
  const resetClick = () => {
    filterClick(['ACTIVE']);
    setFilters(['ACTIVE']);
    setCheckedArray(['ACTIVE']);
  };
  return (
    <Bullseye>
      <EmptyState variant={EmptyStateVariant.full}>
        {iconType === 'searchIcon' && (
          <EmptyStateIcon icon={SearchIcon} size="sm" />
        )}
        {(iconType === 'warningTriangleIcon' ||
          iconType === 'warningTriangleIcon1') && (
          <EmptyStateIcon
            icon={ExclamationTriangleIcon}
            size="sm"
            color="var(--pf-global--warning-color--100)"
          />
        )}
        {iconType === 'infoCircleIcon' && (
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

        {iconType === 'warningTriangleIcon' && (
          <Button variant="primary" onClick={() => refetch()}>
            Refresh
          </Button>
        )}

        {iconType === 'warningTriangleIcon1' && (
          <Button variant="link" onClick={resetClick}>
            Reset to default
          </Button>
        )}
      </EmptyState>
    </Bullseye>
  );
};

export default EmptyStateComponent;
