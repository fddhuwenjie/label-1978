package com.student.util;

import com.student.exception.ValidationException;
import com.student.model.Student;

public class ValidationUtil {
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    public static void validateStudent(Student student) {
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            throw new ValidationException("学号不能为空");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new ValidationException("姓名不能为空");
        }
        if (student.getAge() != null && (student.getAge() < 0 || student.getAge() > 150)) {
            throw new ValidationException("年龄必须在0-150之间");
        }
        if (student.getGender() != null && !student.getGender().isEmpty()) {
            if (!student.getGender().equals("男") && !student.getGender().equals("女")) {
                throw new ValidationException("性别只能是男或女");
            }
        }
        if (student.getPhone() != null && !student.getPhone().isEmpty()) {
            if (!student.getPhone().matches(PHONE_REGEX)) {
                throw new ValidationException("手机号格式不正确");
            }
        }
    }
}
