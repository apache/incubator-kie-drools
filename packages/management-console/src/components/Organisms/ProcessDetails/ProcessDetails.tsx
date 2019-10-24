import React from 'react';
import {
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Grid,
  GridItem,
  Button,
  Text,
  TextVariants,
  TextContent
} from '@patternfly/react-core';

const ProcessDetails = ({ loading, data }) => {
  const DetailsStyle = {
    marginLeft: '2em',
    height: '25em'
  };
  return (
    <Card style={DetailsStyle}>
      <CardHeader>Details</CardHeader>
      <CardBody>
        <Grid gutter="md">
          <GridItem span={6}>
            <TextContent>
              <Text component={TextVariants.h4} className="--pf-global--FontSize--md">
                Defintion Id
              </Text>
              <Text component={TextVariants.h4}>Instance State</Text>
            </TextContent>
          </GridItem>
          <GridItem span={6}>
            <TextContent>
              {!loading ? (
                data.ProcessId.map(item => {
                  return (
                    <div key={item.id}>
                      <Text component={TextVariants.h4}>{item.processId}</Text>
                      <Text component={TextVariants.h4}>{item.state}</Text>
                    </div>
                  );
                })
              ) : (
                <Text component={TextVariants.h4}>Loading...</Text>
              )}
            </TextContent>
          </GridItem>
        </Grid>
      </CardBody>
      <CardFooter>
        <Button variant="primary" style={{ float: 'right' }}>
          Primary
        </Button>
      </CardFooter>
    </Card>
  );
};

export default ProcessDetails;
