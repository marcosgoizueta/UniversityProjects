package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOPagoImpl implements DAOPago {

	private static Logger logger = LoggerFactory.getLogger(DAOPagoImpl.class);
	
	private Connection conexion;
	
	public DAOPagoImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public ArrayList<PagoBean> recuperarPagos(int nroPrestamo) throws Exception {
		logger.info("Inicia la recuperacion de los pagos del prestamo {}", nroPrestamo);
		
		String sql = "SELECT * FROM pago WHERE nro_prestamo="+nroPrestamo+";";
		
		logger.debug("SQL: {}", sql);
		
		ArrayList<PagoBean> lista = new ArrayList<PagoBean>();
		
		try {
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			
			while(rs.next()) {
				logger.debug("Se recuper� el item con nro. de prestamo {}", rs.getInt("nro_prestamo"));
				PagoBean p = new PagoBeanImpl(); 
				p.setNroPrestamo(rs.getInt("nro_prestamo"));
				p.setNroPago(rs.getInt("nro_pago"));
				p.setFechaVencimiento(rs.getDate("fecha_venc"));
				p.setFechaPago(rs.getDate("fecha_pago"));
				lista.add(p);
			}
			return lista;
		}
		catch(SQLException ex){
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

	@Override
	public void registrarPagos(int nroPrestamo, List<Integer> cuotasAPagar)  throws Exception {

		logger.info("Inicia el pago de las {} cuotas del prestamo {}", cuotasAPagar.size(), nroPrestamo);

		/**
		 * Registra los pagos de cuotas definidos en cuotasAPagar.
		 * nroPrestamo asume que está validado
		 * cuotasAPagar Debe verificar que las cuotas a pagar no estén pagas (fecha_pago = NULL)
		 * @throws Exception Si hubo error en la conexión
		 */		
		String sql = "UPDATE pago SET fecha_pago = CURDATE() WHERE fecha_pago IS NULL AND nro_prestamo = ? AND nro_pago = ? ;";
		//Por cada nro de cuota a pagar, mando una sentencia para actualizarla (pagarla)
		try { 
			for (Integer nroPago : cuotasAPagar) {
				logger.debug("UPDATE pago SET fecha_pago = CURDATE() WHERE fecha_pago IS NULL AND nro_prestamo = {} AND nro_pago = {} ;",nroPrestamo,nroPago);
				PreparedStatement update = conexion.prepareStatement(sql);
			    update.setInt(1, nroPrestamo);
			    update.setInt(2, nroPago);
			 	update.executeUpdate();
			}
		 }
		 catch (SQLException ex){
			 logger.error("SQLException: " + ex.getMessage());
			 logger.error("SQLState: " + ex.getSQLState());
			 logger.error("VendorError: " + ex.getErrorCode());
			 if (ex.getErrorCode()==1451) // Cannot delete or update a parent row: a foreign key constraint fails
					throw new Exception("No se puede actualizar el pago por que esta referenciado en otra tabla'");
				else throw new Exception("Error inesperado al borrar la batalla en la B.D.");
		 }	
		

	}
}
