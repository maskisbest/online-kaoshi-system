package com.exam.mapper;

import com.exam.entity.Admin;
import com.exam.entity.Student;
import com.exam.entity.Teacher;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select(
        "select adminId,adminName,sex,tel,email,cardId,role from admin where adminId = #{username} and pwd = #{password}"
    )
    public Admin adminLogin(Integer username, String password);

    @Select(
        "select teacherId,teacherName,institute,sex,tel,email,cardId," +
            "type,role from teacher where teacherId = #{username} and pwd = #{password}"
    )
    public Teacher teacherLogin(Integer username, String password);

    @Select(
        "select studentId,studentName,grade,major,clazz,institute,tel," +
            "email,cardId,sex,role from student where studentId = #{username} and pwd = #{password}"
    )
    public Student studentLogin(Integer username, String password);

    /**
     * 检查三类用户是否存在相同的证件号/电话/邮箱
     */
    @Select(
        "select " +
            "(select count(1) from admin where cardId = #{cardId} or tel = #{tel} or email = #{email}) +" +
            "(select count(1) from teacher where cardId = #{cardId} or tel = #{tel} or email = #{email}) +" +
            "(select count(1) from student where cardId = #{cardId} or tel = #{tel} or email = #{email})"
    )
    int countUserByContact(
        @Param("cardId") String cardId,
        @Param("tel") String tel,
        @Param("email") String email
    );
}
