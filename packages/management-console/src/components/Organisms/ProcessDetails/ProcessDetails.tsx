import {TimeAgo} from '@n1ru4l/react-time-ago';
import {Button, Card, CardBody, CardHeader, Form, FormGroup, Text, TextVariants, Title} from '@patternfly/react-core';
import React from 'react';
import {Link} from "react-router-dom";

const ProcessDetails = ({loading, data}) => {
    return (
        <Card>
            <CardHeader>
                <Title headingLevel="h3" size="xl">Details</Title>
            </CardHeader>
            <CardBody>
                <Form>
                    <FormGroup label="Name" fieldId="name">
                        <Text component={TextVariants.p}>{data.ProcessInstances[0].processName}</Text>
                    </FormGroup>
                    <FormGroup label="State" fieldId="state">
                        <Text component={TextVariants.p}>{data.ProcessInstances[0].state}</Text>
                    </FormGroup>
                    <FormGroup label="Id" fieldId="id">
                        <Text component={TextVariants.p}>{data.ProcessInstances[0].id}</Text>
                    </FormGroup>
                    <FormGroup label="Endpoint" fieldId="endpoint">
                    {data.ProcessInstances[0].endpoint ?
                        <Text component={TextVariants.p}>{data.ProcessInstances[0].endpoint}</Text>
                        : '' }
                    </FormGroup> 
                    <FormGroup label="Start" fieldId="start">
                    {data.ProcessInstances[0].start ?
                        <Text component={TextVariants.p}>
                            <TimeAgo date={new Date(`${data.ProcessInstances[0].start}`)} render={({error, value}) =>
                                    <span>{value}</span>}/>
                        </Text>: ''}
                    </FormGroup> 
                    <FormGroup label="End" fieldId="end">
                    {data.ProcessInstances[0].end ?
                        <Text component={TextVariants.p}>
                            <TimeAgo date={new Date(`${data.ProcessInstances[0].end}`)} render={({error, value}) =>
                                <span>{value}</span>}/>
                        </Text>
                        : ''}
                    </FormGroup> 
                    {data.ProcessInstances[0].parentProcessInstanceId ?
                        <FormGroup label="Parent Process" fieldId="parent">
                            <Text component={TextVariants.p}>
                                <Link to={'/ProcessInstances/' + data.ProcessInstances[0].parentProcessInstanceId}>
                                    <Button variant="secondary">{data.ProcessInstances[0].parentProcessInstanceId}</Button>
                                </Link>
                            </Text>
                        </FormGroup> : ''}
                    {data.ProcessInstances[0].childProcessInstanceId ?
                        <FormGroup label="Sub Processes" fieldId="parent">
                            {data.ProcessInstances[0].childProcessInstanceId.map((child, index) =>
                                <Text component={TextVariants.p} key={child} style={{marginTop: '5px'}}>
                                    <Link to={'/ProcessInstances/' + child}>
                                        <Button variant="secondary">{child}</Button>
                                    </Link>
                                </Text>)
                            }
                        </FormGroup> : ''}
                </Form>
            </CardBody>
        </Card>
    );
};

export default ProcessDetails;
