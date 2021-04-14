#!/bin/bash
EL_BASE=http://localhost:8080/annotation
ACCEPT_HEADER='Accept: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"'
CONTENT_TYPE_HEADER='Content-Type: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"'
CONTAINER_URI=$EL_BASE/w3c/examples/

# create container
echo ">> create examples container"
curl -X POST \
     --data @create-container.json \
     -H $ACCEPT_HEADER \
     -H $CONTENT_TYPE_HEADER \
     -H 'Slug: examples' \
     $EL_BASE/w3c/
echo

# add annotations
echo ">> add annotations"
for j in create-annotation*.json; do
  curl --data @$j \
     -H $ACCEPT_HEADER \
     -H $CONTENT_TYPE_HEADER \
     $CONTAINER_URI
done
echo

# show results
echo ">> get examples container"
curl -i $CONTAINER_URI