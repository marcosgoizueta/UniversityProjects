package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOPrestamoImpl implements DAOPrestamo {

	private static Logger logger = LoggerFactory.getLogger(DAOPrestamoImpl.class);
	
	private Connection conexion;
	
	public DAOPrestamoImpl(Connection c) {
		this.conexion = c;
	}
	
	
	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {

		logger.info("Creación o actualizacion del prestamo.");
		logger.debug("meses : {}", prestamo.getCantidadMeses());
		logger.debug("monto : {}", prestamo.getMonto());
		logger.debug("tasa : {}", prestamo.getTasaInteres());
		logger.debug("interes : {}", prestamo.getInteres());
		logger.debug("cuota : {}", prestamo.getValorCuota());
		logger.debug("legajo : {}", prestamo.getLegajo());
		logger.debug("cliente : {}", prestamo.getNroCliente());
		
		/**   
		 * Crear un Prestamo segun el PrestamoBean prestamo. 
		 *    
		 * 
		 * @throws Exception deberá propagar la excepción si ocurre alguna. Puede capturarla para loguear los errores, ej.
		 *				logger.error("SQLException: " + ex.getMessage());
		 * 				logger.error("SQLState: " + ex.getSQLState());
		 *				logger.error("VendorError: " + ex.getErrorCode());
		 *		   pero luego deberá propagarla para que se encargue el controlador. 
		 */

		String sql = "INSERT INTO prestamo(fecha,cant_meses,monto,tasa_interes,interes,valor_cuota,legajo,nro_cliente) VALUES (CURDATE(),?,?,?,?,?,?,?) ;";
		logger.debug(sql);
		
		try 
		{  PreparedStatement insert = conexion.prepareStatement(sql); 
		   insert.setInt(1, prestamo.getCantidadMeses());
		   insert.setDouble(2, prestamo.getMonto());
		   insert.setDouble(3,prestamo.getTasaInteres());
		   insert.setDouble(4, prestamo.getInteres());
		   insert.setDouble(5, prestamo.getValorCuota());
		   insert.setInt(6, prestamo.getLegajo());
		   insert.setInt(7, prestamo.getNroCliente());
		   insert.executeUpdate();
		}
		catch (SQLException ex)
		{
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			new Exception("Error inesperado al insertar la batalla en la B.D.");
		}
		
	}

	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
		
		logger.info("Intenta recuperar el prestamo nro {}.", nroPrestamo);
		
		/**
		 * Obtiene el prestamo según el id nroPrestamo
		 * @param nroPrestamo
		 * @return Un prestamo que corresponde a ese id o null
		 * @throws Exception si hubo algun problema de conexión
		 */		
		//Creo string para la sentencia
		String sql = "SELECT * FROM prestamo WHERE nro_prestamo="+nroPrestamo+";";
		logger.debug("SQL: {}", sql);
		
		PrestamoBean prestamo = null;
		try {
			Statement select = conexion.createStatement();
			ResultSet rs= select.executeQuery(sql);
			
			if(rs.next()) {
				logger.debug("Se recuper� el prestamo con nro {}", rs.getInt("nro_prestamo"));
				prestamo = new PrestamoBeanImpl();
				prestamo.setNroPrestamo(rs.getInt("nro_prestamo"));
				prestamo.setFecha(rs.getDate("fecha"));
				prestamo.setCantidadMeses(rs.getInt("cant_meses"));
				prestamo.setMonto(rs.getDouble("monto"));
				prestamo.setTasaInteres(rs.getDouble("tasa_interes"));
				prestamo.setInteres(rs.getDouble("interes"));
				prestamo.setValorCuota(rs.getDouble("valor_cuota"));
				prestamo.setLegajo(rs.getInt("legajo"));
				prestamo.setNroCliente(rs.getInt("nro_cliente"));
			}
			return prestamo;
		}
		catch(SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

}
