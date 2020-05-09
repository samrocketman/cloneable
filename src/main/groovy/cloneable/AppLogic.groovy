package cloneable

class AppLogic {
    AppLogic() {
        throw new IllegalStateException('ERROR: you\'ve encountered a bug.  Add --debug option and open an issue.')
    }

    static void main(String token) {
        println new Credential(token).token
    }
}
