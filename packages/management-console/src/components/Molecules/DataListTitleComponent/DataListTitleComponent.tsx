import React from 'react';
import { Title, TextContent, Text, TextVariants } from '@patternfly/react-core';

export interface IOwnProps {}

const DataListTitleComponent: React.FC<IOwnProps> = () => {
  return (
    <React.Fragment>
      <Title headingLevel="h1" size="4xl">
        Process Instances
      </Title>
    </React.Fragment>
  );
};

export default DataListTitleComponent;
