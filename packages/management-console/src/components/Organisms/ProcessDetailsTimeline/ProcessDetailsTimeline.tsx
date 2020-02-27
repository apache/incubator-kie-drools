import Moment from 'react-moment';
import { Card, CardBody, CardHeader, Title } from '@patternfly/react-core';
import { ServicesIcon, UserIcon } from '@patternfly/react-icons';
import React from 'react';
import './ProcessDetailsTimeline.css';

export interface IOwnProps {
  loading: boolean;
  data: any;
}

const ProcessDetailsTimeline: React.FC<IOwnProps> = ({ loading, data }) => {
  return (
    <Card>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Timeline
        </Title>
      </CardHeader>
      <CardBody>
        <div className="timeline-container">
          {!loading ? (
            data[0].nodes.map(content => {
              return (
                <div className="timeline-item" key={content.id}>
                  <div className="timeline-item-content">
                    <Moment fromNow>{new Date(`${content.exit}`)}</Moment>

                    <p>{content.name}</p>
                    <span className="circle">
                      {content.type === 'HumanTaskNode' ? (
                        <UserIcon className="processdetailstimetine-iconstyle" />
                      ) : (
                          <ServicesIcon className="processdetailstimetine-iconstyle" />
                        )}{' '}
                    </span>
                  </div>
                </div>
              );
            })
          ) : (
              <p>loading...</p>
            )}
        </div>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsTimeline;
