var AWS = require("aws-sdk");
let awsConfig = {
    "region": "us-east-1",
    "endpoint": "http://dynamodb.us-east-1.amazonaws.com",
    "accessKeyId": "", "secretAccessKey": ""
};
AWS.config.update(awsConfig);

let docClient = new AWS.DynamoDB.DocumentClient();

let search = function () {
    var params = {
        TableName: "treedata",
        FilterExpression: "#keyone.#keytwo = :keyone",
        ExpressionAttributeNames: {
            "#keyone": "employee",
            "#keytwo": "star_administration_active"
        },
        ExpressionAttributeValues: {
            ":keyone": "Yes"
        }
    };
    docClient.scan(params, async function (err, data) {
        if (err) {
            console.log("users::search::error - " + JSON.stringify(err, null, 2));                      
        } else {
            console.log(data);                      
        }
    });
}

search();