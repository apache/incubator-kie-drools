import React, { useState } from 'react';
import {
  PageSection,
  Bullseye,
  EmptyState,
  EmptyStateIcon,
  EmptyStateVariant,
  Button,
  EmptyStateBody,
  Title,
  Page,
  SkipToContent,
  PageSidebar,
  PageHeader,
  Nav,
  NavList,
  NavItem,
  Brand
} from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
import { BrowserRouter as Router, Link } from 'react-router-dom';
import Avatar from '../../Atoms/AvatarComponent/AvatarComponent';
import PageToolbarComponent from '../../Organisms/PageToolbarComponent/PageToolbarComponent';
import managementConsoleLogo from '../../../static/managementConsoleLogo.svg';

const NoServerComponent = props => {
  const [isNavOpen, setIsNavOpen] = useState(true);
  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  const Header = (
    <PageHeader
      logo={<Brand src={managementConsoleLogo} alt="Management Console Logo" />}
      toolbar={<PageToolbarComponent />}
      avatar={<Avatar />}
      showNavToggle
      isNavOpen={isNavOpen}
      onNavToggle={onNavToggle}
    />
  );

  const PageNav = (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        <NavItem>
          <Link to="/ProcessInstances">Process Instances</Link>
        </NavItem>
        <NavItem>
          <Link to="/DomainExplorer">Domain Explorer</Link>
        </NavItem>
      </NavList>
    </Nav>
  );
  const Sidebar = (
    <PageSidebar nav={PageNav} isNavOpen={isNavOpen} theme="dark" />
  );

  const pageId = 'main-content-page-layout-default-nav';
  const PageSkipToContent = (
    <SkipToContent href={`#${pageId}`}>Skip to Content</SkipToContent>
  );

  return (
    <>
      <Router>
        <Page
          header={Header}
          skipToContent={PageSkipToContent}
          mainContainerId={pageId}
          sidebar={Sidebar}
          isManagedSidebar
          className="kogito-management-console--dashboard-page"
        >
          <PageSection variant="light">
            <Bullseye>
              <EmptyState variant={EmptyStateVariant.full}>
                <EmptyStateIcon
                  icon={ExclamationCircleIcon}
                  size="md"
                  color="var(--pf-global--danger-color--100)"
                />
                <Title headingLevel="h1" size="4xl">
                  Error connecting server
                </Title>
                <EmptyStateBody>
                  The management console could not access the server to display
                  content.
                </EmptyStateBody>
                <EmptyStateBody>
                  Try reloading the page, or contact your administrator for more
                  information.
                </EmptyStateBody>
                <Button
                  variant="primary"
                  onClick={() => window.location.reload()}
                >
                  Refresh               
                </Button>
              </EmptyState>
            </Bullseye>
          </PageSection>
        </Page>
      </Router>
    </>
  );
};

export default NoServerComponent;
