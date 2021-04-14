


- create container
  POST to /w3c/
  use "Slug: name" header to set a custom container name
  returns container info as json-ld

  
add annotation to container


annotation moet hebben:
- creator


Belangrijke queries zijn:
- zoeken op creator,
  /w3c/search/creator?levels=annotation
  
- creatie datum (gebruikers denken dan o.a. in termen als 'ik wil een paar annotaties aanpassen die *ik* gemaakt heb in de laatste twee uur', of '... gisteren'), en
  /w3c/services/search/temporal?levels=annotation&types=created&since=...
  
- annotation bodies
  er is w3c/services/search/body   

  - (het type body zoals een 'comment', 'tag' of 'correction', of
    w3c definieert wel een type bij een body, maar niet zo.
    de gebruikte termen zouden als purpose of motivation ingevuld kunnen worden. er kan echter alleen gezocht worden op annotation.id of annotation.source

  - de waarde die ik heb ingevoerd).

Als je de search van elucidate zinvol wilt gebruiken, ben je beperkt in welke delen van het WebAnnotation model je gebruikt.


- zoekveld toevoegen:
 - database wijzigingen: liquibase-changelog.xml bijwerken

De annotation json is opgeslagen in db: annotation.json, maar genormaliseerd naar expanded OA!

OA:
```
{
  "@context": "https://raw.githubusercontent.com/dlcs/elucidate-server/master/elucidate-server/src/main/resources/contexts/oa.jsonld",
  "@id": "http://localhost:8080/annotation/oa/8cc97fb5-b938-4561-82fe-72e2c97214b4/b3d7d8b2-be8a-4df3-8e39-5a5cdbac4b95",
  "@type": "oa:Annotation",
  "hasBody": {
    "@id": "http://example.org/annotation-body-30",
    "@type": "oa:TextualBody",
    "value": "I like this page! (30)"
  },
  "hasTarget": "http://www.example.com/index.html"
}
```

W3C
```
{
  "@context": "http://www.w3.org/ns/anno.jsonld",
  "id": "http://localhost:8080/annotation/w3c/8cc97fb5-b938-4561-82fe-72e2c97214b4/b3d7d8b2-be8a-4df3-8e39-5a5cdbac4b95",
  "type": "Annotation",
  "body": {
    "id": "http://example.org/annotation-body-30",
    "type": "TextualBody",
    "value": "I like this page! (30)"
  },
  "target": "http://www.example.com/index.html"
}
```

annotation.json
```
{
  "@type": [
    "http://www.w3.org/ns/oa#Annotation"
  ],
  "http://www.w3.org/ns/oa#hasBody": [
    {
      "@id": "http://example.org/annotation-body-30",
      "@type": [
        "http://www.w3.org/ns/oa#TextualBody"
      ],
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#value": [
        {
          "@value": "I like this page! (30)"
        }
      ]
    }
  ],
  "http://www.w3.org/ns/oa#hasTarget": [
    {
      "@id": "http://www.example.com/index.html"
    }
  ]
}
```

#GIN indexes

`create index annotation_gin_idx on annotation using gin(json);`

`select c.collectionid, a.annotationid from annotation as a join annotation_collection as c on a.collectionid=c.id;`

`select 'http://localhost:8080/annotation/w3c/'||c.collectionid || '/'||a.annotationid as uri from annotation as a join annotation_collection as c on a.collectionid=c.id order by uri;`

