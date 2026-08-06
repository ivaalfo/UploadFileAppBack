package eus.martico.fileTransfer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import eus.martico.fileTransfer.sql.SqlOperadorEnum;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public abstract class DAOFiltro {

	protected static final String SQL_ORDER_BY = " ORDER BY ";
	protected static final char SQL_CARACTER_LIKE_CADENA = '%';
	protected static final String SQL_CARACTER_LIKE_CADENA_Y_COMA = SQL_CARACTER_LIKE_CADENA + ",";
	protected static final String SQL_CARACTER_COMA_Y_LIKE_CADENA = "," + SQL_CARACTER_LIKE_CADENA;
	
	public enum TipoOperadorFiltro {
		IGUAL("="),
		MENOR("<"),
		MAYOR(">"),
		MENORIGUAL("<="),
		MAYORIGUAL(">="),
		DISTINTO("<>"),
		ISNULLL("IS NULL"),
		ISNOTNULLL("IS NOT NULL"),
		LIKE("LIKE")
		;
		
		private String text;

		TipoOperadorFiltro(String text) {
	        this.text = text;
	    }
		
		public String getText() {
	        return this.text;
	    }
	}

	public class CampoFiltro {
		String campo;
		TipoOperadorFiltro operador;
		SqlOperadorEnum AndOr;
		byte iniFin = 0;

		public CampoFiltro(String _campo, TipoOperadorFiltro _operador) {
			this.campo = _campo;
			this.operador = _operador;
			this.AndOr = SqlOperadorEnum.AND;
		}
		public CampoFiltro(String _campo, TipoOperadorFiltro _operador, SqlOperadorEnum _AndOr) {
			this.campo = _campo;
			this.operador = _operador;
			this.AndOr = _AndOr;
		}
		public String getCampo() {
			return this.campo;
		}
		public TipoOperadorFiltro getOperador() {
			return this.operador;
		}
		public SqlOperadorEnum getAndOr() {
			return AndOr;
		}
		public byte getIniFin() {
			return iniFin;
		}
		public void setIniFin(byte iniFin) {
			//1 = inicio, -1 = fin del bloque OR
			this.iniFin = iniFin;
		}
	}

	protected List<CampoFiltro> campos = new ArrayList<CampoFiltro>();
	
	public void addCampo(String _nombreCampo, TipoOperadorFiltro _tipoOperador) {
		CampoFiltro campoFiltro = new CampoFiltro(_nombreCampo, _tipoOperador);
		campos.add(campoFiltro);
	}
	
	public void addCampo(String _nombreCampo, TipoOperadorFiltro _tipoOperador, SqlOperadorEnum _AndOr) {
		CampoFiltro campoFiltro = new CampoFiltro(_nombreCampo, _tipoOperador, _AndOr);
		campos.add(campoFiltro);
	}
	
	public void addCampo(final CampoFiltro _campoFiltro) {
		campos.add(_campoFiltro);
	}
	
	public boolean isEmpty() {
		return campos.isEmpty();
	}
	
	public String getSQLWhere() {		
		if (this.isEmpty())
			return null;
		else {
			StringBuilder sqlWhere = new StringBuilder(" WHERE ");
			boolean primero = true;
			for(CampoFiltro campoFiltro: campos){
				if(campoFiltro.getAndOr() == SqlOperadorEnum.OR){
					if(campoFiltro.getIniFin() > 0){
						//Si es un OR y no es el primer campo del where pone un AND antes del parentesis
						if(!primero){
							sqlWhere.append(" ");
							sqlWhere.append(SqlOperadorEnum.AND);
							sqlWhere.append(" ");
						}
						//Si es un OR y es el primero del bloque OR abrimos parentesis
						sqlWhere.append("(");
					} else {
						sqlWhere.append(" ");
						sqlWhere.append(campoFiltro.getAndOr().getValor());
						sqlWhere.append(" ");				
					}
				} else {
					if(!primero){
						sqlWhere.append(" ");
						sqlWhere.append(campoFiltro.getAndOr().getValor());
						sqlWhere.append(" ");
					}
				}
				sqlWhere.append(campoFiltro.getCampo());
				sqlWhere.append(" ");
				sqlWhere.append(campoFiltro.getOperador().getText());
				switch(campoFiltro.getOperador()){
					case ISNULLL:
						break;
					case ISNOTNULLL:
						break;
					default:
						sqlWhere.append(" ?");
				}
				if(campoFiltro.getAndOr() == SqlOperadorEnum.OR){
					//Si es un OR y es el ultimo del bloque OR cerramos parentesis
					if(campoFiltro.getIniFin() < 0){
						sqlWhere.append(")");
					}
				}
				if(primero){
					primero = false;
				}
			}
			return sqlWhere.toString();
		}
	}

	public String getSQLOrdenDireccion(byte _orden) {
		if(_orden < 0){
			return " DESC";
		} else {
			return " ASC";
		}
	}
	
	protected void setFiltroOR(List<Object> _lista, String _campo, final boolean _orIsNUll) {
		int x = 0;
		for(@SuppressWarnings("unused") Object obj: _lista) {
			++x;
			CampoFiltro campoFiltro = new CampoFiltro(_campo, DAOFiltro.TipoOperadorFiltro.IGUAL, SqlOperadorEnum.OR);
			if(x == 1){
				campoFiltro.setIniFin((byte)x);
			}
			//Marcamos que es el ultimo del bloque OR
			if(!_orIsNUll && x==_lista.size()){
				campoFiltro.setIniFin((byte)(campoFiltro.getIniFin()-1));
			}
			this.addCampo(campoFiltro);
		}
		if(_orIsNUll) {
			//Metemos al final el OR campo IS NULL
			CampoFiltro campoFiltro = new CampoFiltro(_campo, DAOFiltro.TipoOperadorFiltro.ISNULLL, SqlOperadorEnum.OR);
			campoFiltro.setIniFin((byte)(campoFiltro.getIniFin()-1));
			this.addCampo(campoFiltro);
		}
	}
	
	protected void setFiltroLIKEOR(String[] _lista, String _campo) {
		int x = 0;
		for(@SuppressWarnings("unused") String str: _lista) {
			++x;
			CampoFiltro campoFiltro = new CampoFiltro(_campo, DAOFiltro.TipoOperadorFiltro.LIKE, SqlOperadorEnum.OR);
			if(x == 1){ 
				campoFiltro.setIniFin((byte)x);
			}
			if(x == _lista.length){
				campoFiltro.setIniFin((byte)(campoFiltro.getIniFin()-1));
			}
			this.addCampo(campoFiltro);
		}
	}
	
	protected void setFiltroOR(String[] _lista, String _campo) {
		setFiltroOR(_lista, _campo, false);
	}
	
	protected void setFiltroOR(String[] _lista, String _campo, final boolean _orIsNull) {
		setFiltroOR(Arrays.asList(_lista), _campo, _orIsNull);
	}
	
	protected void setFiltroOR(long[] _arrl, String _campo) {
		setFiltroOR(_arrl, _campo, false);
	}
	
	protected void setFiltroOR(long[] _arrl, String _campo, final boolean _orIsNull) {
		if(_arrl != null){
			List<Long> list = Arrays.stream(_arrl).boxed().collect(Collectors.toList());
			Long[] longObjects = list.toArray(new Long[list.size()]);		
			setFiltroOR(Arrays.asList(longObjects), _campo, _orIsNull);
		}
	}
	
	public abstract String getSQLCampo(String _enumCampo);
	public abstract String getSQLOrderBy();
	public abstract void setFiltro(final PreparedStatement _ps) throws SQLException;
	
}
