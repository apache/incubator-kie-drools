import React from 'react';
import { UncontrolledReactSVGPanZoom } from 'react-svg-pan-zoom';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/common';
import {
  ReactSvgPanZoomLoader,
  SvgLoaderSelectElement
} from 'react-svg-pan-zoom-loader';
import { Title, Card, CardHeader, CardBody } from '@patternfly/react-core';

interface svgType {
  src: string;
}
interface svgProp {
  props: svgType;
}
interface IOwnProps {
  svg: svgProp;
}

const ProcessDetailsProcessDiagram: React.FC<IOwnProps & OUIAProps> = ({
  svg,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <>
      <Card {...componentOuiaProps(ouiaId, 'process-diagram', ouiaSafe)}>
        <CardHeader>
          <Title headingLevel="h3" size="xl">
            Process Diagram
          </Title>
        </CardHeader>
        <CardBody>
          <ReactSvgPanZoomLoader
            src={svg.props.src}
            width={1000}
            height={400}
            proxy={
              <>
                <SvgLoaderSelectElement />
              </>
            }
            render={() => (
              <UncontrolledReactSVGPanZoom
                width={1000}
                height={400}
                detectAutoPan={false}
                background="#fff"
              >
                <svg width={1000} height={400}>
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

export default ProcessDetailsProcessDiagram;
