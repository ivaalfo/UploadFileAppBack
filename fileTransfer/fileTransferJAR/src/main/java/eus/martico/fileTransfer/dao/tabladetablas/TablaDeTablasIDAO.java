package eus.martico.fileTransfer.dao.tabladetablas;

import java.util.List;

import eus.martico.fileTransfer.dao.BaseIDAO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaCabeceraVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasFiltroVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;

public interface TablaDeTablasIDAO extends BaseIDAO {
		
	  TablaDetalleVO find(Long _ni) throws TablaDeTablasDAOException;

	  List<TablaDetalleVO> getByCodigoCabecera(String _codigoCab) throws TablaDeTablasDAOException;

	  /* Devuelve un subgrupo dentro de una cabecera, los que tengan el tipo indicado */
	  List<TablaDetalleVO> getByCabeceraTipo(String _codigoCab, String _tipo) throws TablaDeTablasDAOException;

	  List<TablaDetalleVO> findByFiltro(TablaDeTablasFiltroVO _filtro) throws TablaDeTablasDAOException;
	  
	  Long nextNi() throws TablaDeTablasDAOException;

	  List<TablaDetalleCodTabCab> getAll() throws TablaDeTablasDAOException;

	  TablaDetalleVO getDetalle(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException;
	  TablaDetalleVO getDetalle(String _tabla, String _codigoDetalle, boolean _conInactivos) throws TablaDeTablasDAOException;
	  
	  /* Por el campo Clave */
	  TablaDetalleVO getDetalleByClave(String _tabla, String _claveDetalle) throws TablaDeTablasDAOException;
	  
	  Long getNi(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException;
	  
	  Short getIndice(String _tabla, String _codigoDetalle) throws TablaDeTablasDAOException;
	  
	  TablaDetalleVO insertDetalle(TablaCabeceraVO _cabecera, TablaDetalleVO _detalle) throws TablaDeTablasDAOException;
	  TablaCabeceraVO getCabeceraByCodigo(String _cabeceraCodigo) throws TablaDeTablasDAOException;
}
