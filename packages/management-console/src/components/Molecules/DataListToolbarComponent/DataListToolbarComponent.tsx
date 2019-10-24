import React, { useState } from 'react';
import {
  Dropdown,
  DropdownToggle,
  DropdownToggleCheckbox,
  Button,
  ChipGroup,
  ChipGroupToolbarItem,
  Chip,
  Toolbar,
  ToolbarItem,
  ToolbarGroup,
  DropdownItem,
  Checkbox
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import _ from 'lodash';

export interface IOwnProps {
  isComplete: boolean;
  isActive: boolean;
  isAborted: boolean;
  checkedArray: any;
  handleChange: any;
  filterClick: any;
  removeCheck: any;
}

const DataListToolbarComponent: React.FC<IOwnProps> = ({
  isActive,
  isComplete,
  isAborted,
  handleChange,
  checkedArray,
  filterClick,
  removeCheck
}) => {
  const [chipGroups, setchipGroups] = useState([]);
  const [isOpen, setisOpen] = useState(false);

  const dropDownList = [
    <DropdownItem key="link1" component="checkbox">
      <Checkbox
        label="  ACTIVE"
        aria-label="controlled checkbox example"
        id="check-1"
        name="isActiveChecked"
        onChange={handleChange}
        isChecked={isActive}
      />
    </DropdownItem>,
    <DropdownItem key="link2">
      <Checkbox
        label="COMPLETED"
        aria-label="controlled checkbox example"
        id="check-2"
        name="isCompletedChecked"
        onChange={handleChange}
        isChecked={isComplete}
      />
    </DropdownItem>,
    <DropdownItem key="link3">
      <Checkbox label="ERROR" aria-label="controlled checkbox example" id="check-3" name="isErrorChecked" />
    </DropdownItem>,
    <DropdownItem key="link4">
      <Checkbox
        label="ABORTED"
        aria-label="controlled checkbox example"
        id="check-4"
        name="isAbortChecked"
        onChange={handleChange}
        isChecked={isAborted}
      />
    </DropdownItem>,
    <DropdownItem key="link5">
      <Checkbox label="SUSPENDED" aria-label="controlled checkbox example" id="check-5" name="check5" />
    </DropdownItem>
  ];

  const onToggle = isOpen => {
    setisOpen(isOpen);
  };
  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };
  const onFilterClick = () => {
    filterClick();
    let chipArray = chipGroups.slice();
    chipArray = [];
    chipArray.push({
      category: 'Status',
      chips: checkedArray
    });
    setchipGroups(chipArray);
  };
  const deleteItem = id => {
    const copyOfChipGroups = chipGroups.slice();
    for (let i = 0; copyOfChipGroups.length > i; i++) {
      const index = copyOfChipGroups[i].chips.indexOf(id);
      if (index !== -1) {
        copyOfChipGroups[i].chips.splice(index, 1);
        // check if this is the last item in the group category
        if (copyOfChipGroups[i].chips.length === 0) {
          copyOfChipGroups.splice(i, 1);
          setchipGroups(copyOfChipGroups);
        } else {
          setchipGroups(copyOfChipGroups);
        }
      }
      filterClick();
      removeCheck(id);
    }
  };

  return (
    <React.Fragment>
      <Toolbar className="pf-u-justify-content-space-between pf-u-mx-xl pf-u-my-md">
        <ToolbarGroup>
          <ToolbarItem>
            <Dropdown
              toggle={
                <DropdownToggle
                  splitButtonItems={[
                    <DropdownToggleCheckbox id="example-checkbox-1" key="split-checkbox" aria-label="Select all" />
                  ]}
                />
              }
            />
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarGroup>
          <ToolbarItem>
            <Dropdown
              toggle={
                <DropdownToggle onToggle={onToggle}>
                  <FilterIcon /> &nbsp;&nbsp; Dropdown
                </DropdownToggle>
              }
              isOpen={isOpen}
              dropdownItems={dropDownList}
            />
          </ToolbarItem>
          <ToolbarItem>
            <Button variant="primary" onClick={onFilterClick}>
              Apply Filter
            </Button>
          </ToolbarItem>
        </ToolbarGroup>
        <ToolbarGroup>
          <ToolbarItem>
            <ChipGroup withToolbar>
              {chipGroups.map(currentGroup => (
                <ChipGroupToolbarItem key={currentGroup.category} categoryName={currentGroup.category}>
                  {currentGroup.chips.map(chip => (
                    <Chip key={chip} onClick={() => deleteItem(chip)}>
                      {chip}
                    </Chip>
                  ))}
                </ChipGroupToolbarItem>
              ))}
            </ChipGroup>
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
};

export default DataListToolbarComponent;
