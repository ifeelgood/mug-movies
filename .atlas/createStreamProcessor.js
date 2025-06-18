source = {
  "$source": {
    "connectionName": "MUGCluster0",
    "db": "sample_mflix",
    "coll": "movies"
  }
}

emit = {
    "$emit": {
        "connectionName": "mug-confluent-cloud",
        "topic": "mug-movies-cdc",
        "config": {
            "key": {"$toString": "$documentKey._id"},
            "keyFormat": "string"
        }
    }
  }

sp.createStreamProcessor("mugprocessor", [source, emit])
sp.mugprocessor.start();