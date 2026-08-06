package eus.martico.fileTransfer.services.tabladetablas;

import java.util.List;
import java.util.Map;

//import eus.martico.fileTransfer.model.tabladetablas.vo.PuntosTransporteTiposEnum;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaCabeceraVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasFiltroVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * User security operations like login and logout, and CRUD operations.
 * 
 */
public interface TablaDeTablasIService {

  TablaDetalleVO find(MyServiceContext _context, long _ni) throws TablaDeTablasServiceException;

  List<TablaDetalleVO> getByCodigoCabecera(String _id) throws TablaDeTablasServiceException;
  List<TablaDetalleVO> getByCabeceraTipo(MyServiceContext _context, String _codigoCab, String _tipo) throws TablaDeTablasServiceException;
  
  List<TablaDetalleVO> findByFiltro(MyServiceContext _context, TablaDeTablasFiltroVO _filtro) throws TablaDeTablasServiceException;
  
  TablaCabeceraVO getCabeceraByCodigo(MyServiceContext _context, String _cabeceraCodigo) throws TablaDeTablasServiceException;
  TablaDetalleVO getDetalle(MyServiceContext _context, final String _tabla, final String _codigoDetalle) throws TablaDeTablasServiceException;
  
  Long getNi(final String _tabla, final String _codigoDetalle) throws TablaDeTablasServiceException;
  
  Map<String, List<TablaDetalleCodTabCab>> getAll(MyServiceContext _context) throws TablaDeTablasServiceException;
  
//  TablaDetalleVO getDetalleByTipoPorDefecto(MyServiceContext _context, String _codigoCab, PuntosTransporteTiposEnum _tipo) throws TablaDeTablasServiceException;
  
  TablaDetalleVO insertDetalle(MyServiceContext _context, TablaCabeceraVO _cabecera, TablaDetalleVO _detalle)  throws TablaDeTablasServiceException;
  

}
