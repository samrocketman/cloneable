#!/bin/bash -x
askpass="\${CLONEABLE_ASKPASS_LOCATION:-/tmp/askpass}"
cat > "\$askpass" <<'EOF'
#!/bin/bash
if [ -n "\${CLONEABLE_DEBUG:-}" ]; then
  echo cloneable askpass called with: "\$@" >&2
fi
case "\$1" in
  Username*)
    echo x-access-token
    ;;
  Password*)
    java \\
      -jar ${jarPath} \\
      -o "\${CLONEABLE_OWNER:-${owner}}" \\
      --print-auth-token
    ;;
esac
EOF
chmod 700 "\$askpass"
cat <<EOF
================================================================================
 ________________________________________
< Cloneable HTTP cloning with GitHub App >
 ----------------------------------------
        \\   ^__^
         \\  (oo)\\_______
            (__)\\       )\\/\\\\
                ||----w |
                ||     ||

GIT_ASKPASS installed to: "\$askpass"
CLONEABLE_OWNER default: ${owner}

If you want a different location for GIT_ASKPASS program installation, then set
environment variable CLONEABLE_ASKPASS_LOCATION and run this program again.

GitHub repository cloning assumes the following environment variables are set.

  - CLONEABLE_GITHUB_APP_ID
  - CLONEABLE_GITHUB_APP_KEY

If you want to clone repositories from a different organization than the set
CLONEABLE_OWNER default, then you'll need to set the environment variable to a
different organization.

  export CLONEABLE_OWNER=another-org

Before you clone https repositories be sure to set the following environment
variable.

  export GIT_ASKPASS='\$askpass'

GIT_ASKPASS is used by Git to perform HTTP authentication.
================================================================================
EOF
