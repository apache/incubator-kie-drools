/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { UncontrolledReactSVGPanZoom } from 'react-svg-pan-zoom';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import {
  ReactSvgPanZoomLoader,
  SvgLoaderSelectElement
} from 'react-svg-pan-zoom-loader';
import {
  Card,
  CardHeader,
  CardBody
} from '@patternfly/react-core/dist/js/components/Card';
import { Title } from '@patternfly/react-core/dist/js/components/Title';

interface SvgType {
  src: string;
}
interface SvgProp {
  props: SvgType;
}
interface IOwnProps {
  svg: SvgProp;
  width?: number;
  height?: number;
}

const ProcessDiagram: React.FC<IOwnProps & OUIAProps> = ({
  svg,
  width,
  height,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <>
      <Card {...componentOuiaProps(ouiaId, 'process-diagram', ouiaSafe)}>
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Diagram
          </Title>
        </CardHeader>
        <CardBody>
          <ReactSvgPanZoomLoader
            src={svg.props.src}
            width={width ?? 1000}
            height={height ?? 400}
            proxy={
              <>
                <SvgLoaderSelectElement />
              </>
            }
            render={() => (
              <UncontrolledReactSVGPanZoom
                width={width ?? 1000}
                height={height ?? 400}
                detectAutoPan={false}
                background="#fff"
              >
                <svg width={width ?? 1000} height={height ?? 400}>
                  {svg}
                </svg>
              </UncontrolledReactSVGPanZoom>
            )}
          />
        </CardBody>
      </Card>
    </>
  );
};

export default ProcessDiagram;
