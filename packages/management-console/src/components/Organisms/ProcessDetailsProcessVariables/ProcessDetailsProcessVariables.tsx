import {
  Card,
  CardBody,
  CardHeader,
  Text,
  TextContent,
  TextVariants,
  Title
} from '@patternfly/react-core';
import React from 'react';
import ReactJson from 'react-json-view';

const ProcessDetailsProcessVariables = ({ loading, data }) => {
  return (
    <Card>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Process Variables
        </Title>
      </CardHeader>
      <CardBody>
        <TextContent>
          {!loading ? (
            data.ProcessInstances.map((item, index) => {
              return (
                <div key={index}>
                  <ReactJson src={JSON.parse(item.variables)} />
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
