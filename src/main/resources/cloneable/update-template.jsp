#!/bin/sh
# Created by Sam Gleske
# Generated by Cloneable
# https://github.com/samrocketman/cloneable

<%
  if(isHttpUpdate) {
%>find . -maxdepth 1 -name '*.git' -print0 \\
  | xargs -0 -P16 -I'{}' -- /bin/bash -exc \\
  'cd "{}"; export CLONEABLE_OWNER="\$(git config remote.origin.url | cut -d/ -f4)"; git fetch'<%
} else {
%>find . -maxdepth 1 -name '*.git' -print0 \\
  | xargs -0 -P16 -I'{}' -- /bin/bash -exc 'cd "{}"; git fetch'<% } %>
