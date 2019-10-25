import React from 'react';
import { Title, TextContent, Text, TextVariants } from '@patternfly/react-core';

export interface IOwnProps {}

const DataListTitleComponent: React.FC<IOwnProps> = () => {
  return (
    <React.Fragment>
      <Title headingLevel="h1" size="4xl">
        Instances
      </Title>
      <TextContent>
        <Text component={TextVariants.p}>
          Some generalized information about the instances. This page gives you general information, and you can filter
          the list and click the details to get more information.There would be similar pages for jobs, errors and
          completed instances as well.
        </Text>
      </TextContent>
    </React.Fragment>
  );
};

export default DataListTitleComponent;
