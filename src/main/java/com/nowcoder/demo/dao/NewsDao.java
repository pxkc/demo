package com.nowcoder.demo.dao;

import ch.qos.logback.classic.db.names.TableName;
import com.nowcoder.demo.model.News;
import com.nowcoder.demo.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author pxk
 * @date 2019/10/8 - 15:27
 */
@Mapper//无论是注解方式，还是xml方式，这个是必须的
public interface NewsDao {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, create_date, user_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Update({"update", TABLE_NAME, "set comment_count = #{count} where id = #{id}" })
    void updateCommentCount(@Param("id") int id, @Param("count") int count);

    @Update({"update", TABLE_NAME, "set like_count = #{count} where id = #{id}" })
    void updateLikeCount(@Param("id") int id, @Param("count") int count);

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createDate},#{userId})"})
    int addNews(News news);

    List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
    //建文件夹要分开建 用注解处理，到NewsDao.xml查看


    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, "where id =#{id}"})
    News getById(int newsId);
}