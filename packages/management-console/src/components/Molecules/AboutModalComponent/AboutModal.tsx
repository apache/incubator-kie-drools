import React, { useState } from 'react';
import { AboutModal, Button, TextContent, TextList, TextListItem } from '@patternfly/react-core';
import './AboutModal.css';
export interface IOwnProps {
  isOpenProp: boolean;
  handleModalToggleProp: any;
}

const AboutModalBox: React.FC<IOwnProps> = ({ isOpenProp, handleModalToggleProp }) => {
  return (
    <AboutModal
      isOpen={isOpenProp}
      onClose={handleModalToggleProp}
      trademark="Trademark and copyright information here"
      brandImageAlt="Kogito Logo"
      brandImageSrc={require('../../../static/kogito_about_logo.png')}
      productName="Kogito"
    >
      <TextContent>
        <TextList component="dl" style={{ width: '100%' }}>
          <TextListItem component="dt">CFME Version</TextListItem>
          <TextListItem component="dd">5.5.3.4.20102789036450</TextListItem>
          <TextListItem component="dt">Cloudforms Version</TextListItem>
          <TextListItem component="dd">4.1</TextListItem>
          <TextListItem component="dt">Server Name</TextListItem>
          <TextListItem component="dd">40DemoMaster</TextListItem>
          <TextListItem component="dt">User Name</TextListItem>
          <TextListItem component="dd">Administrator</TextListItem>
          <TextListItem component="dt">User Role</TextListItem>
          <TextListItem component="dd">EvmRole-super_administrator</TextListItem>
          <TextListItem component="dt">Browser Version</TextListItem>
          <TextListItem component="dd">601.2</TextListItem>
          <TextListItem component="dt">Browser OS</TextListItem>
          <TextListItem component="dd">Mac</TextListItem>
        </TextList>
      </TextContent>
    </AboutModal>
  );
};

export default AboutModalBox;
