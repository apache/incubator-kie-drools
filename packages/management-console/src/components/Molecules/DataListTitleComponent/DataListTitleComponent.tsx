import React from 'react';
import { Title, TextContent, Text, TextVariants } from '@patternfly/react-core';

export interface IOwnProps {}

const DataListTitleComponent: React.FC<IOwnProps> = () => {
  return (
    <React.Fragment>
      <Title
        headingLevel="h1"
        size="4xl"
        style={{
          backgroundColor: 'white',
          paddingTop: '21px',
          paddingLeft: '53px',
          paddingBottom: '15px',
          fontWeight: 500,
          width: '100%'
        }}
      >
        Instances
      </Title>
      <TextContent>
        <Text
          component={TextVariants.p}
          style={{ backgroundColor: 'white', marginTop: '-13px', paddingLeft: '55px', paddingBottom: '29px' }}
        >
          Some generalized information about the instances. This page gives you general information , and you can filter
          the list and click the details to get more information.There would be similar pages for Jobs,Errors and
          Complete as well
        </Text>
      </TextContent>
    </React.Fragment>
  );
};

export default DataListTitleComponent;
