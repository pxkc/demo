package com.nowcoder.demo.dao;

import com.nowcoder.demo.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author pxk
 * @date 2019/11/20 - 14:35
 */
@Mapper
public interface CommentDao {
    String TABLE_NAME = " comment";
    String INSERT_FIELDS = "user_id, content, created_date, entity_type, entity_id, status";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId}, #{content}, #{createdDate}, #{entityType}, #{entityId}, #{status})"})
    int addComment(Comment comment);//返回 int 失败或者成功

    @Select({"select", SELECT_FIELDS ,"from", TABLE_NAME,
            " where entity_id = #{entityId} and entity_type = #{entityType} order by id desc" })
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);
    //entityType 写成了 Tpye
    //不是ordered by 是order by
    //数据库 是下划线，不是驼峰命名法4
    @Select({"select count(id) from ", TABLE_NAME,
            "where entity_id = #{entityId} and entity_type = #{entityType} order by id desc"})
    int getCommentsCount(@Param("entityId") int entityId, @Param("entityType") int entityType);
    //又是Tpye
}