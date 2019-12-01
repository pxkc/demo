package com.nowcoder.demo.dao;

import com.nowcoder.demo.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author pxk
 * @date 2019/10/20 - 11:12
 */
@Mapper
public interface LoginTicketDao {
    String TABLE_NAME = "login_ticket";
    String INSERT_FILEDS = "user_id, expired, status, ticket";
    String SELCET_FILEDS = "id, " + INSERT_FILEDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FILEDS, ") values (#{userId}, #{expired}, #{status}, #{ticket})"})
    int addTicket(LoginTicket ticket);

    @Select({"select ", SELCET_FILEDS, "from", TABLE_NAME, "where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, "set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int stauts);
    //第一次用两个参数，@Param("ticket") ,@Param("status")

}
