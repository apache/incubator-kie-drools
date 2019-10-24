import React from 'react';
import {
    Card,
    CardHeader,
    CardBody
} from '@patternfly/react-core';
import { url } from './Url';

const ProcessDetailsProcessDiagram = () => {
    return (
        <Card style={{overflowX: 'scroll', overflowY: 'scroll'}}>
            <CardHeader>Process Diagram</CardHeader>
            <CardBody><img src={url} /></CardBody>
        </Card>

    )
}

export default ProcessDetailsProcessDiagram;