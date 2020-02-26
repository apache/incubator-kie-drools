import React, { useState } from 'react';
import {
  PageSection,
  TextContent,
  Text,
  TextVariants,
  Bullseye,
  Button,
  Flex,
  FlexItem,
  FlexModifiers
} from '@patternfly/react-core';
import { Redirect } from 'react-router';

const ErrorComponent = () => {
  const [isRedirect, setIsredirect] = useState(false);
  const redirectHandler = () => {
    setIsredirect(true);
  };
  return (
    <>
      {isRedirect && <Redirect to="/DomainExplorer" />}
      <PageSection variant="light">
        <Bullseye>
          <Flex breakpointMods={[{ modifier: FlexModifiers.column }]}>
            <FlexItem>
              <TextContent>
                <Text component={TextVariants.h1}>404 - PAGE NOT FOUND</Text>
                <br />
                <Text component={TextVariants.p}>
                  Oops! This page could not be found
                </Text>
                <Text component={TextVariants.small}>
                  Sorry you cannot view this page, as the URL you have enter is
                  incorrect
                </Text>
              </TextContent>
            </FlexItem>
            <br />
            <FlexItem>
              <Button variant="primary" onClick={redirectHandler}>
                Go Back
              </Button>
            </FlexItem>
          </Flex>
        </Bullseye>
      </PageSection>
    </>
  );
};

export default ErrorComponent;
