/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import {
  CardBody,
  Card,
  CardHeader
} from '@patternfly/react-core/dist/js/components/Card';
import { Label } from '@patternfly/react-core/dist/js/components/Label';
import { Tooltip } from '@patternfly/react-core/dist/js/components/Tooltip';
import { Title } from '@patternfly/react-core/dist/js/components/Title';
import {
  TextContent,
  TextVariants,
  Text
} from '@patternfly/react-core/dist/js/components/Text';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { InfoCircleIcon } from '@patternfly/react-icons/dist/js/icons/info-circle-icon';
import '../styles.css';
import { Milestone } from '@kogito-apps/management-console-shared/dist/types';

interface IOwnProps {
  milestones?: Pick<Milestone, 'id' | 'name' | 'status'>[];
}
const ProcessDetailsMilestonesPanel: React.FC<IOwnProps & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  milestones
}) => {
  const handleStatus = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return <Label icon={<InfoCircleIcon />}>Available</Label>;
      case 'ACTIVE':
        return (
          <Label color="blue" icon={<InfoCircleIcon />}>
            Active
          </Label>
        );
      case 'COMPLETED':
        return (
          <Label color="green" icon={<InfoCircleIcon />}>
            Completed
          </Label>
        );
      default:
        break;
    }
  };
  const compareObjs = (firstEle, secondEle) => {
    if (firstEle.status < secondEle.status) {
      return -1;
    }
    if (firstEle.status > secondEle.status) {
      return 1;
    }
    return 0;
  };
  const sortedMilestones = milestones.sort(compareObjs);
  return (
    <Card {...componentOuiaProps(ouiaId, 'milestones', ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Milestones
        </Title>
      </CardHeader>
      <CardBody>
        <TextContent className="kogito-process-details--milestones__nameText">
          {sortedMilestones.map((milestone, index) => {
            if (milestone.name.length > 45) {
              return (
                <Tooltip content={milestone.name} key={index}>
                  <Text
                    component={
                      milestone.status === 'COMPLETED'
                        ? TextVariants.blockquote
                        : TextVariants.p
                    }
                  >
                    <span className="kogito-process-details--milestones__nameTextEllipses">
                      {milestone.name}
                    </span>{' '}
                    {handleStatus(milestone.status)}
                  </Text>
                </Tooltip>
              );
            } else {
              return (
                <Text
                  component={
                    milestone.status === 'COMPLETED'
                      ? TextVariants.blockquote
                      : TextVariants.p
                  }
                  key={index}
                >
                  {milestone.name} {handleStatus(milestone.status)}
                </Text>
              );
            }
          })}
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsMilestonesPanel;
