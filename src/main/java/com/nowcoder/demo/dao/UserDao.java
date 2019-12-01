package com.nowcoder.demo.dao;

import com.nowcoder.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * @author pxk
 * @date 2019/10/6 - 17:10
 */
@Mapper
public interface UserDao {

    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name, password, salt, headUrl ";
    String SELECT_FIELDS = " id, name, password, salt, headUrl";


    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{name}, #{password}, #{salt}, #{headUrl})"})
    int addUser(User user);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, "where id =#{id}"})
    User selcetById(int id);

    @Select({"select", SELECT_FIELDS, " from ", TABLE_NAME, "where name =#{name}"})
    User selcetByName(String name);


    @Update({"update", TABLE_NAME, "set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
    void deleteById(int id);
}