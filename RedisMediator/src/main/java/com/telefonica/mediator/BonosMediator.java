package com.telefonica.mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class BonosMediator extends AbstractMediator { 

	private Log log = LogFactory.getLog(BonosMediator.class);  
	private String codigo;
	
	public boolean mediate(MessageContext context) { 
		saveBonosInRedis(context); 
		return true;
	}
	
	private void saveBonosInRedis(MessageContext context) {
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
}