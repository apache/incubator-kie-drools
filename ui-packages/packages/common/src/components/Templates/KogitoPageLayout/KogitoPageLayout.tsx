import {
  Page,
  PageSidebar,
  PageHeader,
  Brand,
  PageHeaderTools
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import PageToolbar from '../../Molecules/PageToolbar/PageToolbar';
import { aboutLogoContext } from '../../contexts';
import '../../styles.css';

import { ouiaAttribute } from '@kogito-apps/ouia-tools';

interface IOwnProps {
  children: React.ReactNode;
  BrandSrc: string;
  PageNav: React.ReactNode;
  pageNavOpen?: boolean;
  BrandAltText: string;
  BrandClick: () => void;
}

const KogitoPageLayout: React.FC<IOwnProps> = ({
  children,
  BrandSrc,
  PageNav,
  pageNavOpen,
  BrandAltText,
  BrandClick
}) => {
  const pageId = 'main-content-page-layout-default-nav';
  const [isNavOpen, setIsNavOpen] = useState(
    pageNavOpen != undefined ? pageNavOpen : true
  );
  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  useEffect(() => {
    if (document.getElementById(pageId)) {
      document.getElementById(pageId).setAttribute('data-ouia-main', 'true');
    }
  });

  const Header = (
    <PageHeader
      logo={<Brand src={BrandSrc} alt={BrandAltText} onClick={BrandClick} />}
      headerTools={
        <PageHeaderTools>
          <aboutLogoContext.Provider value={BrandSrc}>
            <PageToolbar />
          </aboutLogoContext.Provider>
        </PageHeaderTools>
      }
      showNavToggle
      isNavOpen={isNavOpen}
      onNavToggle={onNavToggle}
      {...ouiaAttribute('data-ouia-header', 'true')}
    />
  );

  const Sidebar = (
    <PageSidebar
      nav={PageNav}
      isNavOpen={isNavOpen}
      theme="dark"
      {...ouiaAttribute('data-ouia-navigation', 'true')}
    />
  );

  return (
    <React.Fragment>
      <Page
        header={Header}
        mainContainerId={pageId}
        sidebar={Sidebar}
        className="kogito-common--PageLayout"
      >
        {children}
      </Page>
    </React.Fragment>
  );
};

export default KogitoPageLayout;
