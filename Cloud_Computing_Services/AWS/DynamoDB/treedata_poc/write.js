var AWS = require("aws-sdk");
let awsConfig = {
    "region": "us-east-1",
    "endpoint": "http://dynamodb.us-east-1.amazonaws.com",
    "accessKeyId": "", "secretAccessKey": ""
};
AWS.config.update(awsConfig);

let docClient = new AWS.DynamoDB.DocumentClient();

let save = function () {
    var params = {
        TableName: "treedata",
        Item:  {
            "empid":"201707-4444",
            "employee":{
                    "star_administration_active":"Yes",
                    "categorization_type":"01-PROJECTS",
                    "categorization_category":"Initiative: GSS-Systems Deployment",
                    "categorization_sub_category":"Collaberation Tools",
                    "categorization_topic":"Slack for Support",
                    "classification_watchlist":"No",
                    "state_record_progress":"06-CLOSED"
            }
         }
    };
    docClient.put(params, function (err, data) {

        if (err) {
            console.log("users::save::error - " + JSON.stringify(err, null, 2));                      
        } else {
            console.log("users::save::success" );                      
        }
    });
}

save();