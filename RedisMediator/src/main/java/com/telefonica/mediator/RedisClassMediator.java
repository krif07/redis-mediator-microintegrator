package com.telefonica.mediator;

import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.config.Entry;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.registry.Registry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** * Redis Class Mediator * Supports GET / SET Operations */
public class RedisClassMediator extends AbstractMediator {

	private static final Log log = LogFactory.getLog(RedisClassMediator.class);
	private static JedisPool jedisPool;
	private Registry registryInstance;

	@Override
	public boolean mediate(MessageContext messageContext) {

		Jedis jedis = null;
		boolean isGet = false;
		boolean isSet = false;

		try {
			// Get Registry Object
			registryInstance = messageContext.getConfiguration().getRegistry();

			if (registryInstance == null) {
				log.error("Cannot initiate Registry to read config values.");
				return false;
			}

			jedis = getJedisObject(messageContext);

			if (jedis == null) {
				log.error("Unexpected error. Cannot initiate Jedis Resource. Check your jedis connection details.");
				return false;
			}

			jedis.connect();

			String redisGetKey = (String) messageContext.getProperty(RedisClassMediatorConstants.REDIS_GET_KEY);
			
			if (StringUtils.isNotEmpty(redisGetKey)) {
				isGet = true;
			}
			Set pros = messageContext.getPropertyKeySet();
			// Remove if any previously set attributes from the connector before executing
			// again.
			if (pros != null) {
				pros.remove(RedisClassMediatorConstants.REDIS_GET_VALUE);
				pros.remove(RedisClassMediatorConstants.REDIS_SET_VALUE_STATUS);
			}
			String redisSetKey = (String) messageContext.getProperty(RedisClassMediatorConstants.REDIS_SET_KEY);
			String redisSetValue = (String) messageContext.getProperty(RedisClassMediatorConstants.REDIS_SET_VALUE);
			if (StringUtils.isNotEmpty(redisSetKey) && StringUtils.isNotEmpty(redisSetValue)) {
				isSet = true;
			}

			if (isGet) {				
				hgetRedisOperation(messageContext, jedis, redisGetKey);				
				
			} else if (isSet) {
				hsetRedisOperation(messageContext, jedis, redisSetKey, redisSetValue);
								
			} else {
				log.error("Cannot find required Redis GET or SET Properties, skipping Redis Mediator");
			}

		} catch (Exception e) {
			String error = "Error occurred while handling message in RedisClassMediator. " + e;
			handleException(error, messageContext);
		} finally {

			if (jedis != null) {
				jedis.disconnect();
				jedis.close();
			}
		}
		return true;
	}
	
