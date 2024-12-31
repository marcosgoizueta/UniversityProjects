package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOClienteMorosoImpl implements DAOClienteMoroso {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteMorosoImpl.class);
	
	private Connection conexion;
	
	public DAOClienteMorosoImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception {
		logger.info("Busca los clientes morosos.");
		/**
		 * Deberá recuperar un listado de clientes morosos los cuales consisten de un bean ClienteMorosoBeanImpl
		 * deberá indicar para dicho cliente cual es el prestamo sobre el que está moroso y la cantidad de cuotas que 
		 * tiene atrasadas. En todos los casos deberá generar excepciones que será capturadas por el controlador
		 * si hay algún error que necesita ser informado al usuario. 
		 */
		
		String sql = "SELECT c.nro_cliente,c.tipo_doc,c.nro_doc,c.nombre,c.apellido, p.nro_prestamo,p.monto,p.cant_meses,p.valor_cuota,tp.cuotas_atrasadas\r\n"
				+ "FROM cliente AS c JOIN prestamo AS p ON c.nro_cliente =  p.nro_cliente\r\n"
				+ "JOIN (SELECT nro_prestamo, COUNT(nro_prestamo) AS cuotas_atrasadas FROM pago WHERE fecha_pago IS NULL AND fecha_venc < CURDATE() GROUP BY nro_prestamo) AS tp ON p.nro_prestamo = tp.nro_prestamo\r\n"
				+ "HAVING tp.cuotas_atrasadas > 1;";
		
		logger.debug(sql);
		
		ArrayList<ClienteMorosoBean> morosos = new ArrayList<ClienteMorosoBean>();
		
		try{ 
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			
			 while (rs.next()) {
				logger.debug("Se recuper� el cliente con nro {} y prestamo {}", rs.getInt("nro_cliente"), rs.getInt("nro_prestamo"));
				
				ClienteBean cliente = new ClienteBeanImpl();
				cliente.setNroCliente(rs.getInt("nro_cliente"));
				cliente.setTipoDocumento("tipo_doc");
				cliente.setNroDocumento(rs.getInt("nro_doc"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setApellido(rs.getString("apellido"));
				
				PrestamoBean prestamo = new PrestamoBeanImpl();
				prestamo.setNroCliente(rs.getInt("nro_cliente"));
				prestamo.setNroPrestamo(rs.getInt("nro_prestamo"));
				prestamo.setMonto(rs.getDouble("monto"));
				prestamo.setCantidadMeses(rs.getInt("cant_meses"));
				prestamo.setValorCuota(rs.getDouble("valor_cuota"));
				
				ClienteMorosoBean mr = new ClienteMorosoBeanImpl();
				mr.setCliente(cliente);
				mr.setPrestamo(prestamo);
				mr.setCantidadCuotasAtrasadas(rs.getInt("cuotas_atrasadas"));
				morosos.add(mr);			
			  }		
			 
			return morosos;		
				
		}
		catch (SQLException ex)
		{			
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

}

