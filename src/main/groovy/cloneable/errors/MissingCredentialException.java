package cloneable.errors;

class MissingCredentialException extends ErrorException {
    public MissingCredentialException(String message) {
        super(message);
    }
    public MissingCredentialException() {
        this("No GitHub token credential provided or GITHUB_TOKEN environment variable missing.");
    }
}
