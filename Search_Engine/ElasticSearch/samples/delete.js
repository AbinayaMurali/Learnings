var elasticsearch = require('elasticsearch');
var elasticClient = new elasticsearch.Client({
  	host: 'localhost:9200',
  	log: 'trace'
});

elasticClient.indices.delete({
    index:"city_offices"
  }).then(function (resp) {
    console.log(resp);
  }, function (error) {
    console.trace(error.message);
  });