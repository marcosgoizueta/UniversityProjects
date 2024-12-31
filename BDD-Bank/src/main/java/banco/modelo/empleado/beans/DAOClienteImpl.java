package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOClienteImpl implements DAOCliente {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteImpl.class);
	
	private Connection conexion;
	
	public DAOClienteImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {

		logger.info("Intenta recuperar el cliente con documento de tipo {} y nro {}.", tipoDoc, nroDoc);
		/**
		 *  Recuperar el cliente que tenga un documento que se corresponda con los parámetros recibidos.  
		 *	Deberá generar o propagar una excepción si no existe dicho cliente o hay un error de conexión.		
		 */
		
		String tipoDoc_s = "'"+tipoDoc+"'";
		//Creo string para la sentencia
		String sql = "SELECT * FROM cliente WHERE tipo_doc="+tipoDoc_s+"AND nro_doc="+nroDoc+";";
		logger.debug("SQL: {}", sql);
		//Cliente que sera devuelto
		ClienteBean clToReturn = new ClienteBeanImpl();
		
		try {
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			if(rs.next()) {
				logger.debug("Se recuperó el cliente con tipoDoc {} y nroDoc {}",rs.getString("tipo_doc"),rs.getInt("nro_doc"));
				clToReturn.setNroCliente(rs.getInt("nro_cliente"));
				clToReturn.setApellido(rs.getString("apellido"));
				clToReturn.setNombre(rs.getString("nombre"));
				clToReturn.setTipoDocumento(rs.getString("tipo_doc"));
				clToReturn.setNroDocumento(rs.getInt("nro_doc"));
				clToReturn.setDireccion(rs.getString("direccion"));
				clToReturn.setTelefono(rs.getString("telefono"));
				clToReturn.setFechaNacimiento(rs.getDate("fecha_nac"));	
			}
			else {
				throw new Exception("No existe cliente para el tipo y nro. de documento seleccionados.");
			}
			
			return clToReturn;
			
		}
		catch(SQLException ex){
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

	@Override
	public ClienteBean recuperarCliente(Integer nroCliente) throws Exception {
		logger.info("recupera el cliente por nro de cliente.");
		
		/**
		 *  Recuperar el cliente que tenga un número de cliente de acuerdo al parámetro recibido.  
		 *	Deberá generar o propagar una excepción si no existe dicho cliente o hay un error de conexión.		
		 */
		//Creo string para la sentencia
		String sql = "SELECT * FROM cliente WHERE nro_cliente="+nroCliente+";";
		logger.debug("SQL: {}", sql);
		//Cliente que será devuelto
		ClienteBean cliente = new ClienteBeanImpl();
		try {
			Statement select = conexion.createStatement();
			ResultSet rs= select.executeQuery(sql);
			 
			if(rs.next()) {
				logger.debug("Se recuperó el cliente con nro. de cliente {}",rs.getInt("nro_cliente"));
				cliente.setNroCliente(rs.getInt("nro_cliente"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setTipoDocumento(rs.getString("tipo_doc"));
				cliente.setNroDocumento(rs.getInt("nro_doc"));
				cliente.setDireccion(rs.getString("direccion"));
				cliente.setTelefono(rs.getString("telefono"));
				cliente.setFechaNacimiento(rs.getDate("fecha_nac"));	
			}
			else {
				throw new Exception("No existe cliente para el nro. de cliente seleccionado.");
			}
			
			return cliente;
		}
		catch(SQLException ex){
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}

	}

}
