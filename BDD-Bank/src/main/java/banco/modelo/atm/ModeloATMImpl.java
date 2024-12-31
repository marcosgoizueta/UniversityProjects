package banco.modelo.atm;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.modelo.ModeloImpl;
import banco.utils.Fechas;


public class ModeloATMImpl extends ModeloImpl implements ModeloATM {
	
	private static Logger logger = LoggerFactory.getLogger(ModeloATMImpl.class);	

	private String tarjeta = null;   // mantiene la tarjeta del cliente actual
	private Integer codigoATM = null;
	
	/*
	 * La información del cajero ATM se recupera del archivo que se encuentra definido en ModeloATM.CONFIG
	 */
	public ModeloATMImpl() {
		logger.debug("Se crea el modelo ATM.");

		logger.debug("Recuperación de la información sobre el cajero");
		
		Properties prop = new Properties();
		try (FileInputStream file = new FileInputStream(ModeloATM.CONFIG))
		{
			logger.debug("Se intenta leer el archivo de propiedades {}",ModeloATM.CONFIG);
			prop.load(file);

			codigoATM = Integer.valueOf(prop.getProperty("atm.codigo.cajero"));

			logger.debug("Código cajero ATM: {}", codigoATM);
		}
		catch(Exception ex)
		{
        	logger.error("Se produjo un error al recuperar el archivo de propiedades {}.",ModeloATM.CONFIG); 
		}
		return;
	}

	@Override
	public boolean autenticarUsuarioAplicacion(String tarjeta, String pin) throws Exception	{
		
		logger.info("Se intenta autenticar la tarjeta {} con pin {}", tarjeta, pin);
		boolean encontre = false;

		ResultSet rs = this.consulta("SELECT * FROM tarjeta WHERE tarjeta.pin = MD5("+pin+") AND tarjeta.nro_tarjeta = "+tarjeta+";");
			try {
				if(rs.next()) {
					this.tarjeta = tarjeta;
					encontre = true;
				}else {
					throw new  Exception("No se encontró tarjeta.");
				}
			 
			}catch (java.sql.SQLException ex) {
				logger.error("SQLException: " + ex.getMessage());
				logger.error("SQLState: " + ex.getSQLState());
				logger.error("VendorError: " + ex.getErrorCode());
				throw new Exception("Error inesperado al verificar tarjeta.");
			}
			return encontre;
		}

	
	
