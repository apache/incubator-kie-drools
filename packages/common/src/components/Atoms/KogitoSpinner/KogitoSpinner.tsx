import React from 'react';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  Spinner
} from '@patternfly/react-core';

interface IOwnProps {
  spinnerText: string;
}
const KogitoSpinner: React.FC<IOwnProps> = ({ spinnerText }) => {
  return (
    <EmptyState>
      <EmptyStateIcon variant="container" component={Spinner} />
      <Title size="lg">{spinnerText}</Title>
    </EmptyState>
  );
};

export default KogitoSpinner;
