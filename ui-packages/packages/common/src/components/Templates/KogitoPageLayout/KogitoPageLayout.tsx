import {
  Page,
  PageSidebar,
  PageHeader,
  Avatar,
  Brand,
  PageHeaderTools,
  OUIAProps
} from '@patternfly/react-core';
import React, { useState, useEffect } from 'react';
import PageToolbar from '../../Molecules/PageToolbar/PageToolbar';
import { aboutLogoContext } from '../../contexts';
import '../../styles.css';

import userImage from '../../../static/avatar.svg';
import { ouiaAttribute } from '../../../utils/OuiaUtils';

interface IOwnProps {
  children: React.ReactNode;
  BrandSrc: string;
  PageNav: React.ReactNode;
  BrandAltText: string;
  BrandClick: () => void;
}

const KogitoPageLayout: React.FC<IOwnProps & OUIAProps> = ({
  ouiaId,
  ...props
}) => {
  const pageId = 'main-content-page-layout-default-nav';
  const [isNavOpen, setIsNavOpen] = useState(true);
  const onNavToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

  useEffect(() => {
    if(document.getElementById(pageId)) {
      document.getElementById(pageId).setAttribute('data-ouia-main', 'true');
    }
  });

  const Header = (
    <PageHeader
      logo={
        <Brand
          src={props.BrandSrc}
          alt={props.BrandAltText}
          onClick={props.BrandClick}
        />
      }
      headerTools={
        <PageHeaderTools>
          <aboutLogoContext.Provider value={props.BrandSrc}>
            <PageToolbar />
          </aboutLogoContext.Provider>
          <Avatar src={userImage} alt="Kogito Logo" />
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
      nav={props.PageNav}
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
        {props.children}
      </Page>
    </React.Fragment>
  );
};

export default KogitoPageLayout;
