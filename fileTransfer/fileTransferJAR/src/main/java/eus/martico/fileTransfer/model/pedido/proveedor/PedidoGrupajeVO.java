package eus.martico.fileTransfer.model.pedido.proveedor;

import java.util.List;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class PedidoGrupajeVO {
	//Clase personalizada para recibir el array de grupajes
		
	public String grupTR;
	public List<GrupajeItem> arrayGrulog;

	public static class GrupajeItem {
		public String grupNum;
		public String expediente;
		public String refCarga;
		public boolean opcionCMR;
		public boolean opcionFAC;
		
		public String getGrupNum() {
			return grupNum;
		}
		public void setGrupNum(String grupNum) {
			this.grupNum = grupNum;
		}
		public String getExp() {
			return expediente;
		}
		public void setExp(String expediente) {
			this.expediente = expediente;
		}
		public String getRefCarga() {
			return refCarga;
		}
		public void setRefCarga(String refCarga) {
			this.refCarga = refCarga;
		}
		public boolean opcionCMR() {
			return opcionCMR;
		}
		public void setOpcionCMR(boolean opcionCMR) {
			this.opcionCMR = opcionCMR;
		}
		public boolean opcionFAC() {
			return opcionFAC;
		}
		public void setOpcionFAC(boolean opcionFAC) {
			this.opcionFAC = opcionFAC;
		}
		
	}

	public String getGrupTR() {
		return grupTR;
	}

	public void setGrupTR(String grupTR) {
		this.grupTR = grupTR;
	}

	public List<GrupajeItem> getSeleccionados() {
		return arrayGrulog;
	}

	public void setSeleccionados(List<GrupajeItem> seleccionados) {
		this.arrayGrulog = seleccionados;
	}
	
	
}
