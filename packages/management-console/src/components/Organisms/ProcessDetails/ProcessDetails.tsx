import { TimeAgo } from '@n1ru4l/react-time-ago';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Form,
  FormGroup,
  Text,
  TextVariants,
  Title,
  Tooltip
} from '@patternfly/react-core';
import React from 'react';
import { Link } from 'react-router-dom';
import { LevelDownAltIcon, LevelUpAltIcon } from '@patternfly/react-icons';

const ProcessDetails = ({ loading, data }) => {
  return (
    <Card>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Details
        </Title>
      </CardHeader>
      <CardBody>
        <Form>
          <FormGroup label="Name" fieldId="name">
            <Text component={TextVariants.p}>
              {data.ProcessInstances[0].processName}
            </Text>
          </FormGroup>
          <FormGroup label="State" fieldId="state">
            <Text component={TextVariants.p}>
              {data.ProcessInstances[0].state}
            </Text>
          </FormGroup>
          <FormGroup label="Id" fieldId="id">
            <Text component={TextVariants.p}>
              {data.ProcessInstances[0].id}
            </Text>
          </FormGroup>
          <FormGroup label="Endpoint" fieldId="endpoint">
            {data.ProcessInstances[0].endpoint ? (
              <Text component={TextVariants.p}>
                {data.ProcessInstances[0].endpoint}
              </Text>
            ) : (
              ''
            )}
          </FormGroup>
          <FormGroup label="Start" fieldId="start">
            {data.ProcessInstances[0].start ? (
              <Text component={TextVariants.p}>
                <TimeAgo
                  date={new Date(`${data.ProcessInstances[0].start}`)}
                  render={({ error, value }) => <span>{value}</span>}
                />
              </Text>
            ) : (
              ''
            )}
          </FormGroup>
          <FormGroup label="End" fieldId="end">
            {data.ProcessInstances[0].end ? (
              <Text component={TextVariants.p}>
                <TimeAgo
                  date={new Date(`${data.ProcessInstances[0].end}`)}
                  render={({ error, value }) => <span>{value}</span>}
                />
              </Text>
            ) : (
              ''
            )}
          </FormGroup>
          <FormGroup label="Last Updated" fieldId="lastUpdate">
            {data.ProcessInstances[0].lastUpdate ? (
              <Text component={TextVariants.p}>
                <TimeAgo
                  date={new Date(`${data.ProcessInstances[0].lastUpdate}`)}
                  render={({ error, value }) => <span>{value}</span>}
                />
              </Text>
            ) : (
              ''
            )}
          </FormGroup>
          {data.ProcessInstances[0].parentProcessInstanceId ? (
            <FormGroup label="Parent Process" fieldId="parent">
              <div>
                <Tooltip
                  content={data.ProcessInstances[0].parentProcessInstanceId}
                >
                  <Button
                    component="a"
                    href={
                      '/ProcessInstances/' +
                      data.ProcessInstances[0].parentProcessInstanceId
                    }
                    variant="link"
                    icon={<LevelUpAltIcon />}
                  >
                    {data.ProcessInstances[0].parentProcessInstanceId}
                  </Button>
                </Tooltip>
              </div>
            </FormGroup>
          ) : (
            ''
          )}
          {data.ProcessInstances[0].childProcessInstanceId ? (
            <FormGroup label="Sub Processes" fieldId="parent">
              {data.ProcessInstances[0].childProcessInstanceId.map(
                (child, index) => (
                  <div key={child}>
                    <Tooltip content={child}>
                      <Button
                        component="a"
                        href={'/ProcessInstances/' + child}
                        key={child}
                        variant="link"
                        icon={<LevelDownAltIcon />}
                      >
                        {child}
                      </Button>
                    </Tooltip>
                  </div>
                )
              )}
            </FormGroup>
          ) : (
            ''
          )}
        </Form>
      </CardBody>
    </Card>
  );
};

export default ProcessDetails;
