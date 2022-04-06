#!/usr/bin/env bash

cd ~/workspaces/elucidate/elucidate-server/elucidate-parent && mvn package && echo

cd ~/workspaces/elucidate/elucidate-server/elucidate-server

version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
image=registry.diginfra.net/tt/huc-elucidate-server:$version

docker build -t $image -f Dockerfile .
result=$?
echo
if [ $result == 0 ]; then
  echo image built:
  docker images | grep elucidate
  echo
  echo "to run:"
  echo "  docker run --rm -p 8080:8080 --name huc-elucidate-server $image"
  echo "to push:"
  echo "  docker push $image"
  echo
else
  echo "error, no image built"
fi
