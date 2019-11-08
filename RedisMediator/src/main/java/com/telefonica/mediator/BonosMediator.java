package com.telefonica.mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class BonosMediator extends AbstractMediator { 

	private Log log = LogFactory.getLog(BonosMediator.class);  
	private String codigo;
	
	private String redisHost = "redis-13973.c61.us-east-1-3.ec2.cloud.redislabs.com";
	private Integer redisPort = 13973;
	private Integer redisTimeout = 3600;
	private String redisPassword = "qkW6SKVi7XZeWAwucxkB4rcu065AEu2r";
	private String redisMessage = "MENSAJE DE REDIS";
	
	public boolean mediate(MessageContext context) { 
		saveBonosInRedis(context);
		showStepsInLog(context);
		return true;
	}
	
	private void saveBonosInRedis(MessageContext context) {
		log.debug("------------------ Inicio del servicio save ---------------");
		
		context.setProperty("redisHost",getRedisHost());
		context.setProperty("redisPort",getRedisPort());
		context.setProperty("redisTimeout",getRedisTimeout());
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

	public Integer getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(Integer redisPort) {
		this.redisPort = redisPort;
	}

	public Integer getRedisTimeout() {
		return redisTimeout;
	}

	public void setRedisTimeout(Integer redisTimeout) {
		this.redisTimeout = redisTimeout;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}

	public String getRedisMessage() {
		return redisMessage;
	}

	public void setRedisMessage(String redisMessage) {
		this.redisMessage = redisMessage;
	}
}