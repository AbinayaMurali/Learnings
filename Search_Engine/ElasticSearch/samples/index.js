var elasticsearch = require('elasticsearch');
var elasticClient = new elasticsearch.Client({
  	host: 'localhost:9200',
  	log: 'trace'
});
var payload = {
        "settings": {
          "number_of_shards": 1
        },
        "mappings": {
          "_doc": {
            "properties": {
              "city": {
                "type": "keyword"
              },
              "office_type": {
                "type": "keyword"
              },
              "citizens": {
                "type": "nested",
                "properties": {
                  "occupation": {
                    "type": "keyword"
                  },
                  "age": {
                    "type": "integer"
                  },
                  "pets": {
                    "type": "nested",
                    "properties": {
                      "kind": {
                        "type": "keyword"
                        },
                      "name": {
                        "type": "keyword"
                      },
                      "age": {
                        "type": "integer"
                      }
                    }
                  }
                }
              }
            }
          }
        }
      };

elasticClient.index({
    index:"city_offices",
    body: payload
  }).then(function (resp) {
    console.log(resp);
  }, function (error) {
    console.trace(error.message);
  });