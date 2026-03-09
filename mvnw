#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVA_HOME if not already set
if [ -z "${JAVA_HOME-}" ]; then
  if [ -x /usr/libexec/java_home ]; then
    JAVA_HOME="$(/usr/libexec/java_home)" || true
  fi
fi

# Determine the Java command to use
if [ -n "${JAVA_HOME-}" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ]; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
  fi
else
  JAVACMD="$(\unset -f command 2>/dev/null; \command -v java)" || true
  if [ ! -x "${JAVACMD-}" ]; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
  fi
fi

# Hash of the wrapper jar
wrapperJarPath="$(cd -- "$(dirname -- "$0")" && pwd -P)/.mvn/wrapper/maven-wrapper.jar"
wrapperUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

# Download maven-wrapper.jar if needed
while [ -d "$wrapperJarPath" ]; do
  echo "Deleting stale wrapper jar directory $wrapperJarPath" >&2
  rm -rf "$wrapperJarPath"
done

if [ ! -e "$wrapperJarPath" ]; then
  if [ "${MVNW_VERBOSE-}" = true ]; then
    echo "Downloading $wrapperUrl"
  fi

  if command -v wget > /dev/null 2>&1; then
    QUIET=""
    if [ "${MVNW_VERBOSE-}" != true ]; then
      QUIET="--quiet"
    fi
    if [ -z "${MVNW_USERNAME-}" ] || [ -z "${MVNW_PASSWORD-}" ]; then
      wget $QUIET "$wrapperUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
    else
      wget $QUIET --http-user="$MVNW_USERNAME" --http-password="$MVNW_PASSWORD" "$wrapperUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
    fi
  elif command -v curl > /dev/null 2>&1; then
    QUIET=""
    if [ "${MVNW_VERBOSE-}" != true ]; then
      QUIET="--silent"
    fi
    if [ -z "${MVNW_USERNAME-}" ] || [ -z "${MVNW_PASSWORD-}" ]; then
      curl $QUIET -o "$wrapperJarPath" "$wrapperUrl" -f -L || rm -f "$wrapperJarPath"
    else
      curl $QUIET --user "$MVNW_USERNAME:$MVNW_PASSWORD" -o "$wrapperJarPath" "$wrapperUrl" -f -L || rm -f "$wrapperJarPath"
    fi
  else
    echo "ERROR: Could not find 'wget' or 'curl' to download Maven wrapper." >&2
    exit 1
  fi
fi

if [ ! -e "$wrapperJarPath" ]; then
  echo "ERROR: Could not download Maven wrapper jar." >&2
  exit 1
fi

# Determine the project base directory
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd -- "$(dirname -- "$0")" && pwd -P)}"
MAVEN_OPTS="${MAVEN_OPTS-}"

exec "$JAVACMD" \
  $MAVEN_OPTS \
  ${MAVEN_ADDITIONAL_OPTS-} \
  -classpath "$wrapperJarPath" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
