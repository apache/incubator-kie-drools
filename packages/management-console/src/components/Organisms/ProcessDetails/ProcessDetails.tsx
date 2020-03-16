import Moment from 'react-moment';
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
import {
  LevelDownAltIcon,
  LevelUpAltIcon,
  OnRunningIcon,
  CheckCircleIcon,
  BanIcon,
  PausedIcon,
  ErrorCircleOIcon
} from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import { ProcessInstanceState } from '../../../graphql/types';

interface IOwnProps {
  loading: boolean;
  data: any;
}
const ProcessDetails: React.FC<IOwnProps> = ({ data, loading }) => {
  const stateIconCreator = state => {
    switch (state) {
      case ProcessInstanceState.Active:
        return (
          <>
            <OnRunningIcon className="pf-u-mr-sm" />
            Active
          </>
        );
      case ProcessInstanceState.Completed:
        return (
          <>
            <CheckCircleIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--success-color--100)"
            />
            Completed
          </>
        );
      case ProcessInstanceState.Aborted:
        return (
          <>
            <BanIcon className="pf-u-mr-sm" />
            Aborted
          </>
        );
      case ProcessInstanceState.Suspended:
        return (
          <>
            <PausedIcon className="pf-u-mr-sm" />
            Suspended
          </>
        );
      case ProcessInstanceState.Error:
        return (
          <>
            <ErrorCircleOIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--danger-color--100)"
            />
            Error
          </>
        );
    }
  };

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
              {stateIconCreator(data.ProcessInstances[0].state)}
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
                <Moment fromNow>
                  {new Date(`${data.ProcessInstances[0].start}`)}
                </Moment>
              </Text>
            ) : (
                ''
              )}
          </FormGroup>
          <FormGroup label="End" fieldId="end">
            {data.ProcessInstances[0].end ? (
              <Text component={TextVariants.p}>
                <Moment fromNow>
                  {new Date(`${data.ProcessInstances[0].end}`)}
                </Moment>
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
