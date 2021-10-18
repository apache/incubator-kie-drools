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
            Something went wrong
          </Title>
          <EmptyStatePrimary>
            <NavLink to="/">
              Go back to the &quot;Audit investigation&quot; home page
            </NavLink>
          </EmptyStatePrimary>
        </EmptyState>
      </PageSection>
    </>
  );
};

export default ApplicationError;
