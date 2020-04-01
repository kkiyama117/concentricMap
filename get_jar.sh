#!/bin/sh
curl -s https://api.github.com/repos/kkiyama117/concentricMap/releases/latest \
  | grep browser_download_url \
  | cut -d '"' -f 4 \
  | wget -i - -O app.jar
