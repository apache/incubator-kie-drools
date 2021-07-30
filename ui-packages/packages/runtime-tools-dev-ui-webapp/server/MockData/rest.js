module.exports = restData = {
  management: {
    process: [
      {
        processId: 'flightBooking',
        instances: [
          {
            processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
            skip: 'Internal server error',
            retrigger: 'success',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf864',
            skip: 'success',
            retrigger: 'success',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf865',
            skip: 'failed',
            retrigger: 'failed',
            aborted: 'Internal server error'
          }
        ]
      },
      {
        processId: 'travels',
        instances: [
          {
            processInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
            skip: 'success',
            retrigger: 'Authentication failed',
            aborted: 'Authorization failed'
          },
          {
            processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf867',
            skip: 'failed',
            retrigger: 'failed',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf868',
            skip: 'success',
            retrigger: 'success',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
            skip: 'success',
            retrigger: 'Authentication failed',
            aborted: 'success'
          },
          {
            processInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0bccddee',
            skip: 'failed',
            retrigger: 'success',
            aborted: 'success'
          },
          {
            processInstanceId: '538f9feb-5a14-4096-b791-2055b38da7c6',
            skip: 'success',
            retrigger: 'Authentication failed',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: 'tEE12-fo54-l665-mp112-akou112345566',
            skip: 'Authentication failed',
            retrigger: 'success',
            aborted: 'Internal server error'
          },
          {
            processInstanceId: 'RZ11-tu77-hj321-bnfhe1-xdr2134',
            skip: 'success',
            retrigger: 'Authentication failed',
            aborted: 'Internal server error'
          },
        ]
      }
    ]
  }
};
