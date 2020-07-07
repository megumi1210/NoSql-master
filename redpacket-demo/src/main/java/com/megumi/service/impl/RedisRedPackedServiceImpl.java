package com.megumi.service.impl;


import com.megumi.domain.UserRedPacket;
import com.megumi.service.RedisRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenj
 */
@Service
@SuppressWarnings("all")
public class RedisRedPackedServiceImpl implements RedisRedPacketService {
    private static final  String  PREFIX = "red_packet_list_";
    //每次取出1000 条 ，避免一次取出消耗太多内存
    private static  final  int  TIME_SIZE = 1000;

    @Autowired
     @Qualifier("stringToStringRedisTemplate")
     private RedisTemplate redisTemplate = null;

     @Autowired
     private DataSource dataSource = null;

    @Override
    //开启新线程运行
    @Async
    public void saveUserRedPacketByRedis(Long rePackedId, Double unitAmount) {
        System.out.println("开始保存数据");
        Long start = System.currentTimeMillis();

        //获取列表操作对象
        BoundListOperations ops  =redisTemplate.boundListOps(PREFIX + rePackedId);
        Long size = ops.size();
        Long times = size % TIME_SIZE == 0? size /TIME_SIZE:size/TIME_SIZE+1;
        int count = 0;
        List<UserRedPacket>  userRedPacketList = new ArrayList<>(TIME_SIZE);
        for(int i  = 0 ; i< times;i++){
            //获取至多TIME_SIZE个抢红包信息
            List userIdList = null;
            if(i ==0 ){
                 userIdList = ops.range(i*TIME_SIZE,(i+1)*TIME_SIZE);
            }else {
                userIdList = ops.range(i*TIME_SIZE +1 ,(i+1)*TIME_SIZE);
            }

            userRedPacketList.clear();

            for(int j  = 0 ; j< userIdList.size() ; j++){
                String args = userIdList.get(j).toString();
                String[] arr =  args.split("-");
                String  userIdStr =  arr[0];
                String timeStr = arr[1];
                Long userId = Long.parseLong(userIdStr);
                Long time = Long.parseLong(timeStr);
                //生成抢红包信息
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPackedId(rePackedId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote("抢红包" + rePackedId);
                userRedPacketList.add(userRedPacket);
            }
            //插入抢红包信息
            count+= executeBatch(userRedPacketList);
            //删除Redis 列表
            redisTemplate.delete(PREFIX +rePackedId);
            Long end = System.currentTimeMillis();
            System.out.println("保存数据结束，耗时" +(end - start) +"毫秒,共" +
                    count +"条记录被保存");
        }

    }


    private  int executeBatch(List<UserRedPacket> userRedPackets){
        Connection conn = null;
        Statement stmt = null;
        int[] count = null;

        try {
             conn = dataSource.getConnection();
             conn.setAutoCommit(false);
             stmt =conn.createStatement();
             for(UserRedPacket userRedPacket :userRedPackets){
                 String sql1="update T_RED_PACKET set stock=stock-1 where id="
                             + userRedPacket.getRedPackedId();
                 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 String  sql2 = "insert into T_USER_RED_PACKET(red_packet_id,user_id," +
                               "amount, grab_time, note)" +
                                    "values(" + userRedPacket.getRedPackedId() +"," +
                                                userRedPacket.getUserId() +"," +
                                                userRedPacket.getAmount() +"," +
                                         "'"  +  df.format(userRedPacket.getGrabTime()) + "',"+
                                         "'"   +  userRedPacket.getNote() + "')";
                                   stmt.addBatch(sql1);
                                   stmt.addBatch(sql2);
             }

             //执行批量
            count =  stmt.executeBatch();
             //提交操作
            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException("抢红包批量执行程序错误");
        }finally {
            try{
                if(conn!= null && !conn.isClosed()){
                    conn.close();
                }
            }catch (SQLException e){
                 e.printStackTrace();
            }
        }
        //返回返回抢红包的数据记录
        return  count.length/2;
    }
}
