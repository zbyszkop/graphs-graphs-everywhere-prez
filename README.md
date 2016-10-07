# graphs-graphs-everywhere-prez

Handbook for Graphs, graphs everywhere presentation. 

## Useful links
[Solr download](http://archive.apache.org/dist/lucene/solr/6.2.0/)
[How to allow CORS in SOLR](http://marianoguerra.org/posts/enable-cors-in-apache-solr.html)
##Useful commands (in Solr directory)
####Starting Solr for the first time, usting cloud example (recommended)
```
bin/solr -e cloud
```
####Starting Solr with the created example core
```
bin/solr start -cloud -p 8983 -s "example/cloud/node1/solr"
```
####Stopping Solr
```
bin/solr stop
```
####Add collection to Solr
```
bin/solr create -c <collection name>
```

####Remove collection to Solr
```
bin/solr delete -c <collection name>
```
##Schemas for provided collections
all schemas are provided via REST Api, POST resource `/<collection>/schema`, Content-type: application/json
####emails
```javascript
{
  "add-field":[{
     "name":"from",
     "type":"string",
     "stored":true },
     {
     "name":"to",
     "type":"string",
     "stored":true },
     {
     "name":"cc",
     "type":"string",
     "stored":true },
     {
     "name":"bcc",
     "type":"string",
     "stored":true },
     {
     "name":"subject",
     "type":"text_en",
     "stored":true },
     {
     "name":"message",
     "type":"text_en",
     "stored":true },
     {
     "name":"date",
     "type":"date",
     "stored":true }
     ]
}
```
####social_users
```javascript
{
  "add-field":[
    {
      "name": "full_name",
      "type":"string"
    },
    {
      "name": "email",
      "type":"string"
    },
    {
      "name": "friends",
      "type":"strings"
    }
  ]
}
```
####social_users_flat
```javascript
{
  "add-field":[
    {
      "name": "full_name",
      "type":"string"
    },
    {
      "name": "email",
      "type":"string"
    },
    {
      "name": "friend",
      "type":"string"
    }

  ]
}
```
####song_likes
```javascript
{
  "add-field":[
    {
      "name": "email",
      "type":"string"
    },
    {
      "name": "title",
      "type":"string"
    },
    {
      "name": "genre",
      "type":"string"
    },
    {
      "name": "artist",
      "type":"string"
    },
    {
      "name": "title_artist",
      "type":"string"
    }

  ]
}
```

For all of Postman fans:
[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/eaffa82012ba323c9f49)

## Solr collection feeders
Note that every collection should be created and schema should be provided for each(this is important - without schema documents will still be there, but some operations won't be possible).

#### Emails collection Feeder
Writes  2500+ emails from SpamAssassins's easy_ham collection. 

[EmailFeeder](/solr-example/src/main/java/jug/solrexample/feed/EmailFeeder.java)
#### Social_users collection Feeder
From the list of emails (all fakes, if anybody's interested) generates random unidirectional friends network. Since it's randomized, results may vary. 

[SocialUsersFeeder](/solr-example/src/main/java/jug/solrexample/feed/SocialUsersFeeder.java)
###Social_users_flat collection Feeder
Similar to the previous one, but provides denormalized documents, i.e. instead of friends list per document, every relation has it's own document. This was created because of the limitations of `gatherNodes` function.

[SocialUsersFlatFeeder]((/solr-example/src/main/java/jug/solrexample/feed/SocialUsersFlatFeeder.java))
####Song_likes
Collection of song likes by users gathered from the same source as previous two collections

[SongLikesFeeder]((/solr-example/src/main/java/jug/solrexample/feed/SongLikesFeeder.java))


