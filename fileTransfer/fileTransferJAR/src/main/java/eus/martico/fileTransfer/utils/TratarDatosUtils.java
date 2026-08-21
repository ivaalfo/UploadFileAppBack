package eus.martico.fileTransfer.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.model.pedido.factura.PedidoFacturaDAOImpMapper;
import eus.martico.fileTransfer.model.pedido.factura.PedidoFacturaVO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;


/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class TratarDatosUtils {
	
	protected static final Logger logger = LoggerFactory.getLogger(TratarDatosUtils.class);
	
	
	
	/**
	 * tratarDuplicados
	 * @param listaOriginal
	 * 
	 * @return List<PedidoProveedorVO>
	 */
	public static List<PedidoProveedorVO> tratarDuplicados(List<PedidoProveedorVO> listaOriginal) {
		Map<String, PedidoProveedorVO> mapa = new LinkedHashMap<>();
		
		for(PedidoProveedorVO actual : listaOriginal) {
			String key = actual.getTrack().trim() + "_" + 
					(actual.getExpediente() != null ? actual.getExpediente().trim() : "");
			
			if(!mapa.containsKey(key)) {
				mapa.put(key, actual);
			} else {
				//Lógica de desempate...
				PedidoProveedorVO existente = mapa.get(key);
				boolean exVacio = existente.getFechaEntReal() == null || existente.getFechaEntReal().getYear() <= 1900;
				if(exVacio) {
					mapa.put(key, actual);
				}
			}
		}
		return new ArrayList<>(mapa.values());
	}
	
	/**
	 * tratarOrigenDestino
	 * @param pedido PedidoProveedorVO
	 */
	public static void tratarOrigenDestino(PedidoProveedorVO pedido) {
		//Tratar Origen
		String origen = pedido.getOrigen();
		String origenResult = FileTransferUtils.capitalizarPrimeraLetra(origen);
		pedido.setOrigen(origenResult);

		//Tratar Destino
		String destino = pedido.getDestino();
		String destinoResult = FileTransferUtils.capitalizarPrimeraLetra(destino);
		pedido.setDestino(destinoResult);
	}
	
	/**
	 * tratarProvUser
	 * @param pedido PedidoProveedorVO
	 * @param cacheProveedores Map<String, String>
	 * @param usuarioDAO UsuarioIDAO
	 */
	public static void tratarProvUser(PedidoProveedorVO pedido, Map<String, String> cacheProveedores, UsuarioIDAO usuarioDAO) {
		if(pedido.getProveedorKy() != null){
			String provKy = pedido.getProveedor();
		    //Solo vamos a BD(FTARCHI) si no lo hemos buscado ya en este bucle
		    String usuProv = cacheProveedores.computeIfAbsent(provKy, ky -> {
		    	try {
					return usuarioDAO.getProvByCLICOD(ky);
				} catch (UsuarioDAOException e) {
					return null;
				}
		    });
			pedido.setProveedorUser(usuProv);
		}
	}
	
	/**
	 * tratarMotRechazo
	 * @param pedidoProv PedidoProveedorVO
	 * @param pedidoFiles PedidoFilesVO
	 * 
	 * @throws PedidoFilesException
	 */
	public static void tratarMotRechazo(PedidoProveedorVO pedidoProv, PedidoFilesVO pedidoFiles) throws PedidoFilesException {
		if(pedidoFiles == null) return;
		if(pedidoProv != null && pedidoProv.getEstado() != null){
			//Cogemos el motRec del pedido en cuestión de FTARCHI
			String motRecCode = pedidoFiles.getMotivoRechazo() != null ? pedidoFiles.getMotivoRechazo() : "";

			//Limpieza de prefijos para no duplicarlos
			if(motRecCode.startsWith("CMR y FAC rechazados: ")){
				motRecCode = motRecCode.substring("CMR y FAC rechazados: ".length());
			} else if(motRecCode.startsWith("CMR Rechazado: ")) {
				motRecCode = motRecCode.substring("CMR Rechazado: ".length());
			} else if(motRecCode.startsWith("Factura Rechazada: ")) {
				motRecCode = motRecCode.substring("Factura Rechazada: ".length());
			} else if(motRecCode.startsWith("Nota: ")) {
				motRecCode = motRecCode.substring("Nota: ".length());
			} else if(motRecCode.startsWith("Pedido deshabilitado: ")){
				motRecCode = motRecCode.substring("Pedido deshabilitado: ".length());
			}

			boolean pedidoRechazado = pedidoProv.getEstado().equals(Long.valueOf(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO));
			if(pedidoRechazado){
				boolean rechazadoCMR = Integer.valueOf(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO).equals(pedidoFiles.getEstadoCMR());
				boolean rechazadaFAC = Integer.valueOf(FileTransferConstantes.ESTADO_3_FICH_RECHAZADO).equals(pedidoFiles.getEstadoFAC());
				if(rechazadoCMR && rechazadaFAC){
					pedidoProv.setMotivoRechazo("CMR y FAC rechazados: "+motRecCode);
				} else if(rechazadoCMR){
					pedidoProv.setMotivoRechazo("CMR Rechazado: "+motRecCode);
				} else if(rechazadaFAC){
					pedidoProv.setMotivoRechazo("Factura Rechazada: "+motRecCode);
				}
			} else if(pedidoProv.getEstado().equals(Long.valueOf(FileTransferConstantes.ESTADO_9_PEDIDO_NULO))){
				pedidoProv.setMotivoRechazo("Pedido deshabilitado: " + motRecCode);
			} else {
				if(Integer.valueOf(FileTransferConstantes.CON_ANOTACIONES).equals(pedidoFiles.getAnotaciones())){
					pedidoProv.setMotivoRechazo("Nota: "+motRecCode);
				}
			}
		}
	}
	
	/**
	 * tratarFechasSubida
	 * @param pedido PedidoProveedorVO
	 * @param pedidoFiles PedidoFilesVO
	 * 
	 * @throws PedidoFilesException
	 */
	public static void tratarFechasSubida(PedidoProveedorVO pedido, PedidoFilesVO pedidoFiles) throws PedidoFilesException {
		if(pedidoFiles != null){
			//Guardamos el LocalDate para el FRONT
			pedido.setFechaSubidaCMR(pedidoFiles.getFechaSubidaCMR().toLocalDate());
			pedido.setFechaSubidaFAC(pedidoFiles.getFechaSubidaFAC().toLocalDate());
			
			//Guardamos el LocalDateTime completo para ordenar en el paso 4 de tratarListaPVal -> ordenarPorFecSubida
			pedido.setFechaSubidaCMRcompleta(pedidoFiles.getFechaSubidaCMR());
		} else {
			pedido.setFechaSubidaCMR(null);
			pedido.setFechaSubidaFAC(null);
			pedido.setFechaSubidaCMRcompleta(null);
		}
	}
	
	/**
	 * tratarDeshabilitados
	 * @param listaRecibida List<PedidoProveedorVO>
	 * 
	 * @return List<PedidoProveedorVO>
	 */
	public static List<PedidoProveedorVO> tratarDeshabilitados(List<PedidoProveedorVO> listaRecibida) {
		List<PedidoProveedorVO> listaAdevolver = new ArrayList<>();
		
		for(PedidoProveedorVO pProv : listaRecibida) {
			Long estadoPprov = pProv.getEstado();
			if(!estadoPprov.equals(new Long(FileTransferConstantes.ESTADO_9_PEDIDO_NULO))){
				listaAdevolver.add(pProv);
			}
		}
		return listaAdevolver;
	}
	
	/**
	 * tratarGrupajesInicial
	 * @param pedido PedidoProveedorVO
	 */
	public static void tratarGrupajesInicio(PedidoProveedorVO pedido) {
		String track = pedido.getTrack();
		if(track != null && track.startsWith("GRU")){
			pedido.setGrupaje(true);
			
			//El número de grupaje son los 3 ultimos del TR
			pedido.setGrupNum(track.substring(track.length() - 3));
			pedido.setGrupTR(track.substring(0, track.length() - 3));
			
			if(pedido.getFechaEntReal() == null){
				if(pedido.getHasCMR()==0) pedido.setHasCMR(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
				if(pedido.getHasFAC()==0) pedido.setHasFAC(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
				if(pedido.getHasOTR()==0) pedido.setHasOTR(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
			}
		}
	}
	
	
	/**
	 * refreshPedido
	 * @param pedido PedidoProveedorVO
	 * @param pedidoFiles PedidoFilesVO
	 * 
	 * @throws PedidoFilesException
	 */
	public static void refreshPedido(PedidoProveedorVO pedido, PedidoFilesVO pedidoFiles) throws PedidoFilesException {
		if(pedidoFiles != null) {
			//Si existe en FTARCHI los campos no pueden ser nulos
		    pedido.setHasCMR(pedidoFiles.getEstadoCMR().longValue());
		    pedido.setHasFAC(pedidoFiles.getEstadoFAC().longValue());
		    pedido.setFechaEntReal(pedidoFiles.getFechaEntReal().toLocalDate());
		    pedido.setEstado(pedidoFiles.getEstadoFacturacion().longValue());
		    pedido.setAnotaciones(pedidoFiles.getAnotaciones().longValue());
		    if(pedidoFiles.getRutaOTR() == null || pedidoFiles.getRutaOTR().trim().isEmpty()) {
		        pedido.setHasOTR(Long.valueOf(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO));
		    } else {
		        pedido.setHasOTR(Long.valueOf(FileTransferConstantes.ESTADO_1_FICH_SUBIDO));
		    }
		} else {
			pedido.setFechaEntReal(null);
			pedido.setHasCMR(Long.valueOf(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO));
	        pedido.setHasFAC(Long.valueOf(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO));
			pedido.setHasOTR(Long.valueOf(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO));
			if(pedido.isGrupaje()) {
		        pedido.setHasCMR(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
		        pedido.setHasFAC(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
		        pedido.setHasOTR(Long.valueOf(FileTransferConstantes.ESTADO_8_INIT_GRUP));
		    }
		}
	}
	
	/**
	 * tratarGrupSietesActiv
	 * @param lista List<PedidoProveedorVO>
	 * @param pedidoFilesDAO PedidoFilesIDAO
	 */
	public static void tratarGrupSietesPActivos(List<PedidoProveedorVO> lista, PedidoFilesIDAO pedidoFilesDAO) {
		List<PedidoProveedorVO> copiaLista = new ArrayList<>(lista);
		Iterator<PedidoProveedorVO> it = lista.iterator();

		while(it.hasNext()) {
			PedidoProveedorVO pedido = it.next();

			if(pedido.isGrupaje()){
				String currentGrupTR = pedido.getGrupTR();
				boolean hayHermanosVivos = false;

				//Buscamos si queda algún hermano que no sea estado 2
				for(PedidoProveedorVO hermano : copiaLista) {
					if(currentGrupTR != null && currentGrupTR.equals(hermano.getGrupTR())) {

						//El hermano es "otro" solo si TR o EXP son distintos
						boolean esElMismo = pedido.getTrack().equals(hermano.getTrack()) 
								&& pedido.getExpediente().equals(hermano.getExpediente());

						//Si el hermano NO es el mismo pedido Y su estado != 2
						if(!esElMismo && hermano.getEstado() != 2) {
							hayHermanosVivos = true;
							break;
						}
					}
				}
				
				//CASO A: Es un 7/7 que debe validarse y desaparecer
				if(pedido.getHasCMR() == 7 && pedido.getHasFAC() == 7 && pedido.getEstado() != 2) {
					if(!hayHermanosVivos) {
						try {
							Long estCMR = pedido.getHasCMR();
							Long estFAC = pedido.getHasFAC();
							Long estadoGEN = Long.valueOf(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
							pedidoFilesDAO.updateEstados(pedido.getTrack(), pedido.getExpediente(), estCMR, estFAC, estadoGEN);
							//Lo actualizamos en memoria
							pedido.setEstado(Long.valueOf(FileTransferConstantes.ESTADO_2_FICH_VALIDADO));
						} catch (Exception e) {
							logger.error("Error al validar 7/7 : {}", pedido.getTrack(), e.getMessage());
						}
						it.remove(); 
					}
				}
				//CASO B: Es un estado 2 (que trae la query nueva) pero su grupo ya está cerrado
				else if(pedido.getEstado() == 2) {
					if(!hayHermanosVivos) {
						it.remove(); //Desaparece de la vista porque el grupo ya terminó
					}
				}
			} 
			//CASO C: Pedido individual que ya es estado 2 (limpieza por si la query trae de más)
			else if(pedido.getEstado() == 2) {
				it.remove();
			}
		}
	}
	
	/**
	 * tratarGrupSietesPValidador
	 * @param lista List<PedidoProveedorVO>
	 * @param pedidoFilesDAO PedidoFilesIDAO
	 */
	public static void tratarGrupSietesPValidador(List<PedidoProveedorVO> lista, PedidoFilesIDAO pedidoFilesDAO) {
		if(lista == null || lista.isEmpty()) return;
		Iterator<PedidoProveedorVO> it = lista.iterator();
		
		while(it.hasNext()) {
			PedidoProveedorVO pedAux = it.next();
			
			//Si hay hermanos 'rezagados'
			if (pedAux.isGrupaje()){
				int rezagados = pedidoFilesDAO.countHermanosEnActivos(pedAux.getGrupTR());
				if (rezagados > 0){
					it.remove();
					continue; 		//Saltamos al siguiente registro sin evaluar el resto de condiciones
				}
			}
		}
	}
	
	/**
	 * tratarGrupSietesPHistorico
	 * @param lista List<PedidoProveedorVO>
	 * @param pedidoFilesDAO PedidoFilesIDAO
	 * @throws PedidoFilesException 
	 */
	public static void tratarGrupSietesPHistorico(List<PedidoProveedorVO> lista, PedidoFilesIDAO pedidoFilesDAO) throws PedidoFilesException {
		if(lista == null || lista.isEmpty()) return;
		
		Iterator<PedidoProveedorVO> it = lista.iterator();
		
		while(it.hasNext()) {
			PedidoProveedorVO pedido = it.next();
			
			//Un 0 nunca es histórico
			if(pedido.getHasCMR() != 2 && pedido.getHasCMR() != 7 && pedido.getEstado() != 9) {
				it.remove();
				continue;
			}
			
			if(pedido.isGrupaje() && pedido.getEstado() != 9) {		//Los anulados siempre se mantienen
				String currentGrupTR = pedido.getGrupTR();
				
				//¿Queda algún CMR sin validar en el grupo?
				int pendientes = pedidoFilesDAO.countHermanosPorValidarCMR(currentGrupTR);
				
				if(pendientes == 0) {
					//Si ya no quedan "0" o "1" en toda la tabla para este grupo:
					if(pedido.getHasCMR() == 7 && pedido.getHasFAC() == 7 && pedido.getEstado() != 2) {
						
						//¿Queda algún hermano con estado != 2 (sin cerrar)?
						int sinCerrar = pedidoFilesDAO.countHermanosNoValidados(currentGrupTR);
						if(sinCerrar != 0) {
							//Si todo el grupo está cerrado, le ponemos el 2 al 7/7
							Long estCMR = pedido.getHasCMR();
							Long estFAC = pedido.getHasFAC();
							Long estadoGEN = Long.valueOf(FileTransferConstantes.ESTADO_2_FICH_VALIDADO);
							pedidoFilesDAO.updateEstados(pedido.getTrack(), pedido.getExpediente(), estCMR, estFAC, estadoGEN);
							//Lo actualizamos en memoria
							pedido.setEstado(Long.valueOf(FileTransferConstantes.ESTADO_2_FICH_VALIDADO));
						}
					}
					//Si el grupo está validado, el pedido se queda en la lista (no hacemos it.remove)
				} else {
					//Si el grupo NO está validado, este 7/x todavía no debe verse aquí
					it.remove();
				}
			}
		}
	}
	
	/**
     * Ordena la lista de pedidos por fechaSubidaCMR de forma ascendente (más antiguos primero).
     * Los elementos con fecha nula se colocan al final. No deberian venir nulos en este punto de la aplicacion
     * 
	 * @param lista List<PedidoProveedorVO>
	 */
	public static void ordenarPorFechaSubidaCompleta(List<PedidoProveedorVO> lista){
		if (lista == null || lista.isEmpty()) {
            return;
        }
		
		lista.sort(Comparator.comparing(
				pedido -> {
		            java.time.LocalDateTime fecha = pedido.getFechaSubidaCMRcompleta();
		            //Si es nulo o año 1, devolvemos null para que vaya al final
		            if (fecha == null || fecha.getYear() == 1) {
		                return null;
		            }
		            return fecha;
		        }, 
				Comparator.nullsLast(Comparator.naturalOrder())
		));
	}
	
	/**
	 * reordenarGrup
	 * @param lista List<PedidoProveedorVO>
	 * @return List<PedidoProveedorVO>
	 */
	public static List<PedidoProveedorVO> reordenarGrup(List<PedidoProveedorVO> lista) {
		List<PedidoProveedorVO> listaOrdenada = new ArrayList<>();
		Set<String> procesadosKeys = new HashSet<>();
		
		if(lista == null) return listaOrdenada;
		
		for(PedidoProveedorVO p : lista) {
			if(p == null) continue;
			
//			logger.info("SALIDA_JAVA :: TR: {} :: EXP: {} :: CMR: {} :: FAC: {} :: GrupTR: {}", 
//                    p.getTrack(), p.getExpediente(), p.getHasCMR(), p.getHasFAC(), p.getGrupTR());
			
			//Generamos la clave única para este registro
			String keyActual = p.getTrack().trim() + "_" + (p.getExpediente() != null ? p.getExpediente().trim() : "");
			
			if(procesadosKeys.contains(keyActual)) continue;
			
			if(p.isGrupaje() && p.getGrupTR() != null) {
				//Es un grupaje: buscamos todos los que pertenecen al mismo grupo (GrupTR)
				String grupoActual = p.getGrupTR();
				//Recolectamos todos los hermanos del mismo grupo
				List<PedidoProveedorVO> hermanosDelGrupo = new ArrayList<>();
				
				for(PedidoProveedorVO hermano : lista) {
					if(hermano != null && grupoActual.equals(hermano.getGrupTR())) {
						String keyHermano = hermano.getTrack().trim() + "_" + 
								(hermano.getExpediente() != null ? hermano.getExpediente().trim() : "");
						
						if(!procesadosKeys.contains(keyHermano)) {
							hermanosDelGrupo.add(hermano);
						}
					}
				}
				
				//Ordenamos SOLO este pequeño bloque de hermanos por Expediente y luego por Track para que siempre salgan igual
				hermanosDelGrupo.sort(Comparator.comparing(PedidoProveedorVO::getExpediente)
						.thenComparing(PedidoProveedorVO::getTrack));
				
				//Los añadimos todos juntos a la lista final y a procesados
				for(PedidoProveedorVO h : hermanosDelGrupo) {
					listaOrdenada.add(h);
					String keyH = h.getTrack().trim() + "_" + (h.getExpediente() != null ? h.getExpediente().trim() : "");
					procesadosKeys.add(keyH);
				}
			} else {
				//No es grupaje: lo añadimos tal cual respetando su posición original
				listaOrdenada.add(p);
				procesadosKeys.add(keyActual);
			}
		}
		return listaOrdenada;
	}
	
	/**
	 * obtenerFacNumber
	 * @param pedido PedidoProveedorVO
	 * @param template JdbcTemplate
	 */
	public static void obtenerFacNumber(PedidoProveedorVO pedido, JdbcTemplate template) {
		String track = pedido.getTrack();
		String exp = pedido.getExpediente();

		//Query 1: Facturas contabilizadas
		String sqlSelectFacNumByTrAndExp = "SELECT * FROM FTRCONTA WHERE F2TR = ? AND F2EXPE = ?";

		//Query 2: Facturas no contabilizadas (FAIMPINV).
		//Extrae el número de factura ordenando por la fecha de exportación más reciente
		String sqlSelectFacNumNoConta = "SELECT REPLACE(TRIM(CHAR(INVOICE_NUMBER)), ' ', '') "
				+ " FROM FAIMPINV "
				+ " WHERE DOCCOMMENT = ? "
				+ " ORDER BY EXPORTDATE DESC";

		if(track != null && exp != null){
			//1. Buscamos en las contabilizadas
			List<PedidoFacturaVO> listPfac = template.query(sqlSelectFacNumByTrAndExp,
					new PedidoFacturaDAOImpMapper(), track, exp);

			if(!listPfac.isEmpty()){
				pedido.setFacNumber(listPfac.get(0).getFacNumExcell());
			} else {
				//2. Si no encuentra nada, buscamos en las NO contabilizadas
				List<String> listFacNoConta = template.queryForList(sqlSelectFacNumNoConta, String.class, track);

				if(listFacNoConta.isEmpty()){
					pedido.setFacNumber(null);
				} else {
	                //Al estar ordenado por EXPORTDATE DESC, la posición 0 es la factura más nueva/actualizada
					pedido.setFacNumber(listFacNoConta.get(0));
				}
			}
		}
	}
	
	/**
	 * obtenerProvNameDestName
	 * @param pedido PedidoFirmadorVO
	 * @param usuarioDAO UsuarioIDAO
	 * @throws UsuarioDAOException
	 */
	public static void obtenerProvNameDestName(PedidoFirmadorVO pedido, UsuarioIDAO usuarioDAO) 
			throws UsuarioDAOException {
		//Obtener Trasportista
		String trasName = usuarioDAO.getProvByCLICOD(pedido.getF3cliCod());
		pedido.setProvUser(trasName);
		
		//Obtener Destinatario
		String destName = usuarioDAO.getProvByCLICOD(pedido.getF3destCod());
		pedido.setDestUser(destName);
	}
	
	/**
	 * tratarFecCorte
	 * @param listaRecibida List<PedidoProveedorVO>
	 * 
	 * @return List<PedidoProveedorVO>
	 */
	public static List<PedidoProveedorVO> tratarFechaCorte(List<PedidoProveedorVO> listaRecibida) {
		List<PedidoProveedorVO> listaAdevolver = new ArrayList<>();
		LocalDate fechaCorte = LocalDate.of(2026, 3, 1);
		
		for(PedidoProveedorVO actual : listaRecibida) {
			LocalDate fechaNormalizada = normalizarFechaCarga(actual.getFechaCarga());
			
			//Si no se puede normalizar (null), se queda en la lista.
			//Si se normaliza, solo entra si es igual o posterior a la fecha de corte.
			if(fechaNormalizada == null || !fechaNormalizada.isBefore(fechaCorte)) {
				listaAdevolver.add(actual);
			}
		}
		return listaAdevolver;
	}

	/**
	 * normalizarFechaCarga
	 * @param fechaRaw
	 * @return
	 */
	private static LocalDate normalizarFechaCarga(String fechaRaw) {
		if(fechaRaw == null || fechaRaw.isEmpty()) return null;
		
		try {
			//Limpieza: primeros 10 caracteres y normalizar separadores a guiones
			String limpia = fechaRaw.substring(0, Math.min(fechaRaw.length(), 10))
					.replace('.', '-')
					.replace('/', '-');
			
			//Definir los formatos posibles
			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
					.appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
					.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
					.toFormatter();
			
			return LocalDate.parse(limpia, formatter);
		} catch (Exception e) {
			//Si falla el parseo, devolvemos null para que el pedido no se descarte
			return null;
		}
	}

}
