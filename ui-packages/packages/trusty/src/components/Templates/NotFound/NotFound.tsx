import React from 'react';
import {
  EmptyState,
  EmptyStateIcon,
  EmptyStatePrimary,
  PageSection,
  PageSectionVariants,
  TextContent,
  Title
} from '@patternfly/react-core';
import { WarningTriangleIcon } from '@patternfly/react-icons';
import { NavLink } from 'react-router-dom';

const NotFound = () => {
  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Title size="3xl" headingLevel="h2">
            Page not found
          </Title>
        </TextContent>
      </PageSection>
      <PageSection isFilled={true}>
        <EmptyState variant={'xl'}>
          <EmptyStateIcon icon={WarningTriangleIcon} />
          <Title size="2xl" headingLevel="h4">
            The page you’re looking for doesn’t exist
          </Title>
          <EmptyStatePrimary>
            <NavLink to="/">Return to home page</NavLink>
          </EmptyStatePrimary>
        </EmptyState>
      </PageSection>
    </>
  );
};

export default NotFound;
