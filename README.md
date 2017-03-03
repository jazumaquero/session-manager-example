## Session manager example.

This is a simple example of how to implement some naive session manager with scala.


## Architecture description.

This session manager implementation have two main pieces:

* Some session repository, that works like a cache, so that, session data can be handle just by using some *session id* key.
* Some http controller that allows using some REST API to the cache.


### SessionRepository implementation.

SessionRepository is implemented with DAO pattern, so that, many implementations can be done.

In order to have some fast implementation, [ScalaCache](https://github.com/cb372/scalacache) was used to implement the
session repository, due to it allows several cache implementation (including 3rd party distributed caches):

* Google Guava
* Memcached
* Ehcache
* Redis
* Caffeine

For the sake of extensibility, any **session data** is just treat like an **array of bytes**, so that, any kind of data
can be store and, furthermore, using **array of bytes** instead of objects allows using multiple cache implementations,
like *Redis* or *Memcache*.

In order to allow some security valves, number of cache entries can be limited and expiration of unused data can be limited
to some max duration.


### API implementation.

In order to follow some standard way of handling http session, the **session id** is store into some cookie. To store
or update some session data it's required to previously have some cookie included for the request.

For the sake of performance, [AKKA-HTTP](http://doc.akka.io/docs/akka-http/current/scala.html) has been used to implement
the REST API.

So that, API works as following:

* *Create some new session id*: just request some *POST* to the configured resource. When cookie is missing new cookie is
included, but when cookie is already present, nothing is done. Always returns **201 Created**.
* *Read session data*: just request some *GET* to the configured resource. When cookie is present, session data is returned
with **200 OK**, but when cookie is missing some **405 Method not allowed** is returned.
* *Update session data*:just request some *PUT* to the configured resource including data into the payload. When cookie is
present, some **200 OK** is returned, but when cookie is missing some **405 Method not allowed** is returned.
* *Delete session*: to remove session just request some *DELETE* to the configured resource. When cookie is
present, some **200 OK** is returned and cookie is required to be deleted on browser, but when cookie is missing some
**405 Method not allowed** is returned.

Resource is going to be requested can be configured, so that, uris will look like following:

    http://<domain.name>:<port>/<resource name>/


## Configuration

Following properties can be used to configure the session manager:

* Server configuration:
    * *server.host*: IP address to bind to.
    * *server.port*: port where server is going to listen to.
    * *server.cookie*: name of the cookie used to store the session id.
    * *server.root*: name of the resource is going to be exposed

* Cache configuration
    * *cache.type*: allows to inject proper implementation of the session repository. Currently only *memory* is supported.
    * *cache.maxSize*: when is present, cache can limit the number of entries to this number.
    * *cache.ttl.duration* and *cache.ttl.unit*: when they are present, they allow configuring a max duration on non used
session data in the cache. Units must accomplish [scala Duration](http://www.scala-lang.org/api/2.9.3/scala/concurrent/duration/Duration.html).

Currently, typesafe configuration format is used. For instance:

    server {
      host = "0.0.0.0"
      port = 9000
      cookie = "foo.bar.nav"
      root = "session"
    }

    cache {
      type="memory"
      maxSize = 1000
      ttl {
        duration = 600
        unit="seconds"
      }
    }


## Leftovers

Following thing are pending (sort by importance):

* Session data must be validated before update operations (use DAO pattern and some factory to allow multiple implementations).
Initial validation must include size limited session data.
* Review security taking into account CRSF.
* Add more SessionRepository for Redis or Memcache.
* It may be interesting to develop some distributed cache based on persistent/non persistent *AKKA* actors.
