package cloneable

import cloneable.errors.MissingCredentialException

import net.gleske.jervis.remotes.creds.ReadonlyTokenCredential

class Credential implements ReadonlyTokenCredential {

    private final secret

    Credential(String token = '') {
        File tokenFile = new File(token ?: '')
        this.secret = tokenFile.exists() ? tokenFile.text.trim() : token
        this.secret = this.secret ?: System.getenv('GITHUB_TOKEN')
        if(!this.secret) {
            throw new MissingCredentialException()
        }
    }

    String getToken() {
        this.secret
    }
}
