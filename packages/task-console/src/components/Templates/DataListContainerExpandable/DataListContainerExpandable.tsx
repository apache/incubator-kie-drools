import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection,
  Expandable
} from '@patternfly/react-core';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import './DataListExpandable.css';
import DataListComponentByState from '../../Organisms/DataListComponentByState/DataListComponentByState';

const DataListContainerExpandable: React.FC<{}> = () => {
  const [isExpanded, setIsExpanded] = useState(false);

  const onToggle = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitleComponent title="User Tasks" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>User Tasks</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="dataList">
              <Expandable toggleText={isExpanded ? 'READY Show Less' : 'READY Show More'} onToggle={onToggle} isExpanded={isExpanded}>
               <DataListComponentByState
                  currentState={'Ready'}
                />
              </Expandable>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default DataListContainerExpandable;
