import { useQuery } from '@apollo/react-hooks';
import {
  Breadcrumb,
  BreadcrumbItem,
  Grid,
  GridItem,
  Page,
  PageSection,
  Title
} from '@patternfly/react-core';
import gql from 'graphql-tag';
import React from 'react';
import { Link } from 'react-router-dom';
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessDiagram from '../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram';
import ProcessDetailsProcessVariables from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';

const ProcessDetailsPage = ({ match }) => {
  const id = match.params.instanceID;
  const GET_PROCESS_INSTANCE = gql`
    query getProcessInstanceById($id: String) {
      ProcessInstances(where: { id: { equal: $id } }) {
        id
        processId
        processName
        parentProcessInstanceId
        roles
        variables
        state
        lastUpdate
        start
        end
        endpoint
        childProcessInstanceId
        nodes {
          id
          name
          type
          enter
          exit
        }
      }
    }
  `;
  const { data, loading, error } = useQuery(GET_PROCESS_INSTANCE, {
    variables: { id }
  });

  {
    if (loading) {
      return <p>Loading...</p>;
    }
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
