package com.exam.service;

import com.exam.entity.Admin;
import com.exam.entity.Student;
import com.exam.entity.Teacher;

public interface LoginService {
    public Admin adminLogin(Integer username, String password);

    public Teacher teacherLogin(Integer username, String password);

    public Student studentLogin(Integer username, String password);

    /**
     * 统计证件号/手机号/邮箱是否已被占用
     */
    int countUserByContact(String cardId, String tel, String email);
}
