import React from 'react';
import {
  AboutModal,
  TextContent,
  Text,
  TextList,
  TextListItem
} from '@patternfly/react-core';
import './AboutModal.css';
import aboutPageLogo from '../../../static/managementConsoleLogo.svg';
import aboutPageBackground from '../../../static/KogitoAbout.png';
import { version } from '../../../../package.json';
export interface IOwnProps {
  isOpenProp: boolean;
  handleModalToggleProp: any;
}
const AboutModalBox: React.FC<IOwnProps> = ({
  isOpenProp,
  handleModalToggleProp
}) => {
  return (
    <AboutModal
      isOpen={isOpenProp}
      onClose={handleModalToggleProp}
      trademark="Management Console is part of Kogito, an open source software released under the Apache Software License 2.0"
      brandImageAlt="Kogito Logo"
      brandImageSrc={aboutPageLogo}
      backgroundImageSrc={aboutPageBackground}
    >
      <TextContent>
        <Text component="h5" />
        <TextList component="dl">
          <TextListItem component="dt">Version: </TextListItem>
          <TextListItem component="dd">{version}</TextListItem>
          <TextListItem component="dt">License information: </TextListItem>
          <TextListItem component="dd">
            <a
              href="https://github.com/kiegroup/kogito-runtimes/blob/master/LICENSE"
              target="_blank"
            >
              https://github.com/kiegroup/kogito-runtimes/blob/master/LICENSE
            </a>
          </TextListItem>
          <TextListItem component="dt">Report a bug: </TextListItem>
          <TextListItem component="dd">
            <a href="https://issues.redhat.com/projects/KOGITO" target="_blank">
              https://issues.redhat.com/projects/KOGITO
            </a>
          </TextListItem>
          <TextListItem component="dt">Get involved/help/docs: </TextListItem>
          <TextListItem component="dd">
            <a
              href="https://github.com/kiegroup/kogito-runtimes/wiki"
              target="_blank"
            >
              https://github.com/kiegroup/kogito-runtimes/wiki
            </a>
          </TextListItem>
          <TextListItem component="dt">Kogito URL: </TextListItem>
          <TextListItem component="dd">
            <a href="http://kogito.kie.org" target="_blank">
              http://kogito.kie.org
            </a>
          </TextListItem>
          <TextListItem component="dt">Data-Index URL: </TextListItem>
          <TextListItem component="dd">
            <a href={process.env.KOGITO_DATAINDEX_URL} target="_blank">
              {process.env.KOGITO_DATAINDEX_HTTP_URL}
            </a>
          </TextListItem>
        </TextList>
      </TextContent>
    </AboutModal>
  );
};

export default AboutModalBox;
