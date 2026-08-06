package eus.martico.fileTransfer.seguridad.model;

public enum JWTScopes {
    REFRESH_TOKEN;    

    public String authority() {

        return this.name();

    }
}
