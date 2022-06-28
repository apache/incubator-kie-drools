import React from 'react';
import { DataTableColumn } from '@kogito-apps/components-common';
import { PlayIcon } from '@patternfly/react-icons';
import { Tooltip, Button } from '@patternfly/react-core';
import { ProcessDefinition } from '../../../api/ProcessDefinitionListEnvelopeApi';
export const getColumn = (
  columnPath: string,
  columnLabel: string
): DataTableColumn => {
  return {
    label: columnLabel,
    path: columnPath,
    bodyCellTransformer: value => <span>{value}</span>
  };
};

export const getActionColumn = (
  startProcess: (processDefinition: ProcessDefinition) => void,
  singularProcessLabel: string
): DataTableColumn => {
  return {
    label: 'Actions',
    path: 'actions',
    bodyCellTransformer: (value, rowData: ProcessDefinition) => (
      <Tooltip content={`Start new ${singularProcessLabel.toLowerCase()}`}>
        <Button onClick={() => startProcess(rowData)} variant="link">
          <PlayIcon />
        </Button>
      </Tooltip>
    )
  };
};
