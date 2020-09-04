import React from 'react';
import {
  Card,
  CardBody,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Title
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';

type ExplanationErrorProps = {
  statusDetail?: string;
};

const ExplanationError = ({ statusDetail }: ExplanationErrorProps) => {
  return (
    <Card>
      <CardBody>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationCircleIcon} color="#C9190B" />
          <Title headingLevel="h4" size="lg">
            Explanation Error
          </Title>
          <EmptyStateBody>
            <p>
              There was an error calculating explanation information for this
              execution.
            </p>
            {statusDetail && (
              <p>
                Error Message:{' '}
                <span className="explanation-error-detail">{statusDetail}</span>
              </p>
            )}
          </EmptyStateBody>
        </EmptyState>
      </CardBody>
    </Card>
  );
};

export default ExplanationError;
