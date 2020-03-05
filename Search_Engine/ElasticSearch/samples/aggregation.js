var elasticsearch = require('elasticsearch');
var elasticClient = new elasticsearch.Client({
  	host: 'localhost:9200',
  	log: 'trace'
});

elasticClient.search({
    index:"city_offices",
    body: {
/*            "aggs": {
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
            },
            "aggs": {
              "pet_kinds": {
                "terms": {
                  "field": "citizens.pets.kind.keyword"
                }
              }
            }*/

              "aggs": {
                "citizens": {
                  "nested": {
                    "path": "citizens.pets"
                  },
                  "aggs": {
                    "kinds": {
                      "terms": {
                        "field": "citizens.pets.kind.keyword"
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