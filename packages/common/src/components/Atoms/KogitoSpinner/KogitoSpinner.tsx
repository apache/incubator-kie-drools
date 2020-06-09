import React from 'react';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  Spinner
} from '@patternfly/react-core';

interface KogitoSpinnerProps {
  spinnerText: string;
}
const KogitoSpinner: React.FC<KogitoSpinnerProps> = ({ spinnerText }) => {
  return (
    <EmptyState>
      <EmptyStateIcon variant="container" component={Spinner} />
      <Title size="lg">{spinnerText}</Title>
    </EmptyState>
  );
};

KogitoSpinner.displayName = 'KogitoSpinner';
export default KogitoSpinner;
