import React from 'react';
import { Title, EmptyState, EmptyStateIcon } from '@patternfly/react-core';
import { Spinner } from '@patternfly/react-core/dist/esm/experimental';

interface IOwnProps {
  spinnerText: string;
}
const EmptyStateSpinner: React.FC<IOwnProps> = ({ spinnerText }) => {
  return (
    <EmptyState>
      <EmptyStateIcon variant="container" component={Spinner} />
      <Title size="lg">{spinnerText}</Title>
    </EmptyState>
  );
};

export default EmptyStateSpinner;
