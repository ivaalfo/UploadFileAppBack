package eus.martico.fileTransfer.model.tabladetablas.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TablaDetalleCodTabCab extends TablaDetalleVO {
	@JsonIgnore
	private  String tabcab_cod;

	public String getTabcab_cod() {
		return tabcab_cod;
	}

	public void setTabcab_cod(String tabcab_cod) {
		this.tabcab_cod = tabcab_cod;
	}
	
	

}
