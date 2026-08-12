package eus.martico.fileTransfer.model.pedido.proveedor;

import java.util.List;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class PedidoGrupajeFechasVO {
	//Clase personalizada para recibir el array de fechas de los grupajes

	public List<FechaItem> arrayFechas;

    public static class FechaItem {
        public String track;
        public String expediente;
        public String fechaEntReal;
    }
    
}
