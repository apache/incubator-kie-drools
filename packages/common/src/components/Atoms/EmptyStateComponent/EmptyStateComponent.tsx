import React from 'react';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  Bullseye,
  Button
} from '@patternfly/react-core';
import { SearchIcon } from '@patternfly/react-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
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
    filterClick(['Ready']);
    setFilters(['Ready']);
    setCheckedArray(['Ready']);
  };
  return (
    <Bullseye>
      <EmptyState>
        {iconType === 'searchIcon' && <EmptyStateIcon icon={SearchIcon} />}
        {iconType === 'warningTriangleIcon' && (
          <FontAwesomeIcon
            icon={faExclamationTriangle}
            size="3x"
            color="var(--pf-global--warning-color--100)"
            className="pf-u-mb-xl"
          />
        )}
        {iconType === 'warningTriangleIcon1' && (
          <FontAwesomeIcon
            icon={faExclamationTriangle}
            size="3x"
            color="var(--pf-global--warning-color--100)"
            className="pf-u-mb-xl"
          />
        )}
        <Title size="lg">{title}</Title>
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
