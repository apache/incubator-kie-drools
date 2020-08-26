import React from 'react';
import {
  Card,
  CardBody,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Title
} from '@patternfly/react-core';
import { ChartBarIcon } from '@patternfly/react-icons';

const ExplanationUnavailable = () => {
  return (
    <Card>
      <CardBody>
        <EmptyState>
          <EmptyStateIcon icon={ChartBarIcon} />
          <Title headingLevel="h4" size="lg">
            Explanation Unavailable
          </Title>
          <EmptyStateBody>
            The current outcome has no explanation information available.
          </EmptyStateBody>
        </EmptyState>
      </CardBody>
    </Card>
  );
};

export default ExplanationUnavailable;
