const express = require('express')
const app = express();
var cors = require('cors')

const mockData = {
    management: {
        process: [{
            processId: 'flightBooking', 
            instances: [{
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf863',
                error: 'Something went wrong',
                skip: 'Internal server error',
                retrigger: 'success'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf864',
                error: 'some thing went wrong',
                skip: 'success',
                retrigger: 'success'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf865',
                error: 'some thing went wrong',
                skip: 'failed',
                retrigger: 'failed'
            }]
            
        },
        {
            processId: 'travels', 
            instances: [{
                processInstanceId: 'e4448857-fa0c-403b-ad69-f0a353458b9d',
                error: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim idest laborum.',
                skip: 'success',
                retrigger: 'Authentication failed'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf867',
                error: 'some thing went wrong',
                skip: 'failed',
                retrigger: 'failed'
            },
            {
                processInstanceId: 'a23e6c20-02c2-4c2b-8c5c-e988a0adf868',
                error: 'some thing went wrong',
                skip: 'success',
                retrigger: 'success'
            }]
            
        }]
    }
} 

const controller = {
    showError : (req, res) => {
        console.log('called', req.params.processId, req.params.processInstanceId)
        const {process} = mockData.management;
        const processId = process.filter((data)     => {
            return data.processId === req.params.processId
        })
        const error = processId[0].instances.filter((err) => {
            return err.processInstanceId === req.params.processInstanceId
        })
        res.send(error[0].error)
    },
    callRetrigger: (req,res) => {
        const {process} = mockData.management;
        const processId = process.filter((data)     => {
            return data.processId === req.params.processId
        })
        const error = processId[0].instances.filter((err) => {
            return err.processInstanceId === req.params.processInstanceId
        })
        // res.send(error[0].retrigger)
        switch(error[0].retrigger) {
            case 'success' :
                res.send(error[0].retrigger)
                break;
            case 'Authentication failed' :
                res.status(401).send(error[0].retrigger);
                break;
            case 'Authorization failed' :
                res.status(403).send(error[0].retrigger)
                break;
            case 'Internal server error' :
                res.status(500).send(error[0].retrigger)
                break;
        }
    },
    callSkip: (req,res) => {
        const {process} = mockData.management;
        const processId = process.filter((data)     => {
            return data.processId === req.params.processId
        })
        const error = processId[0].instances.filter((err) => {
            return err.processInstanceId === req.params.processInstanceId
        })
        switch(error[0].skip) {
            case 'success' :
                res.send(error[0].skip);
                break;
            case 'Authentication failed' :
                res.status(401).send(error[0].skip);
                break;
            case 'Authorization failed' :
                res.status(403).send(error[0].skip)
                break;
            case 'Internal server error' :
                res.status(500).send(error[0].skip)
                break;
        }
    }
}

app.use(cors())
// http://localhost:8090/management/process/{processId}/instances/{processInstanceId}/error
app.get('/management/process/:processId/instances/:processInstanceId/error', controller.showError)
app.get('/management/process/:processId/instances/:processInstanceId/skip', controller.callSkip)
app.get('/management/process/:processId/instances/:processInstanceId/retrigger', controller.callRetrigger)

app.listen('8080', () =>{
    console.log('connected to port rest server 8080')
})
