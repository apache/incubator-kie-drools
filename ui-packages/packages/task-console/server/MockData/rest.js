module.exports = restData = {
  process: [
    {
      processId: 'travels',
      instances: [
        {
          processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
          tasks: [
            {
              referenceName: 'VisaApplication',
              taskId: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
              complete: 'success',
              message: 'success'
            }
          ]
        },
        {
          processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
          tasks: [
            {
              referenceName: 'ConfirmTravel',
              taskId: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
              complete: 'failed',
              message: 'failed'
            }
          ]
        }
      ]
    }
  ]
};
