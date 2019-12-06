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
}
const EmptyStateComponent: React.FC<IOwnProps> = ({
  iconType,
  title,
  body
}) => {
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
        <Title size="lg">{title}</Title>
        <EmptyStateBody>{body}</EmptyStateBody>
        {iconType === 'warningTriangleIcon' && (
          <Button variant="primary" onClick={() => window.location.reload()}>
            Refresh
          </Button>
        )}
      </EmptyState>
    </Bullseye>
  );
};

export default EmptyStateComponent;
