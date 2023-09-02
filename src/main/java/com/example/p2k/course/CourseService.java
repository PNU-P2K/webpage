package com.example.p2k.course;

import com.example.p2k._core.exception.Exception404;
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
        List<Course> courses = courseUserRepository.findCourseByUserId(userId);
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
        Optional<CourseUser> findCourseUser = courseUserRepository.findByCourseIdAndUserId(user.getId(), id);
        if(findCourseUser.isPresent()){
            //이미 신청한 강좌인지 체크
        }

        Optional<Course> course = courseRepository.findById(id);
        if(course.isPresent()){
            CourseUser courseUser = CourseUser.builder()
                    .course(course.get())
                    .user(user)
                    .accept(false)
                    .build();
            courseUserRepository.save(courseUser);
        }
    }

    //강좌 검색
    public CourseResponse.FindCoursesDTO findBySearch(String keyword){

        List<Course> courses = courseRepository.findByNameContaining(keyword);
        return new CourseResponse.FindCoursesDTO(courses);
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
        courseUserRepository.deleteByCourseIdAndUserId(id, user.getId());
    }

    //강좌 생성
    @Transactional
    public void create(CourseRequest.SaveDTO requestDTO, User user){
        Course course = Course.builder().name(requestDTO.getName()).description(requestDTO.getDescription()).build();
        courseRepository.save(course);

        CourseUser courseUser = CourseUser.builder().course(course).user(user).accept(true).build();
        courseUserRepository.save(courseUser);
    }

    //수강생 관리
    public CourseResponse.FindStudentsDTO findStudents(Long id){
        List<User> students = courseUserRepository.findAcceptedUserByCourseId(id);
        return new CourseResponse.FindStudentsDTO(students);
    }

    //강좌 신청 대기 수강생 목록
    public CourseResponse.FindUnacceptedUserDTO findApplications(Long id){
        List<User> users = courseUserRepository.findUnacceptedUserByCourseId(id);
        return new CourseResponse.FindUnacceptedUserDTO(users);
    }

    //강좌 신청 수락
    @Transactional
    public void accept(Long courseId, Long userId){
        courseUserRepository.updateAccept(courseId, userId);
    }

    //강좌 신청 거절
    @Transactional
    public void reject(Long courseId, Long userId){
        courseUserRepository.deleteByCourseIdAndUserId(courseId, userId);
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
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 강좌를 찾을 수 없습니다.")
        );
        return new CourseResponse.FindById(course);
    }
}
