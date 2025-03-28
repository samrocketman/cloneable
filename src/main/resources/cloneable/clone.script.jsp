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
      -k ${keyPath} \\
      -g ${appId} \\
      -o ${owner} \\
      --print-auth-token
    ;;
esac
EOF
chmod 700 "\$askpass"
git config --global credential.https://github.com.helper "\$askpass"
