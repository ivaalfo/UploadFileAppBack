package eus.martico.fileTransfer.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eus.martico.fileTransfer.dao.BaseDAOCons;
//import eus.martico.fileTransfer.dao.DAOFiltro;
//import eus.martico.fileTransfer.model.vo.PaginacionVO;

/**
 * 
 * @author Martico S.L.
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public class SqlUtils {
	
	public static void close(Connection _conn) {
		try {
			if(_conn != null){
				_conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(PreparedStatement _ps) {
		try {
			if(_ps != null){
				_ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet _rs) {
		try {
			if(_rs != null){
				_rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(PreparedStatement _ps, ResultSet _rs) {
		close(_rs);
		close(_ps);
	}

	/**
	 * Devuelve una cadena de la forma ?, ?, ?, etc.. con tyantos ? como el caracter
	 * coma , se encuentre en la _cadenaCampos Sirve para formar SQLs
	 * 
	 * @param _cadenaCampos
	 * @return
	 */
	public static String sqlInterrogaciones(final String _cadenaCampos) {
		if(_cadenaCampos != null && _cadenaCampos.trim().length() > 0){
			int n = StringUtils.countMatches(_cadenaCampos, ",");
			if(n >= 0){
				StringBuilder result = new StringBuilder();
				boolean first = true;
				for(int i = 0; i <= n; i++){
					if(!first){
						result.append(", ");
					} else {
						first = false;
					}
					result.append("?");
				}
				return result.toString();
			}
			return null;
		} else {
			return null;
		}
	}

	public static StringBuilder generarSqlInsert(String tabla, List<String> campos) {
		StringBuilder sqlInsert = new StringBuilder();
		sqlInsert.append("INSERT INTO ");
		sqlInsert.append(tabla);
		sqlInsert.append(" (");
		for(int i = 0; i < campos.size(); i++){
			sqlInsert.append(campos.get(i));
			if(i < campos.size() - 1){
				sqlInsert.append(", ");
			}
		}
		sqlInsert.append(")");
		sqlInsert.append(" VALUES (");
		sqlInsert.append(SqlUtils.sqlInterrogaciones(sqlInsert.toString()));
		sqlInsert.append(")");
		return sqlInsert;
	}

	public static StringBuilder generarSqlUpdate(String tabla, List<String> campos, String... ids) {
		return generarSqlUpdate(tabla, campos, SqlOperadorEnum.AND, ids);
	}
	
	public static StringBuilder generarSqlUpdate(String tabla, List<String> campos, SqlOperadorEnum _operador, String... ids) {
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE ");
		sqlUpdate.append(tabla);
		sqlUpdate.append(" SET ");
		for(int i = 0; i < campos.size(); i++){
			sqlUpdate.append(campos.get(i));
			sqlUpdate.append(" = ?");
			if(i < campos.size() - 1){
				sqlUpdate.append(", ");
			}
		}
		sqlUpdate.append(" WHERE ");
		int nid = 0;
		for(String id: ids){
			if(nid > 0){
				sqlUpdate.append(" ");
				sqlUpdate.append(_operador.getValor());
				sqlUpdate.append(" ");
			}
			sqlUpdate.append(id);
			sqlUpdate.append(" = ?");
			++nid;
		}
		return sqlUpdate;
	}
	
	public static StringBuilder generarSqlDelete(String tabla, String... ids) {
		return generarSqlDelete(tabla, SqlOperadorEnum.AND, ids);
	}
	
	public static StringBuilder generarSqlDelete(String tabla, SqlOperadorEnum _operador, String... ids) {
		StringBuilder sqlDelete = new StringBuilder();
		sqlDelete.append("DELETE FROM ");
		sqlDelete.append(tabla);
		sqlDelete.append(" WHERE ");
		int nid = 0;
		for(String id: ids){
			if(nid > 0){
				sqlDelete.append(" ");
				sqlDelete.append(_operador.getValor());
				sqlDelete.append(" ");
			}
			sqlDelete.append(id);
			sqlDelete.append(" = ?");
			++nid;
		}
		return sqlDelete;
	}

	public static StringBuilder generarSecuencia(String sequencia) {
		StringBuilder sqlNextSequence = new StringBuilder();
		sqlNextSequence.append("SELECT nextval('");
		sqlNextSequence.append(sequencia);
		sqlNextSequence.append("')");
		return sqlNextSequence;
	}

	public static StringBuilder generarSecuenciaForAS400(String sequencia) {
		StringBuilder sqlNextSequence = new StringBuilder();
		sqlNextSequence.append("SELECT NEXT VALUE FOR ");
		sqlNextSequence.append(sequencia);
		sqlNextSequence.append(" FROM SYSIBM.SYSDUMMY1");
		return sqlNextSequence;
	}

	public static StringBuilder generarSelectAll(String tabla) {
		StringBuilder sqlNextSequence = new StringBuilder();
		sqlNextSequence.append("SELECT * FROM ");
		sqlNextSequence.append(tabla);
		return sqlNextSequence;
	}
	
	public static StringBuilder generarSelectAllActive(String tabla) {
		StringBuilder sqlNextSequence = new StringBuilder();
		sqlNextSequence.append("SELECT * FROM ");
		sqlNextSequence.append(tabla);
		sqlNextSequence.append(" WHERE ");
		sqlNextSequence.append(BaseDAOCons.BD_FECHABAJA);
		sqlNextSequence.append(" IS NULL");
		return sqlNextSequence;
	}	

	public static StringBuilder generarSelectById(String tabla, String... ids) {
		return generarSelectById(tabla, SqlOperadorEnum.AND, ids);
	}
	
	public static StringBuilder generarSelectById(String tabla, List<String> campos, String... ids) {
		return generarSelectById(tabla, campos, SqlOperadorEnum.AND, ids);
	}
	
	public static StringBuilder generarSelectById(String tabla, SqlOperadorEnum _operador, String... ids) {
		return generarSelectById(tabla, null, _operador, ids);
	}
	
	public static StringBuilder generarSelectById(String tabla, List<String> campos, SqlOperadorEnum _operador, String... ids) {
		StringBuilder sqlSelectId = new StringBuilder();
		sqlSelectId.append("SELECT ");
		if(campos == null){
			sqlSelectId.append("*");
		} else {
			boolean primero = true;
			for(String campo: campos){
				if(!primero){
					sqlSelectId.append(",");
				}
				sqlSelectId.append(campo);
				if(primero){
					primero = false;
				}
			}
		}
		sqlSelectId.append(" FROM ");
		sqlSelectId.append(tabla);
		sqlSelectId.append(" WHERE (");
		int nid = 0;
		for(String id: ids){
			if(nid > 0){
				sqlSelectId.append(" ");
				sqlSelectId.append(_operador.getValor());
				sqlSelectId.append(" ");
			}
			sqlSelectId.append(id);
			sqlSelectId.append(" = ?");
			++nid;
		}
		sqlSelectId.append(")");
		return sqlSelectId;
	}
	
	
	public static StringBuilder generarBajaLogica(String tabla,String campoNi) {
		return generarSqlUpdate(tabla, Arrays.asList(BaseDAOCons.BD_FECHABAJA, BaseDAOCons.BD_USER_BAJA), campoNi);
	}
	
	public static StringBuilder modificacionMultiple(String tabla,String campoUpdate, String campoWhere,List<Long> ids) {
		StringBuilder sqlUpdate = new StringBuilder();
		sqlUpdate.append("UPDATE ");
		sqlUpdate.append(tabla);
		sqlUpdate.append(" SET ");
		sqlUpdate.append(campoUpdate);
		sqlUpdate.append(" = ?");
		sqlUpdate.append(" WHERE ");
		sqlUpdate.append(campoWhere);
		sqlUpdate.append(" in ");
		sqlUpdate.append(" (");
		for(int i = 0; i < ids.size(); i++){
			sqlUpdate.append("?");
			if(i < ids.size() - 1){
				sqlUpdate.append(", ");
			}
		}
		sqlUpdate.append(" ) ");
		return sqlUpdate;
	}

    /*************** GETTERS *******************************************************************************************/
    public static Byte getBooleanAsByte(Boolean _value) throws SQLException {
		if(_value == null){
			return null;
		} else if(_value.booleanValue()){
			return 1;
		} else {
			return 0;
		}
    }

    public static String getStringTrim(ResultSet _rs, String _campo) throws SQLException {
    	String result = _rs.getString(_campo);
    	if(result == null){
    		return null;
    	} else {
    		return result.trim();
    	}
    }
    
    public static Character getCharAS400(ResultSet _rs, String _campo) throws SQLException {
    	String result = _rs.getString(_campo);
    	if(result == null){
    		return null;
    	} else {
    		return result.charAt(0);
    	}
    }
    
    public static Integer getIntegerFromRs(ResultSet _rs, String _campo) throws SQLException {
    	int result = _rs.getInt(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return result;
    	}
    }
    
    public static Short getShortFromRs(ResultSet _rs, String _campo) throws SQLException {
    	short result = _rs.getShort(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return result;
    	}
    }
    
    public static Long getLongFromRs(ResultSet _rs, String _campo) throws SQLException {
    	long result = _rs.getLong(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return result;
    	}
    }
    
    public static BigDecimal getBigDecimalFromRs(ResultSet _rs, String _campo) throws SQLException {
    	double result = _rs.getDouble(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return BigDecimal.valueOf(result);
    	}
    }
    
    public static Byte getByteFromRs(ResultSet _rs, String _campo) throws SQLException {
    	byte result = _rs.getByte(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return result;
    	}
    }
    
    public static Boolean getBooleanFromRs(ResultSet _rs, String _campo) throws SQLException {
    	boolean result = _rs.getBoolean(_campo);
    	if(_rs.wasNull()){
    		return null;
    	} else {
    		return result;
    	}
    }
    
    //Retorna true si el valor es 'S' o '1'
    public static Boolean getBooleanFromRsSN(ResultSet _rs, String _campo) throws SQLException {
    	String result = _rs.getString(_campo);
    	if(result != null){
    		if(result.trim().equals("S")){
    			return true;
    		} else if(result.trim().equals("1")){
    			return true;
    		} else {
    			return false;
    		}
    	} else {
    		return null;
    	}
    }
    
    public static LocalDateTime getLocalDateTimeFromRs(final ResultSet _rs, final String _campo) throws SQLException {
    	return getLocalDateTime(_rs.getTimestamp(_campo));
    }
    public static LocalDateTime getLocalDateTime(final Timestamp _value) throws SQLException {
    	return (_value == null ? null : _value.toLocalDateTime());
    }
    public static LocalDateTime getLocalDateTimeFromRsAS400(final ResultSet _rs, final String _campo) throws SQLException {
    	return getLocalDateTimeAS400(_rs.getTimestamp(_campo));
    }
    
    public static LocalDateTime getLocalDateTimeAS400(final Timestamp _value) throws SQLException {
    	if(_value == null){
    		return null;
    	} else if(_value.toLocalDateTime().getYear() < 1000){
    		return null;
    	} else {
    		return _value.toLocalDateTime();
    	}
    }
    
    public static LocalDate getLocalDateFromRs(final ResultSet _rs, final String _campo) throws SQLException {
    	return getLocalDate(_rs.getTimestamp(_campo));
    }
    public static LocalDate getLocalDate(final Timestamp _value) throws SQLException {
    	return (_value == null ? null : _value.toLocalDateTime().toLocalDate());
    }



    /*************** SETTERS *******************************************************************************************/
    public static void setIntegerToPs(PreparedStatement _ps, int _i, Integer _value) throws SQLException {
		if(_value == null){
			_ps.setNull(_i, Types.INTEGER);
		} else {
			_ps.setInt(_i, _value);
		}
    }
    public static void setShortToPs(PreparedStatement _ps, int _i, Short _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.SMALLINT);
    	} else {
			_ps.setInt(_i, _value);
    	}
    }
    public static void setLongToPs(PreparedStatement _ps, int _i, Long _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.NUMERIC);
    	} else {
			_ps.setLong(_i, _value);
    	}
    }
    public static void setByteToPs(PreparedStatement _ps, int _i, Byte _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.TINYINT);
    	} else {
			_ps.setInt(_i, _value);
    	}
    }
    public static void setByteToPsFromBoolean(PreparedStatement _ps, int _i, Boolean _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.TINYINT);
    	} else if(_value.booleanValue()){
			_ps.setByte(_i, (byte)1);
    	} else {
			_ps.setByte(_i, (byte)0);
    	}
    }
    public static void setLocalDateTimeToPs(PreparedStatement _ps, int _i, LocalDateTime _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.TIMESTAMP);
    	} else {
			_ps.setTimestamp(_i, Timestamp.valueOf(_value));
    	}
    }    
    public static void setLocalDateToPs(PreparedStatement _ps, int _i, LocalDate _value) throws SQLException {
    	if(_value == null){
			_ps.setNull(_i, Types.DATE);
    	} else {
			_ps.setDate(_i, java.sql.Date.valueOf(_value));
    	}
    }
    public static void setSoNToRsFromBoolean(PreparedStatement _ps, int _i, Boolean _value) throws SQLException {
    	if(_value == null){
			_ps.setString(_i, null);
    	} else if(_value.booleanValue()){
			_ps.setString(_i, "S");
    	} else {
			_ps.setString(_i, "N");
    	}
    }
       
    public static Date localDateTimeToDate(LocalDateTime dateToConvert){
		if(dateToConvert != null){
		    ZonedDateTime zdt = dateToConvert.atZone(ZoneId.systemDefault());
	        java.util.Date outputUtilDate = Date.from(zdt.toInstant());
	        return new Date(outputUtilDate.getTime());
		} else {
			return null;
		}
	}

}
