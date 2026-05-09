package com.student.dao;

import com.student.model.Student;
import java.util.List;

public interface StudentDao {
    List<Student> findAll();
    Student findById(int id);
    Student findByStudentNo(String studentNo);
    boolean create(Student student);
    boolean update(Student student);
    boolean delete(int id);
    List<Student> search(String keyword);
}
