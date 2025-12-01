package com.exam.controller;

import com.exam.Result.ApiResult;
import com.exam.Result.ApiResultHandler;
import com.exam.entity.Admin;
import com.exam.entity.Student;
import com.exam.entity.Teacher;
import com.exam.service.serviceimpl.AdminServiceImpl;
import com.exam.service.serviceimpl.LoginServiceImpl;
import com.exam.service.serviceimpl.StudentServiceImpl;
import com.exam.service.serviceimpl.TeacherServiceImpl;
import com.exam.vo.RegisterRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录/注册/退出等统一入口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final List<String> SUPPORT_ROLES = Arrays.asList(
        "0",
        "1",
        "2"
    );

    @Autowired
    private LoginServiceImpl loginService;

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private TeacherServiceImpl teacherService;

    @Autowired
    private StudentServiceImpl studentService;

    @PostMapping("/logout")
    public ApiResult<?> logout() {
        // 采用无状态设计，前端删除本地会话即可
        return ApiResultHandler.buildApiResult(200, "退出成功", null);
    }

    @PostMapping("/register")
    public ApiResult<?> register(@RequestBody RegisterRequest request) {
        String validateMsg = validate(request);
        if (validateMsg != null) {
            return ApiResultHandler.buildApiResult(400, validateMsg, null);
        }
        if (
            loginService.countUserByContact(
                request.getCardId(),
                request.getTel(),
                request.getEmail()
            ) >
            0
        ) {
            return ApiResultHandler.buildApiResult(
                409,
                "证件号/手机号/邮箱已被占用，请检查后再提交",
                null
            );
        }

        String role = request.getRole();
        Map<String, Object> result = new HashMap<>(4);
        int affected = 0;
        if ("0".equals(role)) {
            Admin admin = new Admin();
            admin.setAdminName(request.getName());
            admin.setSex(request.getSex());
            admin.setTel(request.getTel());
            admin.setEmail(request.getEmail());
            admin.setPwd(request.getPwd());
            admin.setCardId(request.getCardId());
            admin.setRole("0");
            affected = adminService.add(admin);
            result.put("userId", admin.getAdminId());
        } else if ("1".equals(role)) {
            Teacher teacher = new Teacher();
            teacher.setTeacherName(request.getName());
            teacher.setSex(request.getSex());
            teacher.setTel(request.getTel());
            teacher.setEmail(request.getEmail());
            teacher.setPwd(request.getPwd());
            teacher.setCardId(request.getCardId());
            teacher.setRole("1");
            teacher.setInstitute(request.getInstitute());
            teacher.setType(request.getType());
            affected = teacherService.add(teacher);
            result.put("userId", teacher.getTeacherId());
        } else if ("2".equals(role)) {
            Student student = new Student();
            student.setStudentName(request.getName());
            student.setSex(request.getSex());
            student.setTel(request.getTel());
            student.setEmail(request.getEmail());
            student.setPwd(request.getPwd());
            student.setCardId(request.getCardId());
            student.setRole("2");
            student.setInstitute(request.getInstitute());
            student.setGrade(request.getGrade());
            student.setMajor(request.getMajor());
            student.setClazz(request.getClazz());
            affected = studentService.add(student);
            result.put("userId", student.getStudentId());
        }
        if (affected == 0) {
            return ApiResultHandler.buildApiResult(
                500,
                "注册失败，请稍后重试",
                null
            );
        }
        result.put("role", role);
        return ApiResultHandler.buildApiResult(200, "注册成功", result);
    }

    /**
     * 参数校验
     */
    private String validate(RegisterRequest request) {
        if (request == null) {
            return "注册信息不能为空";
        }
        if (
            !StringUtils.hasText(request.getRole()) ||
            !SUPPORT_ROLES.contains(request.getRole().trim())
        ) {
            return "请选择合法的用户角色";
        }
        if (!StringUtils.hasText(request.getName())) {
            return "姓名不能为空";
        }
        if (
            !StringUtils.hasText(request.getCardId()) ||
            request.getCardId().length() < 8
        ) {
            return "请输入有效的证件号";
        }
        if (
            !StringUtils.hasText(request.getPwd()) ||
            request.getPwd().length() < 6
        ) {
            return "密码至少6位";
        }
        if (!StringUtils.hasText(request.getTel())) {
            return "请输入联系电话";
        }
        if (!StringUtils.hasText(request.getEmail())) {
            return "请输入邮箱";
        }
        return null;
    }
}
