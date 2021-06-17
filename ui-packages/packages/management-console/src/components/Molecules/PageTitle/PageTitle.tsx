import React from 'react';
import { Title } from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

export interface IOwnProps {
  title: string;
}

const PageTitle: React.FC<IOwnProps & OUIAProps> = ({
  title,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <React.Fragment>
      <Title
        headingLevel="h1"
        size="4xl"
        {...componentOuiaProps(ouiaId, 'page-title', ouiaSafe)}
      >
        {title}
      </Title>
    </React.Fragment>
  );
};

export default PageTitle;
