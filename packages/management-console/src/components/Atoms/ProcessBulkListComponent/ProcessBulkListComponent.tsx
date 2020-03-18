import React from 'react';
import {
  TextContent,
  TextVariants,
  Text,
  Divider,
  TextList,
  TextListItem
} from '@patternfly/react-core';

interface IOwnProps {
  abortedMessageObj: any;
  completedMessageObj: any;
  isSingleAbort: any;
  checkedArray: any;
  isAbortModalOpen: boolean;
}
const ProcessBulkListComponent: React.FC<IOwnProps> = ({
  abortedMessageObj,
  completedMessageObj,
  isSingleAbort,
  checkedArray,
  isAbortModalOpen
}) => {
  const stringModifier = (strId: string) => {
    return strId.substring(0, 5);
  };
  return (
    <>
      {' '}
      {Object.keys(abortedMessageObj).length !== 0 &&
        Object.keys(completedMessageObj).length !== 0 &&
        !isSingleAbort && (
          <>
            <TextContent>
              <Text component={TextVariants.h2}>
                {' '}
                The following processes were aborted:
              </Text>

              <TextList>
                {Object.entries(abortedMessageObj).map((process: any) => {
                  return (
                    <TextListItem key={process[0]}>
                      {process[1].processName}{' '}
                      <Text
                        component={TextVariants.small}
                        className="pf-u-display-inline"
                      >
                        {stringModifier(process[0])}
                      </Text>
                    </TextListItem>
                  );
                })}
              </TextList>
            </TextContent>
            {!checkedArray.includes('ABORTED') &&
              isAbortModalOpen &&
              abortedMessageObj !== undefined &&
                Object.keys(abortedMessageObj).length !== 0 && (
                <TextContent className="pf-u-mt-sm">
                  <Text>
                    Note: The process status has been updated. The list may
                    appear inconsistent until you refresh any applied filters.
                  </Text>
                </TextContent>
              )}
            <Divider component="div" className="pf-u-my-xl" />
            <TextContent>
              <Text component={TextVariants.h2}>
                The following processes were skipped because they were either
                completed or aborted:
              </Text>

              <TextList>
                {Object.entries(completedMessageObj).map((process: any) => {
                  return (
                    <TextListItem key={process[0]}>
                      {process[1].processName}{' '}
                      <Text
                        component={TextVariants.small}
                        className="pf-u-display-inline"
                      >
                        {stringModifier(process[0])}
                      </Text>
                    </TextListItem>
                  );
                })}
              </TextList>
            </TextContent>
          </>
        )}
      {Object.keys(abortedMessageObj).length === 0 &&
        Object.keys(completedMessageObj).length !== 0 &&
        !isSingleAbort && (
          <>
            <TextContent>
              <Text component={TextVariants.h2}>
                {' '}
                No processes were aborted
              </Text>
            </TextContent>
            <Divider component="div" className="pf-u-my-xl" />
            <TextContent>
              <Text component={TextVariants.h2}>
                The following processes were skipped because they were either
                completed or aborted:
              </Text>

              <TextList>
                {Object.entries(completedMessageObj).map((process: any) => {
                  return (
                    <TextListItem key={process[0]}>
                      {process[1].processName}{' '}
                      <Text
                        component={TextVariants.small}
                        className="pf-u-display-inline"
                      >
                        {stringModifier(process[0])}
                      </Text>
                    </TextListItem>
                  );
                })}
              </TextList>
            </TextContent>
          </>
        )}
      {Object.keys(abortedMessageObj).length !== 0 &&
        Object.keys(completedMessageObj).length === 0 &&
        !isSingleAbort && (
          <>
            <TextContent>
              <Text component={TextVariants.h2}>
                {' '}
                The following processes were aborted:
              </Text>
              <TextList>
                {Object.entries(abortedMessageObj).map((process: any) => {
                  return (
                    <TextListItem key={process[0]}>
                      {process[1].processName}{' '}
                      <Text
                        component={TextVariants.small}
                        className="pf-u-display-inline"
                      >
                        {stringModifier(process[0])}
                      </Text>
                    </TextListItem>
                  );
                })}
              </TextList>
            </TextContent>
            {!checkedArray.includes('ABORTED') &&
              isAbortModalOpen &&
              abortedMessageObj !== undefined &&
                Object.keys(abortedMessageObj).length !== 0 && (
                <TextContent className="pf-u-mt-sm">
                  <Text>
                    Note: The process status has been updated. The list may
                    appear inconsistent until you refresh any applied filters.
                  </Text>
                </TextContent>
              )}
            <Divider component="div" className="pf-u-my-xl" />
            <TextContent>
              <Text component={TextVariants.h2}>No processes were skipped</Text>
            </TextContent>
          </>
        )}
      {Object.keys(abortedMessageObj).length !== 0 &&
        Object.keys(completedMessageObj).length === 0 &&
        isSingleAbort && (
          <>
            <TextContent>
              <Text component={TextVariants.h2}>
                {' '}
                The following process was aborted:
              </Text>
              <TextList>
                {Object.entries(abortedMessageObj).map((process: any) => {
                  return (
                    <TextListItem key={process[0]}>
                      {process[1].processName}{' '}
                      <Text
                        component={TextVariants.small}
                        className="pf-u-display-inline"
                      >
                        {stringModifier(process[0])}
                      </Text>
                    </TextListItem>
                  );
                })}
              </TextList>
            </TextContent>
            {!checkedArray.includes('ABORTED') &&
              isAbortModalOpen &&
              abortedMessageObj !== undefined &&
                Object.keys(abortedMessageObj).length !== 0 && (
                <TextContent className="pf-u-mt-sm">
                  <Text>
                    Note: The process status has been updated. The list may
                    appear inconsistent until you refresh any applied filters.
                  </Text>
                </TextContent>
              )}
          </>
        )}
    </>
  );
};

export default ProcessBulkListComponent;
