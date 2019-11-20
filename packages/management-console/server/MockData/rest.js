module.exports = restData = {
    management: {
        process: [{
            processId: 'flightBooking', 
            instances: [{
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
                skip: 'Internal server error',
                retrigger: 'success'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf864',
                skip: 'success',
                retrigger: 'success'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf865',
                skip: 'failed',
                retrigger: 'failed'
            }]
            
        },
        {
            processId: 'travels', 
            instances: [{
                processInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
                skip: 'success',
                retrigger: 'Authentication failed'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf867',
                skip: 'failed',
                retrigger: 'failed'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf868',
                skip: 'success',
                retrigger: 'success'
            }]
        }]
    }
} 
