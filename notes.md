

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


