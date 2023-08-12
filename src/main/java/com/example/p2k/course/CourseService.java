package com.example.p2k.course;

import com.example.p2k.courseuser.CourseUser;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseUserRepository courseUserRepository;
    private final PostRepository postRepository;

    //나의 강좌 조회
    public CourseResponse.FindCoursesDTO findCourses(Long userId){
        List<Course> courses = courseRepository.findByUserId(userId);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청 페이지
    public CourseResponse.FindCoursesDTO findAll(){
        List<Course> courses = courseRepository.findAll();
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청
    @Transactional
    public void apply(Long id, User user){

        Optional<CourseUser> findCourseUser = courseUserRepository.findByUserIdAndCourseId(user.getId(), id);
        if(findCourseUser.isPresent()){
            //이미 신청한 강좌인지 체크
        }

        Optional<Course> course = courseRepository.findById(id);
        if(course.isPresent()){
            CourseUser courseUser = CourseUser.builder().course(course.get()).user(user).build();
            courseUserRepository.save(courseUser);
        }
    }

    //나의 가상 환경 조회
    public List<CourseResponse.FindMyVmDTO> findMyVm(Long id){
        return new ArrayList<>();
    }

    //관리자의 가상 환경 조회
    public List<CourseResponse.FindInstructorVmDTO> findInstructorVm(Long id){
        return new ArrayList<>();
    }

    //강좌 취소
    @Transactional
    public void cancel(Long id, User user){
        courseUserRepository.deleteByUserIdAndCourseId(user.getId(), id);
    }

    //강좌 생성
    @Transactional
    public void create(CourseRequest.SaveDTO requestDTO, User user){
        Course course = Course.builder().name(requestDTO.getName()).description(requestDTO.getDescription()).build();
        courseRepository.save(course);

        CourseUser courseUser = CourseUser.builder().course(course).user(user).build();
        courseUserRepository.save(courseUser);
    }

    //수강생 관리
    public CourseResponse.FindStudentsDTO findStudents(Long id){
        List<User> students = courseRepository.findAllUserByCourseId(id);
        return new CourseResponse.FindStudentsDTO(students);
    }

    //강좌 삭제
    @Transactional
    public void delete(Long id){
        courseUserRepository.deleteByCourseId(id);
        postRepository.deleteAllByCourseId(id);
        courseRepository.deleteById(id);
    }

    //강좌 아이디로 강좌 찾기
    public CourseResponse.FindById findById(Long id){
        Course course = courseRepository.findById(id).get();
        return new CourseResponse.FindById(course);
    }
}
