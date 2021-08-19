#!/bin/bash
EL_BASE=$1
if [[ -z $EL_BASE ]]; then
  echo "Usage: $0 <elucidate_base_url>"
  echo "example: $0 http://localhost:8080/annotation"
  exit 1
fi
CONTAINER_URI=$EL_BASE/w3c/examples/

# create container
echo ">> create examples container"
curl -X POST \
     --data @container.json \
     -H 'Accept: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     -H 'Content-Type: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     -H 'Slug: examples' \
     $EL_BASE/w3c/
echo

# add annotations
echo ">> add annotations"
for j in annotation*.json; do
  curl --data @$j \
     -H 'Accept: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     -H 'Content-Type: application/ld+json; profile="http://www.w3.org/ns/anno.jsonld"' \
     $CONTAINER_URI
done
echo

# show results
echo ">> get examples container"
curl -i $CONTAINER_URI