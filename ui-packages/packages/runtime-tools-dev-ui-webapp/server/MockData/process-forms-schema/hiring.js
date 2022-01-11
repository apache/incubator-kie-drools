module.exports = {
    type: 'object',
    properties: {
        Candidate: {
            type: "object",
            properties: {
                email: {
                    type: "string"
                },
                name: {
                    type: "string"
                },
                salary: {
                    type: "integer"
                },
                skills: {
                    type: "string"
                }
            }
        },
            hr_approval: {
                type: "boolean"
            },
            it_approval: {
                type: "boolean"
            }
        }

    }
