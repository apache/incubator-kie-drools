import { TimeAgo } from '@n1ru4l/react-time-ago';
import { Card, CardBody, CardFooter, CardHeader } from '@patternfly/react-core';
import { ServicesIcon, UserIcon } from '@patternfly/react-icons'
import React from 'react';
import './ProcessDetailsTimeline.css';

const ProcessDetailsTimeline = ({ loading, data }) => {

  return (
    <Card>
      <CardHeader>Timeline</CardHeader>
      <CardBody>
        <div className="timeline-container">
          {!loading ? (
            data[0].nodes.map((content, index) => {
              return (
                <div className="timeline-item" key={index}>
                  <div className="timeline-item-content">
                    <TimeAgo date={new Date(`${content.exit}`)} render={({ error, value }) => <span>{value}</span>} />
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
