#!/bin/bash

set -eu

set -x

lein do clean, uberjar
