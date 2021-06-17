import React from 'react';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  Spinner
} from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

interface KogitoSpinnerProps {
  spinnerText: string;
}
const KogitoSpinner: React.FC<KogitoSpinnerProps & OUIAProps> = ({
  spinnerText,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <EmptyState {...componentOuiaProps(ouiaId, 'kogito-spinner', ouiaSafe)}>
      <EmptyStateIcon variant="container" component={Spinner} />
      <Title size="lg" headingLevel="h3">
        {spinnerText}
      </Title>
    </EmptyState>
  );
};

KogitoSpinner.displayName = 'KogitoSpinner';
export default KogitoSpinner;
