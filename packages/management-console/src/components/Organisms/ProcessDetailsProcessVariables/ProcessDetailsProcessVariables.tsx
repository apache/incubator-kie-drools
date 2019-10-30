import { Card, CardBody, CardFooter, CardHeader, Text, TextContent, TextVariants } from '@patternfly/react-core';
import React from 'react';
import ReactJson from 'react-json-view';

const ProcessDetailsProcessVariables = ({ loading, data }) => {
  return (
    <Card style={{ overflowX: 'auto' }}>
      <CardHeader>Process Variables</CardHeader>
      <CardBody>
        <TextContent style={{ width: '30em' }}>
          {!loading ? (
            data.ProcessInstances.map((item, index) => {
              return (
                <div key={index}>
                  <ReactJson src={JSON.parse(item.variables)}/>
                </div>
              );
            })
          ) : (
            <Text component={TextVariants.h4}>Loading...</Text>
          )}
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsProcessVariables;
