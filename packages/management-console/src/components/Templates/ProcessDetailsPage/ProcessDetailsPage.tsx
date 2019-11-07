import {useQuery} from '@apollo/react-hooks';
import {Breadcrumb, BreadcrumbItem, Grid, GridItem, Page, PageSection} from '@patternfly/react-core';
import gql from 'graphql-tag';
import React from 'react';
import {Link} from "react-router-dom";
import ProcessDetails from '../../Organisms/ProcessDetails/ProcessDetails';
import ProcessDetailsProcessDiagram from '../../Organisms/ProcessDetailsProcessDiagram/ProcessDetailsProcessDiagram';
import ProcessDetailsProcessVariables
    from '../../Organisms/ProcessDetailsProcessVariables/ProcessDetailsProcessVariables';
import ProcessDetailsTimeline from '../../Organisms/ProcessDetailsTimeline/ProcessDetailsTimeline';
import './ProcessDetailsPage.css';

const ProcessDetailsPage = ({match}) => {
    const id = match.params.instanceID;
    const GET_QUERY = gql`
        query getQuery($id: [String!]) {
            ProcessInstances(filter: { id: $id }) {
                id
                processId
                processName
                parentProcessInstanceId
                roles
                variables
                state
                start
                end
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
    const {data, loading, error} = useQuery(GET_QUERY, {
        variables: {id: [id]}
    });

    const PageSectionStyle = {
        height: '100%'
    };

    {
        if (loading) {
            return <p>Loading...</p>;
        }
    }
    return (
        <>
            <Page>
                <PageSection style={PageSectionStyle}>
                    <Grid gutter="md" className="pf-m-all-12-col pf-m-all-6-col-on-lg pf-m-all-4-col-on-xl">
                        <GridItem span={12}>
                            <Breadcrumb>
                                <BreadcrumbItem><Link to={'/'}>Home</Link></BreadcrumbItem>
                                <BreadcrumbItem><Link to={'/ProcessInstances/'}>Process Instances</Link></BreadcrumbItem>
                                <BreadcrumbItem isActive>{data.ProcessInstances[0].processName}</BreadcrumbItem>
                            </Breadcrumb>
                        </GridItem>
                        <GridItem>
                            <ProcessDetails loading={loading} data={data}/>
                        </GridItem>
                        <GridItem>
                            <ProcessDetailsProcessVariables loading={loading} data={data}/>
                        </GridItem>
                        <GridItem>
                            <ProcessDetailsTimeline loading={loading} data={data.ProcessInstances}/>
                        </GridItem>
                    </Grid>
                </PageSection>
            </Page>
        </>
    );
};

export default ProcessDetailsPage;
