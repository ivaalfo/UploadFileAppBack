package eus.martico.fileTransfer.services.tabladetablas;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.dao.tabladetablas.TablaDeTablasDAOException;
import eus.martico.fileTransfer.dao.tabladetablas.TablaDeTablasIDAO;
//import eus.martico.fileTransfer.model.tabladetablas.vo.PuntosTransporteTiposEnum;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaCabeceraVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDeTablasFiltroVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleCodTabCab;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * User security operations like login and logout, and CRUD operations on {@link User}.
 * 
 * @author 
 *
 */
@Service(value = "TablaDeTablasIService")
public class TablaDeTablasServiceImp implements TablaDeTablasIService {

	protected static final Logger logger = LoggerFactory.getLogger(TablaDeTablasServiceImp.class);
			
	@Autowired 
	private TablaDeTablasIDAO tablaDeTablasDAO;

	@Override
	public TablaDetalleVO find(MyServiceContext _context,  long _ni)  throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.find(_ni);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	public List<TablaDetalleVO> getByCodigoCabecera(String _codigo)
			throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.getByCodigoCabecera(_codigo);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	public List<TablaDetalleVO> findByFiltro(MyServiceContext _context, TablaDeTablasFiltroVO _filtro) throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.findByFiltro(_filtro);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	public Map<String, List<TablaDetalleCodTabCab>> getAll(MyServiceContext _context) throws TablaDeTablasServiceException {
		try {
			List<TablaDetalleCodTabCab> tabla = tablaDeTablasDAO.getAll();
			Map<String, List<TablaDetalleCodTabCab>> mapPorCodigoCabecera = tabla.stream()
								.collect(Collectors.groupingBy(TablaDetalleCodTabCab::getTabcab_cod));
			return mapPorCodigoCabecera;
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional( readOnly = true )
	public TablaDetalleVO getDetalle(MyServiceContext _context, final String _tabla, final String _codigoDetalle) throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.getDetalle(_tabla, _codigoDetalle);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}
	@Override
	@Transactional( readOnly = true )
	public Long getNi(final String _tabla, final String _codigoDetalle) throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.getNi(_tabla, _codigoDetalle);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	public List<TablaDetalleVO> getByCabeceraTipo(MyServiceContext _context, String _codigoCab, String _tipo)
			throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.getByCabeceraTipo(_codigoCab, _tipo);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public TablaDetalleVO insertDetalle(MyServiceContext _context, TablaCabeceraVO _cabecera, TablaDetalleVO _detalle)
			throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.insertDetalle(_cabecera, _detalle);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}

	@Override
	public TablaCabeceraVO getCabeceraByCodigo(MyServiceContext _context, String _cabeceraCodigo) throws TablaDeTablasServiceException {
		try {
			return tablaDeTablasDAO.getCabeceraByCodigo(_cabeceraCodigo);
		} catch (TablaDeTablasDAOException e) {
			throw new TablaDeTablasServiceException(e.getMessage());
		}
	}
	
}
