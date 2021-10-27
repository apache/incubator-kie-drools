import React from 'react';
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
  PageSection,
  PageSectionVariants,
  TextContent,
  Title
} from '@patternfly/react-core';
import { WarningTriangleIcon } from '@patternfly/react-icons';
import { NavLink } from 'react-router-dom';

const ApplicationError = () => {
  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Title size="3xl" headingLevel="h2">
            Error
          </Title>
        </TextContent>
      </PageSection>
      <PageSection isFilled={false}>
        <EmptyState variant={'xl'}>
          <EmptyStateIcon icon={WarningTriangleIcon} />
          <Title size="2xl" headingLevel="h4">
            Server Error
          </Title>
          <EmptyStateBody>
            Something went wrong with your server. Reach out to your IT team for
            help.
          </EmptyStateBody>
          <EmptyStatePrimary>
            <NavLink to="/">Return to home page</NavLink>
          </EmptyStatePrimary>
        </EmptyState>
      </PageSection>
    </>
  );
};

export default ApplicationError;
