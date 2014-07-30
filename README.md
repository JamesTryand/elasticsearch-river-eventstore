Eventstore River Plugin for ElasticSearch
==============================

The Eventstore River plugin allows index events from eventstore into elasticsearch.

In order to install the plugin, simply run: `bin/plugin -install elasticsearch/elasticsearch-river-eventstore/0.1.0`.

To set up Eventstore River plugin, run
```sh
curl -XPUT 'localhost:9200/_river/eventstore/_meta' -d '
{
    "type": "eventstore",
    "eventstore": {
        "host": "172.21.200.240",
        "port": 1113,
        "username": "admin",
        "password": "changeit",
        "stream": "EventCapture"
    },
    "index": {
        "bulk_size_bytes": 10485760,
        "bulk_timeout": "10ms"
    }
}'
```

The river will listen to the stream defined in the configuration and automatically index the event into elasticsearch.
