See https://docs.github.com/en/free-pro-team@latest/actions/reference/encrypted-secrets

To add a new encryption key (this will need to be done anytime secrets.xml is updated):
1. gpg --symmetric --cipher-algo AES256 secrets.xml
2. Delete the old key in github under the secrets tab
3. Add the new key with the same name (new key though)
4. Upload the new secretx.xml.gpg