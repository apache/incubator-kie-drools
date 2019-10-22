import React, { useState, useEffect } from 'react';
import {
  DataListItem,
  DataListItemRow,
  DataListToggle,
  DataListItemCells,
  DataListCell,
  DataListCheck,
  Button,
  DataListAction,
  Dropdown,
  KebabToggle,
  DropdownItem,
  DataListContent,
  DropdownPosition
} from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import gql from 'graphql-tag';
import { withApollo, useQuery, useApolloClient } from 'react-apollo';

export interface IOwnProps {
  id: number;
  instanceID: string;
  instanceState: string;
  processID: string;
  parentInstanceID: string | null;
}

const DataListItemComponent: React.FC<IOwnProps> = ({ id, instanceID, instanceState, processID, parentInstanceID }) => {
  const [expanded, setexpanded] = useState(['kie-datalist-toggle']);
  const [isOpen, setisOpen] = useState(false);
  const [isLoaded, setisLoaded] = useState(false);
  const [isChecked, setisChecked] = useState(false);
  const [childList, setchildList] = useState([]);
  const client = useApolloClient();
  const GET_CHILD_INSTANCES = gql`
  {
    ProcessInstances(filter: { parentProcessInstanceId:"${instanceID}"}) {
      id
      processId
      parentProcessInstanceId
      roles
      state
      }
        }
`;
  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };
  const onCheckBoxClick = () => {
    setisChecked(isChecked ? false : true);
  };

  const onToggle = isOpen => {
    setisOpen(isOpen);
  };

  const toggle = async id => {
    const index = expanded.indexOf(id);
    const newExpanded =
      index >= 0 ? [...expanded.slice(0, index), ...expanded.slice(index + 1, expanded.length)] : [...expanded, id];
    setexpanded(newExpanded);
    if (isLoaded) {
    } else {
      const data = await client.query({
        query: GET_CHILD_INSTANCES
      });
      setchildList(data['data']);
      setisLoaded(true);
    }
  };
  return (
    <React.Fragment>
      <DataListItem aria-labelledby="kie-datalist-item" isExpanded={expanded.includes('kie-datalist-toggle')}>
        <DataListItemRow>
          <DataListToggle
            onClick={() => toggle('kie-datalist-toggle')}
            isExpanded={expanded.includes('kie-datalist-toggle')}
            id="kie-datalist-toggle"
            aria-controls="kie-datalist-expand"
          />
          <DataListCheck
            aria-labelledby="width-kie-datalist-item"
            name="width-kie-datalist-item"
            checked={isChecked}
            onChange={() => {
              onCheckBoxClick();
            }}
          />
          <DataListItemCells
            dataListCells={[
              <DataListCell key="primary content">
                Instance {id} ({processID})
              </DataListCell>,
              <DataListCell key="secondary content">Chart to be added</DataListCell>,
              // this should be removed in favor of the action below... but I can't get the link to work on the action
              <DataListCell key="secondary content 2">
                <Link to={'/instanceDetail/' + id}>
                  <Button variant="secondary">Old Details</Button>
                </Link>
              </DataListCell>,
              <DataListCell key="secondary content 3">{instanceState}</DataListCell>
            ]}
          />

          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Link to={'/Details/' + instanceID}>
              <Button variant="secondary">Details</Button>
            </Link>
          </DataListAction>
          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Dropdown
              isPlain
              position={DropdownPosition.right}
              isOpen={isOpen}
              onSelect={onSelect}
              toggle={<KebabToggle onToggle={onToggle} />}
              dropdownItems={[
                <DropdownItem key={1}>Link</DropdownItem>,
                <DropdownItem key={2} component="button">
                  Action
                </DropdownItem>
              ]}
            />
          </DataListAction>
        </DataListItemRow>
        <DataListContent
          aria-label="Primary Content Details"
          id="kie-datalist-expand1"
          isHidden={expanded.includes('kie-datalist-toggle')}
        >
          {isLoaded &&
            childList['ProcessInstances'] != undefined &&
            childList['ProcessInstances'].map((child, index) => {
              console.log('i am a child', child);
              return (
                <DataListItemComponent
                  id={index}
                  key={index}
                  instanceState={child.state}
                  instanceID={child.id}
                  processID={child.processId}
                  parentInstanceID={child.parentProcessInstanceId}
                />
              );
            })}
        </DataListContent>
      </DataListItem>
    </React.Fragment>
  );
};

export default DataListItemComponent;