	@Override
	public Double obtenerSaldo() throws Exception
	{
		logger.info("Se intenta obtener el saldo de cliente {}", 3);

		if (this.tarjeta == null ) {
			throw new Exception("El cliente no ingresó la tarjeta");
		}
		ResultSet rs = this.consulta("SELECT ca.saldo,ta.nro_tarjeta FROM (trans_cajas_ahorro as ca JOIN tarjeta as ta ON (ca.nro_ca = ta.nro_ca)) WHERE ta.nro_tarjeta = "+tarjeta+" ORDER BY ca.fecha DESC LIMIT 1;");
		
		Double saldo = null;
		try {
			if (rs.next()) {
				logger.debug("Encontre el dato satisfactotriamente, con nro_tarjeta {}",rs.getInt("nro_tarjeta"));
				saldo = rs.getDouble("saldo");
			
			}
		}catch (java.sql.SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar el saldo.");
		}
		
		return parseMonto(String.valueOf(saldo));
		
	}

	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos() throws Exception {
		return this.cargarUltimosMovimientos(ModeloATM.ULTIMOS_MOVIMIENTOS_CANTIDAD);
	}	
	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos(int cantidad) throws Exception
	{
		logger.info("Busca las ultimas {} transacciones en la BD de la tarjeta",cantidad);			
		ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<TransaccionCajaAhorroBean>();
		ResultSet rs = this.consulta("SELECT t.fecha,t.hora,t.tipo,t.monto,t.cod_caja,t.destino FROM trans_cajas_ahorro AS t JOIN tarjeta ON t.nro_ca = tarjeta.nro_ca WHERE tarjeta.nro_tarjeta ="+tarjeta+" ORDER BY t.fecha DESC LIMIT "+cantidad+";");
		try {
			while(rs.next()) {
				TransaccionCajaAhorroBean transaccion = new TransaccionCajaAhorroBeanImpl();
				transaccion.setTransaccionFechaHora(Fechas.convertirStringADate(rs.getString("fecha"),rs.getString("hora")));
				String tipo = rs.getString("tipo");
				Double monto = rs.getDouble("monto");
				transaccion.setTransaccionTipo(tipo);
				if(tipo.equalsIgnoreCase("debito") || tipo.equalsIgnoreCase("transferencia")|| tipo.equalsIgnoreCase("extraccion")){ 
					monto = -monto;
				}
				transaccion.setTransaccionMonto(monto);
				transaccion.setTransaccionCodigoCaja(rs.getInt("cod_caja"));
				transaccion.setCajaAhorroDestinoNumero(rs.getInt("destino"));
				lista.add(transaccion);
			}
		}catch (java.sql.SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			logger.error("SQLState: " + e.getSQLState());
			logger.error("VendorError: " + e.getErrorCode());
			throw new Exception("Error inesperado al consultar los ultimo movimientos.");
		}
		return lista;

	}	
	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarMovimientosPorPeriodo(Date desde, Date hasta)
			throws Exception {

		if (desde == null) {
			throw new Exception("El inicio del período no puede estar vacío");
		}
		if (hasta == null) {
			throw new Exception("El fin del período no puede estar vacío");
		}
		if (desde.after(hasta)) {
			throw new Exception("El inicio del período no puede ser posterior al fin del período");
		}	
		
		Date fechaActual = new Date();
		if (desde.after(fechaActual)) {
			throw new Exception("El inicio del período no puede ser posterior a la fecha actual");
		}	
		if (hasta.after(fechaActual)) {
			throw new Exception("El fin del período no puede ser posterior a la fecha actual");
		}	
		ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<TransaccionCajaAhorroBean>();
		ResultSet rs = this.consulta("SELECT t.fecha,t.hora,t.tipo,t.monto,t.cod_caja,t.destino FROM trans_cajas_ahorro AS t JOIN tarjeta ON t.nro_ca = tarjeta.nro_ca WHERE tarjeta.nro_tarjeta ="+tarjeta+" AND '"+Fechas.convertirDateAStringDB(desde)+"' <= fecha AND fecha <= '"+Fechas.convertirDateAStringDB(hasta)+"' ORDER BY fecha DESC;;");
		try {
			while(rs.next()) {
				TransaccionCajaAhorroBean transaccion = new TransaccionCajaAhorroBeanImpl();
				transaccion.setTransaccionFechaHora(Fechas.convertirStringADate(rs.getString("fecha"),rs.getString("hora")));
				String tipo = rs.getString("tipo");
				Double monto = rs.getDouble("monto");
				transaccion.setTransaccionTipo(tipo);
				if(tipo.equalsIgnoreCase("debito") || tipo.equalsIgnoreCase("transferencia")|| tipo.equalsIgnoreCase("extraccion")){ 
					monto = -monto;
				}
				transaccion.setTransaccionMonto(monto);
				transaccion.setTransaccionCodigoCaja(rs.getInt("cod_caja"));
				transaccion.setCajaAhorroDestinoNumero(rs.getInt("destino"));
				lista.add(transaccion);
			}
			logger.debug("Retorna una lista con {} elementos", lista.size());
		}catch (java.sql.SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			logger.error("SQLState: " + e.getSQLState());
			logger.error("VendorError: " + e.getErrorCode());
			throw new Exception("Error inesperado al consultar los movimientos dentro de una determinada fecha.");
		}
		return lista;
	}
	
