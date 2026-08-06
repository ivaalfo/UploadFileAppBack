package eus.martico.fileTransfer.model.tabladetablas.vo;

import java.time.format.DateTimeFormatter;

public class TablaDeTablasFiltroVO {

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss");

	private String codigoDetalle;
	private String codigoCabecera;

	public String getCodigoCabecera() {
		return codigoCabecera;
	}

	public void setCodigoCabecera(String codigoCabecera) {
		this.codigoCabecera = codigoCabecera;
	}

	private TablaDeTablasCampoEnum ordenCampo = TablaDeTablasCampoEnum.DETALLE_CODIGO; // Le damos un valor por defecto
	private int ordenDirec=1; // ASC=1, DESC=-1

	public String getCodigoDetalle() {
		return codigoDetalle;
	}
	public void setCodigoDetalle(String _codigoDetalle) {
		this.codigoDetalle = _codigoDetalle;
	}
	public TablaDeTablasCampoEnum getCampoOrden() {
		return ordenCampo;
	}
	public void setCampoOrden(final String _ordenCampo) {
		if (_ordenCampo!=null) {
			if (_ordenCampo.startsWith("-") && _ordenCampo.length() > 1) {
				this.setOrden(-1);
				this.ordenCampo = TablaDeTablasCampoEnum.valueOf(_ordenCampo.substring(1).toUpperCase());
			}
			else if (_ordenCampo.startsWith("+") && _ordenCampo.length() > 1) {
				this.setOrden(1);
				this.ordenCampo = TablaDeTablasCampoEnum.valueOf(_ordenCampo.substring(1).toUpperCase());
			}
			else {
				this.ordenCampo = TablaDeTablasCampoEnum.valueOf(_ordenCampo.toUpperCase());
			}
		}		
		
	}
	public int getOrden() {
		return ordenDirec;
	}
	public void setOrden(int ordenDirec) {
		this.ordenDirec = ordenDirec;
	}	
}
