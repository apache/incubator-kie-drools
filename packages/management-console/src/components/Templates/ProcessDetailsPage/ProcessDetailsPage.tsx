import {
  Breadcrumb,
  BreadcrumbItem,
  Grid,
  GridItem,
  Page,
  PageSection,
  Title
} from '@patternfly/react-core';
import React from 'react';
import { Link, Redirect } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessDiagram from '../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';
import { useGetProcessInstanceByIdQuery } from '../../../graphql/types';

const ProcessDetailsPage = ({ match }) => {
  const id = match.params.instanceID;

  const { loading, error, data } = useGetProcessInstanceByIdQuery({
    variables: { id }
  });

  if (data) {
    const result = data.ProcessInstances;
    if (result.length === 0) {
      return <Redirect to={{
        pathname: '/NoData', state: {
          prev: location.pathname,
          title: 'Process not found', description: `Process instance with the id ${id} not found`,
          buttonText: 'Go to process instances'
        }
      }} />
    }
  }

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <>
      <Page>
        <PageSection isFilled={true}>
          <Grid gutter="md" span={12} lg={6} xl={4}>
            <GridItem span={12}>
              <Breadcrumb>
                <BreadcrumbItem>
                  <Link to={'/'}>Home</Link>
                </BreadcrumbItem>
                <BreadcrumbItem>
                  <Link to={'/ProcessInstances/'}>Process Instances</Link>
                </BreadcrumbItem>
                <BreadcrumbItem isActive>
                  {data.ProcessInstances[0].processName}
                </BreadcrumbItem>
              </Breadcrumb>
            </GridItem>
            <GridItem span={12}>
              <Title headingLevel="h1" size="4xl">
                {data.ProcessInstances[0].processName}
              </Title>
            </GridItem>
            <GridItem>
              <ProcessDetails loading={loading} data={data} />
            </GridItem>
            <GridItem>
              <ProcessDetailsProcessVariables loading={loading} data={data} />
            </GridItem>
            <GridItem>
              <ProcessDetailsTimeline
                loading={loading}
                data={data.ProcessInstances}
              />
            </GridItem>
          </Grid>
        </PageSection>
      </Page>
    </>
  );
};

export default ProcessDetailsPage;
