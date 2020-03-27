import {
  Breadcrumb,
  BreadcrumbItem,
  Grid,
  GridItem,
  Page,
  PageSection,
  Title,
  Card,
  Bullseye
} from '@patternfly/react-core';
import React from 'react';
import { Link, Redirect } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessDiagram from '../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';
import { useGetProcessInstanceByIdQuery } from '../../../graphql/types';
import ProcessDescriptor from '../../Molecules/ProcessDescriptor/ProcessDescriptor';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';

const ProcessDetailsPage = ({ match }) => {
  const id = match.params.instanceID;

  const { loading, error, data } = useGetProcessInstanceByIdQuery({
    variables: { id }
  });

  if (data) {
    const result = data.ProcessInstances;
    if (result.length === 0) {
      return (
        <Redirect
          to={{
            pathname: '/NoData',
            state: {
              prev: location.pathname,
              title: 'Process not found',
              description: `Process instance with the id ${id} not found`,
              buttonText: 'Go to process instances'
            }
          }}
        />
      );
    }
  }

  return (
    <>
      <PageSection variant="light">
        <PageTitleComponent title="Process Details" />
          {!loading ?
          (<Grid gutter="md" span={12} lg={6} xl={4}>
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
          </Grid>
        ) : ''}
        </PageSection>
        <PageSection>
        {!loading ?
        (
          <Grid gutter="md" span={12} lg={6} xl={4}>
          <GridItem span={12}>
              <Title headingLevel="h1" size="4xl">
                <ProcessDescriptor
                  processInstanceData={data.ProcessInstances[0]}
                />
              </Title>
            </GridItem>
            <GridItem>
              <ProcessDetails data={data} />
            </GridItem>
            <GridItem>
              <ProcessDetailsProcessVariables data={data} />
            </GridItem>
            <GridItem>
              <ProcessDetailsTimeline
                data={data.ProcessInstances}
              />
            </GridItem>
          </Grid>): (
            <Card>
              <Bullseye>
            <SpinnerComponent spinnerText="Loading process details..." />
            </Bullseye>
            </Card>
          )}
        </PageSection>
    </>
  );
};

export default ProcessDetailsPage;