	private void getRedisOperation(MessageContext messageContext, Jedis jedis, String redisGetKey){		
		Set pros = messageContext.getPropertyKeySet();
		// Handle Redis GET Operations
		String redisGetValue = jedis.get(redisGetKey);
		// String redisGetValue = jedis.getrange(redisGetKey, 0, -1);
		if (StringUtils.isNotEmpty(redisGetValue)) {
			messageContext.setProperty(RedisClassMediatorConstants.REDIS_GET_VALUE, redisGetValue);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Get [Key] %s and Get [Value] %s for [messageId] %s", redisGetKey,
						redisGetValue, messageContext.getMessageID()));
			}

		} else {
			log.warn(String.format("A Valid value for [key] %s not found in Redis for [messageId] %s",
					redisGetKey, messageContext.getMessageID()));
		}
		
		// Removing property after use
		if (pros != null) {
			pros.remove(RedisClassMediatorConstants.REDIS_GET_KEY);
		}
		
	}
	
	private void setRedisOperation(MessageContext messageContext, Jedis jedis, String redisSetKey, String redisSetValue){
		Set pros = messageContext.getPropertyKeySet();
		
		// Handle Redis PUT Operations
		String status;
		Object ttlValueObj = messageContext.getProperty(RedisClassMediatorConstants.REDIS_SET_TTL_VALUE);
		if (ttlValueObj instanceof Integer) {
			int redisSetTTLValue = (Integer) messageContext
					.getProperty(RedisClassMediatorConstants.REDIS_SET_TTL_VALUE);
			status = jedis.setex(redisSetKey, redisSetTTLValue, redisSetValue);
		} else {
			status = jedis.set(redisSetKey, redisSetValue);
		}
		messageContext.setProperty(RedisClassMediatorConstants.REDIS_SET_VALUE_STATUS, status);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Set [Key] %s and Set [Value] %s for [messageId] %s", redisSetKey,
					redisSetValue, messageContext.getMessageID()));
		}
		
		// Removing properties after usage
		if (pros != null) {
			pros.remove(RedisClassMediatorConstants.REDIS_SET_KEY);
			pros.remove(RedisClassMediatorConstants.REDIS_SET_VALUE);
			if (ttlValueObj != null) {
				pros.remove(RedisClassMediatorConstants.REDIS_SET_TTL_VALUE);
			}
		}
	}
	
	private void hgetRedisOperation(MessageContext messageContext, Jedis jedis, String redisGetKey){
		log.warn("---------------------------------------------------------- hget--------------------------------------");
		log.warn(redisGetKey);
		Set pros = messageContext.getPropertyKeySet();
		// Handle Redis GET Operations
//		Map<String, String> fields = jedis.hmget("bonos1", redisGetKey);
		List<String> redisGetValue = jedis.hmget("bonos1", redisGetKey);
		log.warn(redisGetValue);
		/*if (StringUtils.isNotEmpty(redisGetValue)) {
			messageContext.setProperty(RedisClassMediatorConstants.REDIS_GET_VALUE, redisGetValue);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Get [Key] %s and Get [Value] %s for [messageId] %s", redisGetKey,
						redisGetValue, messageContext.getMessageID()));
			}			

		} else {
			log.warn(String.format("A Valid value for [key] %s not found in Redis for [messageId] %s",
					redisGetKey, messageContext.getMessageID()));
		}*/
		
		// Removing property after use
		if (pros != null) {
			pros.remove(RedisClassMediatorConstants.REDIS_GET_KEY);
		}
		
	}
	
	private void hsetRedisOperation(MessageContext messageContext, Jedis jedis, String redisSetKey, String redisSetValue){
		Set pros = messageContext.getPropertyKeySet();
		// Handle Redis PUT Operations
				
		/*messageContext.setProperty(RedisClassMediatorConstants.REDIS_SET_VALUE_STATUS, status);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Set [Key] %s and Set [Value] %s for [messageId] %s", redisSetKey,
					redisSetValue, messageContext.getMessageID()));
		}*/
		JSONObject dataJsonObject = new JSONObject(redisSetValue);
		
		JSONArray dataArray = dataJsonObject.getJSONArray("BONUS");
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject fila = (JSONObject) dataArray.get(i);
			Map<String, String> values = new HashMap<String, String>();
			
			values.put(fila.getString("TIPO_BONO"), fila.toString());
			//log.warn(redisSetKey +"-"+ fila.getString("TIPO_BONO").toString() +"-"+ fila.toString());
			jedis.hset(redisSetKey, values);						
		}
		
		// Removing properties after usage
		if (pros != null) {
			pros.remove(RedisClassMediatorConstants.REDIS_SET_KEY);
			pros.remove(RedisClassMediatorConstants.REDIS_SET_VALUE);			
		}
	}

	private Jedis getJedisObject(MessageContext messageContext) {

		String redisHost;
		int redisPort;
		int redisTimeout;
		String redisPassword;
		int redisDatabase;
		Jedis jedis = null;

		String redisHostString = (String) messageContext.getProperty(RedisClassMediatorConstants.REDIS_SERVER_HOST);
		/*
		 * String redisHostString = getRegistryResourceString(
		 * RedisClassMediatorConstants.REG_PREFIX +
		 * RedisClassMediatorConstants.REDIS_SERVER_HOST);
		 */
		if (StringUtils.isNotEmpty(redisHostString)) {
			redisHost = redisHostString.trim();
		} else {
			log.error(
					"Redis Hostname is not set in Registry configurations, hence skipping RedisClassMediator execution");
			return null;
		}

		String redisPortString = (String) messageContext.getProperty(RedisClassMediatorConstants.REDIS_SERVER_PORT);
		/*
		 * String redisPortString = getRegistryResourceString(
		 * RedisClassMediatorConstants.REG_PREFIX +
		 * RedisClassMediatorConstants.REDIS_SERVER_PORT);
		 */
		if (StringUtils.isNotEmpty(redisPortString)) {
			redisPort = Integer.parseInt(redisPortString.trim());
		} else {
			log.info("Redis Port is not set in Registry configuration, hence using default port as 6379");
			redisPort = 6379;
		}

		String redisTimeoutString = (String) messageContext
				.getProperty(RedisClassMediatorConstants.REDIS_SERVER_TIMEOUT);
		/*
		 * String redisTimeoutString = getRegistryResourceString(
		 * RedisClassMediatorConstants.REG_PREFIX +
		 * RedisClassMediatorConstants.REDIS_SERVER_TIMEOUT);
		 */
		if (StringUtils.isNotEmpty(redisTimeoutString)) {
			redisTimeout = Integer.parseInt(redisTimeoutString.trim());
		} else {
			log.info("Redis timeout is not set in Registry configuration, hence using default timeout as 2000");
			redisTimeout = 2000;
		}

		String redisPasswordString = (String) messageContext
				.getProperty(RedisClassMediatorConstants.REDIS_SERVER_PASSWORD);
		/*
		 * String redisPasswordString = getRegistryResourceString(
		 * RedisClassMediatorConstants.REG_PREFIX +
		 * RedisClassMediatorConstants.REDIS_SERVER_PASSWORD);
		 */
		if (StringUtils.isNotEmpty(redisHostString)) {
			redisPassword = redisPasswordString.trim();
		} else {
			log.error(
					"Redis Password is not set in Registry configurations, hence skipping RedisClassMediator execution");
			return null;
		}

		String redisDatabaseString = (String) messageContext
				.getProperty(RedisClassMediatorConstants.REDIS_SERVER_DATABASE);
		/*
		 * String redisDatabaseString = getRegistryResourceString(
		 * RedisClassMediatorConstants.REG_PREFIX +
		 * RedisClassMediatorConstants.REDIS_SERVER_DATABASE);
		 */
		if (StringUtils.isNotEmpty(redisDatabaseString)) {
			redisDatabase = Integer.parseInt(redisDatabaseString.trim());
		} else {
			log.info("Redis database is not set in Registry configuration, hence using default database as 0");
			redisDatabase = 0;
		}

		jedis = getRedisPool(redisHost, redisPort, redisTimeout, redisPassword, redisDatabase).getResource();

		return jedis;
	}

	private synchronized JedisPool getRedisPool(String host, int port, int timeout, String password, int database) {
		if (jedisPool != null) {
			return jedisPool;
		} else {
			jedisPool = new JedisPool(buildPoolConfig(), host, port, timeout, password, database);
			if (log.isDebugEnabled()) {
				log.debug("Redis Connection Pool initialized");
			}
			return jedisPool;
		}
	}

	// Build Jedis Connection Pool
	private JedisPoolConfig buildPoolConfig() {
		final JedisPoolConfig poolConfig = new JedisPoolConfig();

		// Jedis Connection Pool Default Configuration Values
		int maxTotal = 128;
		int maxIdle = 128;
		int minIdle = 16;
		boolean testOnBorrow = true;
		boolean testOnReturn = true;
		boolean testWhileIdle = true;
		long minEvictableIdleTimeMillis = 60000;
		long timeBetweenEvictionRunsMillis = 30000;
		int numTestsPerEvictionRun = 3;
		boolean blockWhenExhausted = true;

		// Read and override values from registry
		String maxTotalString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_MAX_TOTAL);
		String maxIdleString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_MAX_IDLE);
		String minIdleString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_MIN_IDLE);
		String testOnBorrowString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_TEST_ON_BORROW);
		String testOnReturnString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_TEST_ON_RETURN);
		String testWhileIdleString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_TEST_WHILE_IDLE);
		String minEvictableIdleTimeMillisString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_MIN_EVICT_IDL_TIME);
		String timeBetweenEvictionRunsMillisString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_TIME_BW_EVCT_RUNS);
		String numTestsPerEvictionRunString = getRegistryResourceString(RedisClassMediatorConstants.REG_PREFIX
				+ RedisClassMediatorConstants.REDIS_SERVER_NUM_TESTS_PER_EVCT_RUN);
		String blockWhenExhaustedString = getRegistryResourceString(
				RedisClassMediatorConstants.REG_PREFIX + RedisClassMediatorConstants.REDIS_SERVER_BLOCK_WHEN_EXHAUSTED);

		if (StringUtils.isNotEmpty(maxTotalString)) {
			maxTotal = Integer.parseInt(maxTotalString.trim());
		}

		if (StringUtils.isNotEmpty(maxIdleString)) {
			maxIdle = Integer.parseInt(maxIdleString.trim());
		}

		if (StringUtils.isNotEmpty(minIdleString)) {
			minIdle = Integer.parseInt(minIdleString.trim());
		}

		if (StringUtils.isNotEmpty(testOnBorrowString)) {
			testOnBorrow = Boolean.parseBoolean(testOnBorrowString.trim());
		}

		if (StringUtils.isNotEmpty(testOnReturnString)) {
			testOnReturn = Boolean.parseBoolean(testOnReturnString.trim());
		}

		if (StringUtils.isNotEmpty(testWhileIdleString)) {
			testWhileIdle = Boolean.parseBoolean(testWhileIdleString.trim());
		}

		if (StringUtils.isNotEmpty(minEvictableIdleTimeMillisString)) {
			minEvictableIdleTimeMillis = Long.parseLong(minEvictableIdleTimeMillisString.trim());
		}

		if (StringUtils.isNotEmpty(timeBetweenEvictionRunsMillisString)) {
			timeBetweenEvictionRunsMillis = Long.parseLong(timeBetweenEvictionRunsMillisString.trim());
		}

		if (StringUtils.isNotEmpty(numTestsPerEvictionRunString)) {
			numTestsPerEvictionRun = Integer.parseInt(numTestsPerEvictionRunString.trim());
		}

		if (StringUtils.isNotEmpty(blockWhenExhaustedString)) {
			blockWhenExhausted = Boolean.parseBoolean(blockWhenExhaustedString.trim());
		}

		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setTestOnBorrow(testOnBorrow);
		poolConfig.setTestOnReturn(testOnReturn);
		poolConfig.setTestWhileIdle(testWhileIdle);
		poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		poolConfig.setBlockWhenExhausted(blockWhenExhausted);

		return poolConfig;
	}

	// Read a registry resource file as a String
	private String getRegistryResourceString(String registryPath) {
		String registryResourceContent = null;

		Object obj = registryInstance.getResource(new Entry(registryPath), null);
		if (obj != null && obj instanceof OMTextImpl) {
			registryResourceContent = ((OMTextImpl) obj).getText();
		}
		return registryResourceContent;
	}

}
