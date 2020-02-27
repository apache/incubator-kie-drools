import Moment from 'react-moment'
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
import { LevelDownAltIcon, LevelUpAltIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';

interface IOwnProps {
  loading: boolean;
  data: any;
}
const ProcessDetails: React.FC<IOwnProps> = ({ data, loading }) => {
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
                <Moment fromNow>{new Date(`${data.ProcessInstances[0].start}`)}</Moment>
              </Text>
            ) : (
                ''
              )}
          </FormGroup>
          <FormGroup label="End" fieldId="end">
            {data.ProcessInstances[0].end ? (
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${data.ProcessInstances[0].end}`)}</Moment>
              </Text>
            ) : (
                ''
              )}
          </FormGroup>
          {!loading &&
            data.ProcessInstances[0].parentProcessInstance !== null ? (
              <FormGroup label="Parent Process" fieldId="parent">
                <div>
                  <Link
                    to={
                      '/ProcessInstances/' +
                      data.ProcessInstances[0].parentProcessInstance.id
                    }
                  >
                    <Tooltip
                      content={data.ProcessInstances[0].parentProcessInstance.id}
                    >
                      <Button variant="link" icon={<LevelUpAltIcon />}>
                        {
                          data.ProcessInstances[0].parentProcessInstance
                            .processName
                        }
                      </Button>
                    </Tooltip>
                  </Link>
                </div>
              </FormGroup>
            ) : (
              <div />
            )}

          {!loading &&
            data.ProcessInstances[0].childProcessInstances.length !== 0 ? (
              <FormGroup label="Sub Processes" fieldId="child">
                {data.ProcessInstances[0].childProcessInstances.map(
                  (child, index) => (
                    <div key={child.id}>
                      <Link to={'/ProcessInstances/' + child.id}>
                        <Tooltip content={child.id}>
                          <Button variant="link" icon={<LevelDownAltIcon />}>
                            {child.processName}
                          </Button>
                        </Tooltip>
                      </Link>
                    </div>
                  )
                )}
              </FormGroup>
            ) : (
              <div />
            )}
        </Form>
      </CardBody>
    </Card>
  );
};

export default ProcessDetails;
