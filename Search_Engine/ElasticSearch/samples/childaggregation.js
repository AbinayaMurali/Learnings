var elasticsearch = require('elasticsearch');
var elasticClient = new elasticsearch.Client({
  	host: 'localhost:9200',
  	log: 'trace'
});

elasticClient.search({
    index:"city_offices",
    body: {
            "aggs": {
              "cities": {
                "terms": {
                  "field": "city.keyword"
                },
                "aggs": {
                  "office_types": {
                    "terms": {
                      "field": "office_type.keyword"
                    }
                  }
                }
              }
            }
  }
  }).then(function (resp) {
    console.log(resp.hits);
  }, function (error) {
    console.trace(error.message);
  });