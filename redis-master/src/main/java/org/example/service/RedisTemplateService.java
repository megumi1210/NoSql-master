package org.example.service;

/**
 * 如果需要使用 redisTemplate 更高级的功能时可以使用此服务接口
 * @author chenj
 */
public interface RedisTemplateService {
    /**
     *  执行多个命令
     */
    void execMultiCommand();

    /**
     * 执行 Redis 事务
     */
    void  execTransaction();

    /**
     *  执行 Redis 流水线
     */
    void  execPipeline();
}
