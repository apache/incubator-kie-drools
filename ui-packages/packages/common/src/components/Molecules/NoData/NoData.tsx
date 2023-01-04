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
import { SearchIcon } from '@patternfly/react-icons';
import { Redirect } from 'react-router';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

export interface IOwnProps {
  defaultPath: string;
  defaultButton: string;
  location: any;
}

const NoData: React.FC<IOwnProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  let prevPath;
  if (props.location.state !== undefined) {
    prevPath = props.location.state.prev;
  } else {
    prevPath = props.defaultPath;
  }

  const tempPath = prevPath.split('/');
  prevPath = tempPath.filter((item) => item);

  const [isRedirect, setIsredirect] = useState(false);
  const redirectHandler = () => {
    setIsredirect(true);
  };
  return (
    <>
      {isRedirect && <Redirect to={`/${prevPath[0]}`} />}
      <PageSection
        isFilled={true}
        {...componentOuiaProps(
          ouiaId,
          'no-data',
          ouiaSafe ? ouiaSafe : !isRedirect
        )}
      >
        <Bullseye>
          <EmptyState variant={EmptyStateVariant.full}>
            <EmptyStateIcon icon={SearchIcon} />
            <Title headingLevel="h1" size="4xl">
              {props.location.state ? props.location.state.title : 'No matches'}
            </Title>
            <EmptyStateBody>
              {props.location.state
                ? props.location.state.description
                : 'No data to display'}
            </EmptyStateBody>
            <Button variant="primary" onClick={redirectHandler}>
              {props.location.state
                ? props.location.state.buttonText
                : props.defaultButton}
            </Button>
          </EmptyState>
        </Bullseye>
      </PageSection>
    </>
  );
};

export default NoData;
