package com.telefonica.mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class BonosMediator extends AbstractMediator { 

	private Log log = LogFactory.getLog(BonosMediator.class);  
	private String codigo;
	
	private String redisHost;
	private String redisPort;
	private String redisPassword;
	private String redisJedisPoolMaxTotal;
	private String redisJedisPoolMaxIdle;
	private String redisJedisPoolMinIdle;
	
	public boolean mediate(MessageContext context) { 
		saveBonosInRedis(context);
		showStepsInLog(context);
		return true;
	}
	
	private void saveBonosInRedis(MessageContext context) {
		log.debug("------------------ Inicio del servicio save ---------------");
		
		context.setProperty("redisHost",getRedisHost());
		context.setProperty("redisPort",getRedisPort());		
		context.setProperty("redisPassword",getRedisPassword());
		
		log.debug("-------------------- Fin del servicio save ------------------");
	}

	private void showStepsInLog(MessageContext context) {
		String bonoName = (String)context.getProperty("BONONAME");
		log.debug(" ------------------------- CODIGO obteined from secuence is --" + bonoName);
		
		String converted = bonoName.toUpperCase();
		
		context.setProperty("CONVERTED", "El bono " + converted + " tiene codigo: " + getCodigo());
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public String getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(String redisPort) {
		this.redisPort = redisPort;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}

	public String getRedisJedisPoolMaxTotal() {
		return redisJedisPoolMaxTotal;
	}

	public void setRedisJedisPoolMaxTotal(String redisJedisPoolMaxTotal) {
		this.redisJedisPoolMaxTotal = redisJedisPoolMaxTotal;
	}

	public String getRedisJedisPoolMaxIdle() {
		return redisJedisPoolMaxIdle;
	}

	public void setRedisJedisPoolMaxIdle(String redisJedisPoolMaxIdle) {
		this.redisJedisPoolMaxIdle = redisJedisPoolMaxIdle;
	}

	public String getRedisJedisPoolMinIdle() {
		return redisJedisPoolMinIdle;
	}

	public void setRedisJedisPoolMinIdle(String redisJedisPoolMinIdle) {
		this.redisJedisPoolMinIdle = redisJedisPoolMinIdle;
	}

		
}