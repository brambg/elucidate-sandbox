curl -X POST \
     --data @create-container.json \
     -H 'Accept: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     -H 'Content-Type: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     -H 'Slug: examples' \
     $EL_BASE/w3c/
