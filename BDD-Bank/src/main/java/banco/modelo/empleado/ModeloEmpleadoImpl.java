package banco.modelo.empleado;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import banco.modelo.ModeloImpl;
import banco.modelo.empleado.beans.ClienteBean;
import banco.modelo.empleado.beans.ClienteMorosoBean;
import banco.modelo.empleado.beans.DAOCliente;
import banco.modelo.empleado.beans.DAOClienteImpl;
import banco.modelo.empleado.beans.DAOClienteMoroso;
import banco.modelo.empleado.beans.DAOClienteMorosoImpl;
import banco.modelo.empleado.beans.DAOEmpleado;
import banco.modelo.empleado.beans.DAOEmpleadoImpl;
import banco.modelo.empleado.beans.DAOPago;
import banco.modelo.empleado.beans.DAOPagoImpl;
import banco.modelo.empleado.beans.DAOPrestamo;
import banco.modelo.empleado.beans.DAOPrestamoImpl;
import banco.modelo.empleado.beans.EmpleadoBean;
import banco.modelo.empleado.beans.PagoBean;
import banco.modelo.empleado.beans.PrestamoBean;

public class ModeloEmpleadoImpl extends ModeloImpl implements ModeloEmpleado {

	private static Logger logger = LoggerFactory.getLogger(ModeloEmpleadoImpl.class);	

	// Indica el usuario actualmente logueado. Null corresponde que todavia no se ha autenticado
	private Integer legajo = null;
	
	public ModeloEmpleadoImpl() {
		logger.debug("Se crea el modelo Empleado.");
	}
	

