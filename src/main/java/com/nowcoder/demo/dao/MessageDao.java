package com.nowcoder.demo.dao;

import com.nowcoder.demo.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author pxk
 * @date 2019/11/20 - 21:45
 */
@Mapper
public interface MessageDao {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    //由于缺少一个, 导致id和from_id 没有正式的隔开，导致from_id变成了id 结果错误


    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId}, #{toId}, #{content}, #{hasRead}, #{conversationId}, #{createdDate})"})
    int addMessage(Message message);


    @Select({"Select count(id) from ", TABLE_NAME, " where has_read = 0 and to_id = #{userId} and conversation_id = #{conversationId} "})
    int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME,
            "where conversation_id = #{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset, @Param("limit") int limit);
    @Select({"select ", INSERT_FIELDS, ", count(id) as id from(select * from ", TABLE_NAME, "where from_id = #{userId} or to_id = #{userId} order by id desc) tt group by conversation_id order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                        @Param("offset") int offset, @Param("limit") int limit);
}
