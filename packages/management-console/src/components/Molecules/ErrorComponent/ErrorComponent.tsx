import React, { useState, useEffect } from 'react';
import {
  PageSection,
  Bullseye,
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  Button,
  EmptyStateBody,
  Title,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
import { Redirect, RouteComponentProps } from 'react-router';
import { ouiaPageTypeAndObjectId } from '@kogito-apps/common';

interface LocationProps {
  prev?: any;
}

const ErrorComponent: React.FC<
  RouteComponentProps<{}, {}, LocationProps> & InjectedOuiaProps
> = ({ ouiaContext, ...props }) => {
  let prevPath;
  if (props.location.state !== undefined) {
    prevPath = props.location.state.prev;
  } else {
    prevPath = '/ProcessInstances';
  }

  const tempPath = prevPath.split('/');
  prevPath = tempPath.filter(item => item);

  const [isRedirect, setIsredirect] = useState(false);

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'error');
  });

  const redirectHandler = () => {
    setIsredirect(true);
  };
  return (
    <>
      {isRedirect && <Redirect to={`/${prevPath[0]}`} />}
      <PageSection variant="light">
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.full}>
            <EmptyStateIcon
              icon={ExclamationCircleIcon}
              size="md"
              color="var(--pf-global--danger-color--100)"
            />
            <Title headingLevel="h1" size="4xl">
              404 Error: page not found
            </Title>
            <EmptyStateBody>This page could not be found.</EmptyStateBody>
            <Button variant="primary" onClick={redirectHandler}>
              Go to process instances               
            </Button>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </>
  );
};

export default withOuiaContext(ErrorComponent);
