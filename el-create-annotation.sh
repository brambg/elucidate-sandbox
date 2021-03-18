curl --data @create-annotation.json \
     --header 'Accept: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     --header 'Content-Type: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     --header 'Slug: annotation_0001' \
     $EL_BASE/w3c/testcontainer/