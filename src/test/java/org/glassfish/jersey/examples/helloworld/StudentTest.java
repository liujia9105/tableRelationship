package org.glassfish.jersey.examples.helloworld;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by liujia on 3/15/16.
 */
public class StudentTest {
    private SqlSessionFactory sqlSessionFactory;
    private StudentRepository studentRepository;
    private SqlSession session;

    @Before
    public void setUp() throws IOException, SQLException {
        String resource = "mybatis.xml";

        Reader reader  = Resources.getResourceAsReader(resource);

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        session = sqlSessionFactory.openSession();
        session.getConnection().setAutoCommit(false);
        studentRepository = session.getMapper(StudentRepository.class);

    }
    @Test
    public void should_select_a_record_when_student_not_exsit(){
        Integer studentId=1;
        Student student=studentRepository.selectById(studentId);

        assertThat(student.getStudentName(),is("LJY"));
    }

    @Test
    public void should_update_a_record_when_klass_not_exsit(){
        Integer studentId=1;
        String studentName="TYW";
        Student student=studentRepository.selectById(studentId);
        student.setStudentName(studentName);
        studentRepository.updateStudent(student);

        assertThat( studentRepository.selectById(studentId).getStudentName(),is(studentName));
    }
    @Test
    public void should_create_a_record_when_klass_not_exsit(){
        Integer studentId=3;
        String studentName="LJ";

        Student student=new Student(studentId,studentName,null,null);
        studentRepository.insertStudent(student);

        assertThat(studentRepository.selectById(studentId).getStudentName(),is(studentName));
    }

//    @Test
//    public void should_delete_a_record_when_klass_not_exsit(){
//        Integer studentId=1;
//        studentRepository.deleteStudentById(studentId);
//        assertThat( studentRepository.selectById(studentId),is(nullValue()));
//    }

    @Test
    public void should_return_a_record_of_student_table_when_klass_table_exsit(){

        Integer studentId=1;
        String klassName="math";
        Student student=studentRepository.selectById(studentId);

        assertThat(student.getKlass().getKlassName(),is(klassName));
    }
    @Test
    public void should_create_relationship_with_klass(){

        Integer klassId=2;
        Integer studentId=2;
        studentRepository.updateRelationshipWithKlass(klassId,studentId);

        assertThat( studentRepository.selectById(studentId).getKlassId(),is(klassId));
    }
    @Test
    public void should_update_a_record_when_klass_exsit(){

        Integer klassId=2;
        Integer studentId=1;
        studentRepository.updateRelationshipWithKlass(klassId,studentId);

        assertThat( studentRepository.selectById(studentId).getKlassId(),is(klassId));
    }
    @Test
    public void should_select_a_record_when_teacher_exsit(){
        Integer studentId=1;
        Student student =studentRepository.selectById(studentId);

        assertThat(student.getTeachers().get(0).getTeacherId(),is(1));
    }
    @Test
    public void should_create_relationship_with_teacher(){

        Integer teacherId=2;
        Integer studentId=2;
        studentRepository.createRelationshipWithTeacher(teacherId,studentId);

        assertThat( studentRepository.selectById(studentId).getTeachers().get(0).getTeacherId(),is(teacherId));
    }
    @Test
    public void should_update_a_record_when_teacher_exsit(){

        Integer studentId=1;
        Integer teacherId=2;

        studentRepository.updateRelationshipWithTeacher(teacherId,studentId);

        assertThat( studentRepository.selectById(studentId).getTeachers().get(0).getTeacherId(),is(teacherId));;
    }
    @Test
    public void should_delete_relationship_with_klass(){
        Integer studentId=1;
        studentRepository.updateRelationshipWithKlass(null,studentId);
        assertThat( studentRepository.selectById(studentId).getKlassId(),is(nullValue()));
    }
    @Test
    public void should_delete_relationship_with_teacher(){
        Integer studentId=1;
        studentRepository.deleteRelationshipWithTeacher(studentId);
        assertThat( studentRepository.selectById(studentId),is(nullValue()));
    }
    @Test
    public void should_update_klass_and_student(){

        Integer studentId=1;
        Student student=studentRepository.selectById(studentId);
        String klassName="english";

        student.getKlass().setKlassName(klassName);
        studentRepository.updateStudent(student);

        assertThat(studentRepository.selectById(studentId).getKlass().getKlassName(),is(klassName));
    }
    @Test
    public void should_update_teachers_and_student(){

        Integer studentId=1;
        Student student=studentRepository.selectById(studentId);
        String teacherName="english";

        student.getTeachers().get(0).setTeacherName(teacherName);
        studentRepository.updateStudent(student);

        assertThat(studentRepository.selectById(studentId).getTeachers().get(0).getTeacherName(),is(teacherName));
    }

    @Test
    public void should_get_error_when_primary_key_create_as_null(){
        Integer studentId=null;
        Student student=new Student(studentId,"test",null,null);
        Boolean except=false;
        try {
            studentRepository.insertStudent(student);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
    @Test
    public void should_get_error_when_primary_key_create_not_unique(){
        Integer studentId=1;
        Student student=new Student(studentId,"test",null,null);
        Boolean except=false;
        try {
            studentRepository.insertStudent(student);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
    @Test
    public void should_get_error_when_create_foreign_not_exist_in_klass(){
        Integer studentId=3;
        Klass klass=new Klass(3,"english",null);
        Student student=new Student(studentId,"test",klass,null);
        Boolean except=false;
        try {
            studentRepository.insertStudent(student);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
    @Test
    public void should_get_error_when_update_foreign_not_exist_in_klass(){
        Integer studentId=2;
        Klass klass=new Klass(3,"english",null);
        Student student=new Student(studentId,"test",klass,null);
        Boolean except=false;
        try {
            studentRepository.updateStudent(student);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
    @Test
    public void should_get_error_when_delete(){
        Integer studentId=1;
        Boolean except=false;
        try {
            studentRepository.deleteStudentById(studentId);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }

    @Test
    public void should_get_error_when_create_relationship_when_student_primary_key_not_exist(){

        Integer teacherId=1;
        Integer studentId=3;

        Boolean except=false;
        try {
            studentRepository.createRelationshipWithTeacher(teacherId,studentId);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
    @Test
    public void should_get_error_when_update_relationship_when_teacher_primary_key_not_exist(){

        Integer teacherId=3;
        Integer studentId=1;

        Boolean except=false;
        try {
            studentRepository.updateRelationshipWithTeacher(teacherId,studentId);
        }catch (PersistenceException ex){
            except=true;
        }
        assertThat( except,is(true));
    }
}
