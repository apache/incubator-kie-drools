import {
  Card,
  Grid,
  GridItem,
  PageSection,
  ExpandableSection
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import './DataListExpandable.css';
import TaskListByState from '../../Organisms/TaskListByState/TaskListByState';
import {
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
  OUIAProps
} from '@kogito-apps/common';

const DataListContainerExpandable: React.FC<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const onToggle = () => {
    setIsExpanded(!isExpanded);
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId('user-tasks', 'true');
  });

  return (
    <React.Fragment>
      <div
        {...componentOuiaProps(ouiaId, 'DataListContainerExpandable', ouiaSafe)}
      >
        <UserTaskPageHeader />
        <PageSection>
          <Grid hasGutter md={1}>
            <GridItem span={12}>
              <Card className="dataList">
                <ExpandableSection
                  toggleText={
                    isExpanded ? 'READY Show Less' : 'READY Show More'
                  }
                  onToggle={onToggle}
                  isExpanded={isExpanded}
                >
                  <TaskListByState currentState={'Ready'} />
                </ExpandableSection>
              </Card>
            </GridItem>
          </Grid>
        </PageSection>
      </div>
    </React.Fragment>
  );
};

export default DataListContainerExpandable;
