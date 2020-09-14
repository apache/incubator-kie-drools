import React from 'react';
import { Flex, FlexItem, Title } from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/common';

export interface IOwnProps {
  title: any;
  extra?: JSX.Element;
}

const PageTitle: React.FC<IOwnProps & OUIAProps> = ({
  title,
  extra,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <React.Fragment>
      <Flex {...componentOuiaProps(ouiaId, 'page-title', ouiaSafe)}>
        <FlexItem spacer={{ default: 'spacerSm' }}>
          <Title headingLevel="h1" size="4xl">
            {title}
          </Title>
        </FlexItem>
        {extra ? (
          <FlexItem spacer={{ default: 'spacerSm' }}>{extra}</FlexItem>
        ) : null}
      </Flex>
    </React.Fragment>
  );
};

export default PageTitle;
