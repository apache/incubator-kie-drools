import React from 'react';
import {
  CardBody,
  Card,
  CardHeader,
  Title,
  TextContent,
  TextVariants,
  Label,
  Text,
  Tooltip
} from '@patternfly/react-core';
import { GraphQL } from '@kogito-apps/common';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { InfoCircleIcon } from '@patternfly/react-icons';
import './ProcessDetailsMilestones.css';
import Milestone = GraphQL.Milestone;

interface IOwnProps {
  milestones?: Pick<Milestone, 'id' | 'name' | 'status'>[];
}
const ProcessDetailsMilestones: React.FC<IOwnProps & OUIAProps> = ({
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
        <TextContent className="kogito-management-console--milestones__nameText">
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
                    <span className="kogito-management-console--milestones__nameTextEllipses">
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

export default ProcessDetailsMilestones;
