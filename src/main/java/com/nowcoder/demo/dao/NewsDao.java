package com.nowcoder.demo.dao;

import com.nowcoder.demo.model.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

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
//
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createDate},#{userId})"})
    int addNews(News news);

    // 用注解处理，到NewsDao.xml查看
    List<News> selectByUserIdAndOffset(@Param("userId") int userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);
}