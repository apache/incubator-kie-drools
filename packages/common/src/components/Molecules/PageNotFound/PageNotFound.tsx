import React, { useState } from 'react';
import {
  PageSection,
  Bullseye,
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  Button,
  EmptyStateBody,
  Title
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
import { Redirect } from 'react-router';

interface IOwnProps {
  defaultPath: string;
  defaultButton: string;
  location: any;
}

const PageNotFound: React.FC<IOwnProps> = props => {
  let prevPath;
  if (props.location.state !== undefined) {
    prevPath = props.location.state.prev;
  } else {
    prevPath = props.defaultPath;
  }

  const tempPath = prevPath.split('/');
  prevPath = tempPath.filter(item => item);

  const [isRedirect, setIsredirect] = useState(false);
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
              {props.defaultButton}               
            </Button>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </>
  );
};

export default PageNotFound;
