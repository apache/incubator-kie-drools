import {
  Card,
  Grid,
  GridItem,
  PageSection,
  Expandable,
  InjectedOuiaProps,
  withOuiaContext
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import './DataListExpandable.css';
import TaskListByState from '../../Organisms/TaskListByState/TaskListByState';
import { ouiaPageTypeAndObjectId } from '@kogito-apps/common';

const DataListContainerExpandable: React.FC<InjectedOuiaProps> = ({
  ouiaContext
}) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const onToggle = () => {
    setIsExpanded(!isExpanded);
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'user-tasks');
  });

  return (
    <React.Fragment>
      <UserTaskPageHeader />
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="dataList">
              <Expandable
                toggleText={isExpanded ? 'READY Show Less' : 'READY Show More'}
                onToggle={onToggle}
                isExpanded={isExpanded}
              >
                <TaskListByState currentState={'Ready'} />
              </Expandable>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default withOuiaContext(DataListContainerExpandable);
