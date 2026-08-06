package eus.martico.fileTransfer.model.usuario.rol;

import eus.martico.fileTransfer.config.FileTransferConstantes;

public enum TipoRolEnum {
	
	//La descripcion es para la tabla del mantenimiento de usuarios. Se puede poner por properties
    NO_ACCESO(FileTransferConstantes.PROPERTY_KEY_ROL_NO_ACCESS, "Sin Acceso", FileTransferConstantes.ROL_NUMBER_NO_ACCESS),
    EXTERNO(FileTransferConstantes.PROPERTY_KEY_ROL_EXTERNO, "Externo", FileTransferConstantes.ROL_NUMBER_EXTERNO),
    CONSULTA(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA, "Consulta", FileTransferConstantes.ROL_NUMBER_CONSULTA),
    INTERNO(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO, "Interno", FileTransferConstantes.ROL_NUMBER_INTERNO),
    ADMIN(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN, "Admin", FileTransferConstantes.ROL_NUMBER_ADMIN);

    private final String key;
    private final String descripcion;
    private final int codigoNumerico;

    TipoRolEnum(String key, String descripcion, int codigoNumerico) {
        this.key = key;
        this.descripcion = descripcion;
        this.codigoNumerico = codigoNumerico;
    }

    //Método para encontrar el Enum por el String que recibes (w3tipo)
    public static TipoRolEnum desdeKey(String key) {
        for(TipoRolEnum tipo : values()) {
            if(tipo.key.equals(key)) {
            	return tipo;
            }
        }
        return null;
    }

    public String getDescripcion() { 
    	return descripcion; 
    }
    public int getCodigoNumerico() {
    	return codigoNumerico;
    }
}
