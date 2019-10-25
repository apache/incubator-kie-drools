import {Page, SkipToContent} from '@patternfly/react-core';
import React from 'react';
import {Redirect, Route} from 'react-router-dom';
import HeaderComponent from '../../Organisms/PageHeaderComponent/HeaderComponent';
import DataListComponent from '../DataListComponent/DataListComponent';
import ProcessDetailsPage from '../ProcessDetailsPage/ProcessDetailsPage';
import './Dashboard.css';

const Dashboard: React.FC<{}> = () => {
    const pageId = 'main-content-page-layout-default-nav';
    const PageSkipToContent = <SkipToContent href={`#${pageId}`}>Skip to Content</SkipToContent>;

    return (
        <React.Fragment>
            <Page header={
                <HeaderComponent/>} skipToContent={PageSkipToContent} mainContainerId={pageId} className="page">
                <Route exact path="/" render={() => <Redirect to="/ProcessInstances"/>}/>
                <Route exact path="/ProcessInstances" component={DataListComponent}/>
                <Route exact path="/ProcessInstances/:instanceID" component={ProcessDetailsPage}/>
            </Page>
        </React.Fragment>
    );
};

export default Dashboard;
