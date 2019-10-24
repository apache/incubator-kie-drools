import React from 'react';
import { Card, CardHeader, CardBody, CardFooter, Button } from '@patternfly/react-core';
import { TimeAgo } from '@n1ru4l/react-time-ago';
import { UserIcon } from '@patternfly/react-icons'
import { ServicesIcon } from '@patternfly/react-icons'


const ProcessDetailsTimeline = ({ loading, data }) => {
  const TimelineStyle = {
    marginLeft: '2em',
    height: '40em',
    position: 'relative',
    bottom: '3em',
    overflowY: 'scroll',
    marginTop: '3em'
  };
  
  const IconStyle = {
    position: 'relative',
    top: '3px',
    left: '3px'
  }
  return (
    <Card style={TimelineStyle}>
      <CardHeader>Timeline</CardHeader>
      <CardBody>
        <div className="timeline-container">
          {!loading ? (
            data[0].nodes.map((content, index) => {
              return (
                <div className="timeline-item" key={index}>
                  <div className="timeline-item-content">
                      <TimeAgo date={new Date(`${content.exit}`)} render={({ error, value }) => <span>{value}</span>}/>
                    <p>{content.name}</p>
                    <span className="circle">{content.type === 'HumanTaskNode' ? <UserIcon style={IconStyle}/> : <ServicesIcon style={IconStyle}/> }  </span>
                  </div>
                </div>
              );
            })
          ) : (
            <p>loading...</p>
          )}
        </div>
      </CardBody>
      <CardFooter>
        <Button variant="primary" style={{ float: 'right' }}>
          Primary
        </Button>
      </CardFooter>
    </Card>
  );
};

export default ProcessDetailsTimeline;
