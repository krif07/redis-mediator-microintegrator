package com.telefonica.mediator;

/**
 * Constants for Redis Class Mediator
 */
public class RedisClassMediatorConstants {

    // Redis Cluster Configuration values
    protected static final String REDIS_SERVER_HOST = "redis.server.host";
    protected static final String REDIS_SERVER_PORT = "redis.server.port";
    protected static final String REDIS_SERVER_TIMEOUT = "redis.server.timeout";
    protected static final String REDIS_SERVER_PASSWORD = "redis.server.password";
    protected static final String REDIS_SERVER_DATABASE = "redis.server.database";
    protected static final String REG_PREFIX = "conf:/test/";

    // Redis Class mediator Properties
    protected static final String REDIS_GET_KEY = "redis.key.get";
    protected static final String REDIS_GET_VALUE = "redis.value.get";
    protected static final String REDIS_SET_KEY = "redis.key.set";
    protected static final String REDIS_SET_VALUE = "redis.value.set";
    protected static final String REDIS_SET_VALUE_STATUS = "redis.set.status";
    protected static final String REDIS_SET_TTL_VALUE = "redis.ttl.set";

    // Redis Connection Factory Configurations
    protected static final String REDIS_SERVER_MAX_TOTAL = "redis.server.maxTotal";
    protected static final String REDIS_SERVER_MAX_IDLE = "redis.server.maxIdle";
    protected static final String REDIS_SERVER_MIN_IDLE = "redis.server.minIdle";
    protected static final String REDIS_SERVER_TEST_ON_BORROW = "redis.server.testOnBorrow";
    protected static final String REDIS_SERVER_TEST_ON_RETURN = "redis.server.testOnReturn";
    protected static final String REDIS_SERVER_TEST_WHILE_IDLE = "redis.server.testWhileIdle";
    protected static final String REDIS_SERVER_MIN_EVICT_IDL_TIME = "redis.server.minEvictableIdleTimeMillis";
    protected static final String REDIS_SERVER_TIME_BW_EVCT_RUNS = "redis.server.timeBetweenEvictionRunsMillis";
    protected static final String REDIS_SERVER_NUM_TESTS_PER_EVCT_RUN = "redis.server.numTestsPerEvictionRun";
    protected static final String REDIS_SERVER_BLOCK_WHEN_EXHAUSTED = "redis.server.blockWhenExhausted";

}
