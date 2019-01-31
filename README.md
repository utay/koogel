# Koogel

Koogel is a mini Google like distributed search/indexing engine. It uses the CQRS pattern alongside with custom injection and event sourcing frameworks.

## Build

Building and running is easy with `gradle`:

```
$ gradle wrapper
$ ./gradlew build
$ java -jar build/libs/koogel-1.0-SNAPSHOT-all.jar
```

## Usage

### Backend

There are several available modules:
- bus: custom HTTP event bus with channels and messages
- store: store the events
- crawler\_manager: logic and load balancing between the crawlers
- indexer\_manager: logic and load balancing between the indexers
- crawler: crawl web pages
- indexer: index the content
- retro\_index: store the documents and serves the search

You need to start **one** `bus`, `store`, `crawler_manager` and `indexer_manager`. Then, you can start as many `crawler`, `indexer` and `retro_index` as you want.

### Frontend

Start the vue webpack server:

`yarn dev`

Then visit `localhost:8080` to access the user interface.
