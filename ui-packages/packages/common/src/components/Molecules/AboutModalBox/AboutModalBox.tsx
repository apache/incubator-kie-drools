import React, { useContext } from 'react';
import {
  AboutModal,
  TextContent,
  Text,
  TextList,
  TextListItem
} from '@patternfly/react-core';
import '../../styles.css';
import { aboutLogoContext } from '../../contexts';
import aboutPageBackground from '../../../static/kogitoAbout.png';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';

export interface IOwnProps {
  isOpenProp: boolean;
  handleModalToggleProp: any;
}
declare global {
  interface Window {
    DATA_INDEX_ENDPOINT: string;
  }
}
const AboutModalBox: React.FC<IOwnProps & OUIAProps> = ({
  isOpenProp,
  handleModalToggleProp,
  ouiaId,
  ouiaSafe
}) => {
  const dataIndexURL =
    window.DATA_INDEX_ENDPOINT || process.env.KOGITO_DATAINDEX_HTTP_URL;
  const logoSrc = useContext(aboutLogoContext);
  return (
    <AboutModal
      isOpen={isOpenProp}
      onClose={handleModalToggleProp}
      trademark={`${process.env.KOGITO_APP_NAME} is part of Kogito, an open source software released under the Apache Software License 2.0`}
      brandImageAlt="Kogito Logo"
      brandImageSrc={logoSrc}
      className="kogito-common--aboutModalBox"
      backgroundImageSrc={aboutPageBackground}
      {...componentOuiaProps(ouiaId, 'AboutModalBox', ouiaSafe)}
    >
      <TextContent>
        <Text component="h5" />
        <TextList component="dl">
          <TextListItem component="dt">Version: </TextListItem>
          <TextListItem component="dd">
            {process.env.KOGITO_APP_VERSION}
          </TextListItem>
          <TextListItem component="dt">License information: </TextListItem>
          <TextListItem component="dd">
            <a
              href="https://github.com/kiegroup/kogito-runtimes/blob/main/LICENSE"
              target="_blank"
              rel="noreferrer"
            >
              https://github.com/kiegroup/kogito-runtimes/blob/main/LICENSE
            </a>
          </TextListItem>
          <TextListItem component="dt">Report a bug: </TextListItem>
          <TextListItem component="dd">
            <a
              href="https://issues.redhat.com/projects/KOGITO"
              target="_blank"
              rel="noreferrer"
            >
              https://issues.redhat.com/projects/KOGITO
            </a>
          </TextListItem>
          <TextListItem component="dt">Get involved/help/docs: </TextListItem>
          <TextListItem component="dd">
            <a
              href="https://docs.jboss.org/kogito/release/latest/html_single/"
              target="_blank"
              rel="noreferrer"
            >
              https://docs.jboss.org/kogito/release/latest/html_single/
            </a>
          </TextListItem>
          <TextListItem component="dt">Kogito URL: </TextListItem>
          <TextListItem component="dd">
            <a href="http://kogito.kie.org" target="_blank" rel="noreferrer">
              http://kogito.kie.org
            </a>
          </TextListItem>
          <TextListItem component="dt">Data-Index URL: </TextListItem>
          <TextListItem component="dd">{dataIndexURL} </TextListItem>
        </TextList>
      </TextContent>
    </AboutModal>
  );
};

export default AboutModalBox;
