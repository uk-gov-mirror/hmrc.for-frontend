#!/bin/sh

SCRIPT=$(find . -type f -name for-frontend)
exec $SCRIPT \
  $HMRC_CONFIG