	@Override
	public Double extraer(Double monto) throws Exception {
		logger.info("Realiza la extraccion de ${} sobre la cuenta", monto);
		
		if (this.codigoATM == null) {
			throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
		}
		if (this.tarjeta == null) {
			throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
		}
		String resultado="";
		String sql = "call p_extraccion("+monto+","+this.codigoATM+","+this.tarjeta+");";
		logger.debug("SQL: "+sql);
	
		try{ 
			 ResultSet rs = this.consulta(sql);
			 if (rs.next()) {
				logger.debug("Resultado: {}", rs.getString("resultado"));
				logger.debug("Saldo resultante: {}", rs.getDouble("saldo"));
				resultado = rs.getString("resultado");
			  }	
			 
			 if (!resultado.equals(ModeloATM.TRANSFERENCIA_EXITOSA)) {
					throw new Exception(resultado);
				}
			 
			 return obtenerSaldo();	
				
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
	public Double transferir(Double monto, int cajaDestino) throws Exception {
		logger.info("Realiza la transferencia de ${} sobre a la cuenta {}", monto, cajaDestino);
		
		if (this.codigoATM == null) {
			throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
		}
		if (this.tarjeta == null) {
			throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
		}

		String resultado = ModeloATM.TRANSFERENCIA_EXITOSA;
		String sql = "call p_transferencia("+monto+","+this.codigoATM+","+this.tarjeta+","+cajaDestino+");";
		logger.debug("SQL: "+sql);
		
		try{ 
			 ResultSet rs = this.consulta(sql);
			 if (rs.next()) {
				logger.debug("Resultado: {}", rs.getString("resultado"));
				logger.debug("Saldo resultante: {}", rs.getDouble("saldo"));
				resultado = rs.getString("resultado");
			  }	
			 
			 if (!resultado.equals(ModeloATM.TRANSFERENCIA_EXITOSA)) {
					throw new Exception(resultado);
				}
			 return obtenerSaldo();		
				
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
	public int parseCuenta(String p_cuenta) throws Exception {
		
		logger.info("Intenta realizar el parsing de un codigo de cuenta {}", p_cuenta);
		int to_ret;
		if (p_cuenta == null) {
			throw new Exception("El código de la cuenta no puede ser vacío");
		}
		to_ret = Integer.parseInt(p_cuenta);
		
		ResultSet rSet = this.consulta("SELECT * FROM  caja_ahorro WHERE nro_ca ="+to_ret +";");
		try {
			if(rSet.next()) {
				logger.info("Encontró la cuenta en la BD.");
				return to_ret;
			}else {
				throw new Exception("No se encontro el numero de cuerta en la BD.q");
			}
		}catch (java.sql.SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			logger.error("SQLState: " + e.getSQLState());
			logger.error("VendorError: " + e.getErrorCode());
			throw new Exception("Error inesperado al verificar si el numero de caja es correcto.");
		}
		
	}	


	@Override
	public Double parseMonto(String p_monto) throws Exception {
		
		logger.info("Intenta realizar el parsing del monto {}", p_monto);
		
		if (p_monto == null) {
			throw new Exception("El monto no puede estar vacío");
		}

		try 
		{
			double monto = Double.parseDouble(p_monto);
			DecimalFormat df = new DecimalFormat("#.00");

			monto = Double.parseDouble(corregirComa(df.format(monto)));
			String montoString = String.valueOf(monto);
			if(monto < 0)
			{
				throw new Exception("El monto no debe ser negativo.");
			}
			logger.info("Realizo el parsing correctamentee, monto: {}", montoString);
			return monto;
		}		
		catch (NumberFormatException e)
		{
			throw new Exception("El monto no tiene un formato válido.");
		}	
	}

	private String corregirComa(String n)
	{
		String toReturn = "";
		
		for(int i = 0;i<n.length();i++)
		{
			if(n.charAt(i)==',')
			{
				toReturn = toReturn + ".";
			}
			else
			{
				toReturn = toReturn+n.charAt(i);
			}
		}
		
		return toReturn;
	}	
	

}
