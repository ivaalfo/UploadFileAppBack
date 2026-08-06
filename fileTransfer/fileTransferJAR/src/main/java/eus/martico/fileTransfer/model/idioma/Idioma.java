package eus.martico.fileTransfer.model.idioma;

public enum Idioma {
	CASTELLANO("ES"),
	EUSKERA("EUS");
	
	private final String value;

	private Idioma(String codOpera) {
	
		this.value=codOpera;
	}

	public String getValue() {
		return value;
	}
	
}