	@Override
	public boolean autenticarUsuarioAplicacion(String legajo, String password) throws Exception {
		logger.info("Se intenta autenticar el legajo {} con password {}", legajo, password);
		/** 
		 * Código que autentica que exista un legajo de empleado y que el password corresponda a ese legajo
		 * (el password guardado en la BD está en MD5) 
		 * En caso exitoso deberá registrar el legajo en la propiedad legajo y retornar true.
		 * Si la autenticación no es exitosa porque el legajo no es válido o el password es incorrecto
		 * deberá retornar falso y si hubo algún otro error deberá producir una excepción.
		 */
		String sql = "SELECT legajo FROM empleado WHERE legajo = "+legajo+" AND password = md5('"+password+"');";
		logger.debug("SQL: {}", sql);
		
		Integer legajoInt = null;	
		boolean autenticado = false;
		try {
			legajoInt = Integer.valueOf(legajo.trim());
        }
        catch (Exception ex) {
        	throw new Exception("Se esperaba que el legajo sea un valor entero.");
        }
		
		try{ 
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			
			 if (rs.next()) {
				logger.debug("Se recuper� el empleado con legajo {}", rs.getInt("legajo"));
				autenticado = true;
				this.legajo = rs.getInt("legajo");
			  }
			 else {
				 throw new Exception("El legajo o password ingresado es incorrecto.");
			 }
			return autenticado;		
		}
		catch (SQLException ex)
		{			
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}
	
	@Override
	public EmpleadoBean obtenerEmpleadoLogueado() throws Exception {
		logger.info("Solicita al DAO un empleado con legajo {}", this.legajo);
		if (this.legajo == null) {
			logger.info("No hay un empleado logueado.");
			throw new Exception("No hay un empleado logueado. La sesión terminó.");
		}
		
		DAOEmpleado dao = new DAOEmpleadoImpl(this.conexion);
		return dao.recuperarEmpleado(this.legajo);
	}	
	
	@Override
	public ArrayList<String> obtenerTiposDocumento() throws Exception {
		logger.info("Intenta recuperar los tipos de documentos.");
		String sql = "SELECT DISTINCT tipo_doc FROM cliente;";
		logger.debug("SQL: {}", sql);
		
		ArrayList<String> tipos = new ArrayList<String>();
		try {
			Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			 
			 while(rs.next()) {
				 logger.debug("Se recuper� el item con tipo de documento {}", rs.getString("tipo_doc"));
				 tipos.add(rs.getString("tipo_doc"));
			 }
			 return tipos;
		}
		catch(SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}	

	@Override
	public double obtenerTasa(double monto, int cantidadMeses) throws Exception {

		logger.info("Busca la tasa correspondiente a el monto {} con una cantidad de meses {}", monto, cantidadMeses);

		/** 
		 * Debe buscar la tasa correspondiente según el monto y la cantidadMeses. 
		 * Deberia propagar una excepción si hay algún error de conexión o 
		 * no encuentra el monto dentro del [monto_inf,monto_sup] y la cantidadMeses.
		 */
		
		String sql = "SELECT tasa FROM tasa_prestamo WHERE periodo = "+cantidadMeses+" AND "+monto+" > (SELECT MIN(monto_inf) FROM tasa_prestamo) AND "+monto+" < (SELECT MAX(monto_sup) FROM tasa_prestamo);";
		logger.debug("SQL: {}", sql);
		
		double tasa;
		
		try{ 
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			 //Si encontro tasa, la devuelvo, de lo contrario indico que el monto y/o periodo es invalido.
			if(rs.next()) {	
				logger.debug("Se recuper� la tasa {}", rs.getDouble("tasa"));
				tasa = rs.getDouble("tasa");
			}
			else {
				throw new Exception("No hay tasa para el monto y período ingresados.");
			}		  
			return tasa;		
		}
		catch (SQLException ex)
		{			
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

	@Override
	public double obtenerInteres(double monto, double tasa, int cantidadMeses) {
		return (monto * tasa * cantidadMeses) / 1200;
	}


	@Override
	public double obtenerValorCuota(double monto, double interes, int cantidadMeses) {
		return (monto + interes) / cantidadMeses;
	}
		

	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {
		DAOCliente dao = new DAOClienteImpl(this.conexion);
		return dao.recuperarCliente(tipoDoc, nroDoc);
	}


	@Override
	public ArrayList<Integer> obtenerCantidadMeses(double monto) throws Exception {
		logger.info("recupera los períodos (cantidad de meses) según el monto {} para el prestamo.", monto);

		/** 
		 * Debe buscar los períodos disponibles según el monto. 
		 * Deberia propagar una excepción si hay algún error de conexión o 
		 * no encuentra el monto dentro del [monto_inf,monto_sup].
		 */
		
		String sql = "SELECT * FROM tasa_prestamo WHERE "+monto+" > monto_inf AND "+monto+" < monto_sup;";
		logger.debug("SQL: {}", sql);
		
		ArrayList<Integer> cantMeses = new ArrayList<Integer>();
		
		try{ 
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			 //Si encontro algun periodo, lo guardo en la lista, de lo contrario indico que el monto es invalido.
			if(rs.next()) {	
				logger.debug("Se recuper� el periodo con cant. meses: {}", rs.getInt("periodo"));
				cantMeses.add(rs.getInt("periodo"));
			}
			else {
				throw new Exception("No hay periodos para el monto ingresado.");
			}
			
			 while (rs.next()) {
				logger.debug("Se recuper� el periodo con cant. meses: {}", rs.getInt("periodo"));
				cantMeses.add(rs.getInt("periodo"));			
			  }			  
			return cantMeses;		
				
		}
		catch (SQLException ex)
		{			
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
	}

	@Override	
	public Integer prestamoVigente(int nroCliente) throws Exception 
	{
		logger.info("Verifica si el cliente {} tiene algun prestamo que tienen cuotas por pagar.", nroCliente);
		
		String sql = "SELECT p.nro_prestamo FROM prestamo AS p JOIN (SELECT nro_prestamo,nro_pago,fecha_pago,fecha_venc FROM pago) AS pa ON p.nro_prestamo = pa.nro_prestamo WHERE p.nro_cliente="+nroCliente+" AND pa.fecha_pago IS NULL;";
		logger.debug("SQL: {}", sql);
		Integer nroPrestamo = null;
		try {
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			if(rs.next()) {
				logger.debug("Se recuper� el prestamo con nro. de prestamo {}", rs.getInt("nro_prestamo"));
				nroPrestamo = rs.getInt("nro_prestamo");
			}
		}
		catch(SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
		return nroPrestamo;
	}


	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {
		logger.info("Crea un nuevo prestamo.");
		
		if (this.legajo == null) {
			throw new Exception("No hay un empleado registrado en el sistema que se haga responsable por este prestamo.");
		}
		else 
		{
			logger.info("Actualiza el prestamo con el legajo {}",this.legajo);
			prestamo.setLegajo(this.legajo);
			
			DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
			dao.crearPrestamo(prestamo);
		}
	}
	
	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
		logger.info("Busca el prestamo número {}", nroPrestamo);
		
		DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
		return dao.recuperarPrestamo(nroPrestamo);
	}
	
	@Override
	public ArrayList<PagoBean> recuperarPagos(Integer prestamo) throws Exception {
		logger.info("Solicita la busqueda de pagos al modelo sobre el prestamo {}.", prestamo);
		
		DAOPago dao = new DAOPagoImpl(this.conexion);		
		return dao.recuperarPagos(prestamo);
	}
	

	@Override
	public void pagarCuotas(String p_tipo, int p_dni, int nroPrestamo, List<Integer> cuotasAPagar) throws Exception {
		
		// Valida que sea un cliente que exista sino genera una excepción
		ClienteBean c = this.recuperarCliente(p_tipo.trim(), p_dni);

		// Valida el prestamo
		if (nroPrestamo != this.prestamoVigente(c.getNroCliente())) {
			throw new Exception ("El nro del prestamo no coincide con un prestamo vigente del cliente");
		}

		if (cuotasAPagar.size() == 0) {
			throw new Exception ("Debe seleccionar al menos una cuota a pagar.");
		}
		
		DAOPago dao = new DAOPagoImpl(this.conexion);
		dao.registrarPagos(nroPrestamo, cuotasAPagar);		
	}


	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception {
		logger.info("Modelo solicita al DAO que busque los clientes morosos");
		DAOClienteMoroso dao = new DAOClienteMorosoImpl(this.conexion);
		return dao.recuperarClientesMorosos();	
	}
	

	
}
