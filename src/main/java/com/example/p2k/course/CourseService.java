package com.example.p2k.course;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception403;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k.courseuser.CourseUser;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.Vm;
import com.example.p2k.vm.VmRepository;
import com.example.p2k.vm.VmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final VmRepository vmRepository;

    public CourseResponse.CoursesDTO findCourses(Long userId) {
        List<Course> courses = courseUserRepository.findCourseByUserId(userId);
        return new CourseResponse.CoursesDTO(courses);
    }

    //나의 강좌 조회
    public CourseResponse.FindCoursesDTO findCourses(Long userId, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id")); //정렬조건
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Course> courses = courseUserRepository.findCourseByUserId(pageable, userId);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청 페이지
    public CourseResponse.FindCoursesDTO findAll(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id")); //정렬조건
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Course> courses = courseRepository.findAll(pageable);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청
    @Transactional
    public void apply(Long id, User user){
        Optional<CourseUser> findCourseUser = courseUserRepository.findByCourseIdAndUserId(id, user.getId());
        if(findCourseUser.isPresent()){
            throw new Exception400("이미 신청한 강좌입니다.");
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
    public CourseResponse.FindCoursesDTO findBySearch(String keyword, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id")); //정렬조건
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Course> courses = courseRepository.findByNameContaining(pageable, keyword);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //나의 가상 환경 조회
    public VmResponse.FindAllDTO findMyVm(User user, Long id){
        List<Vm> vms = vmRepository.findUserIdAndCourseId(user.getId(), id);
        return new VmResponse.FindAllDTO(vms);
    }

    //교육자의 가상 환경 조회
    public VmResponse.FindAllDTO findInstructorVm(Long id){
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 강좌를 찾을 수 없습니다.")
        );

        if(course.getInstructorId() == null){
            throw new Exception400("해당 강좌의 교육자가 존재하지 않습니다.");
        }

        List<Vm> vms = vmRepository.findUserIdAndCourseIdOpen(course.getInstructorId(), id);
        return new VmResponse.FindAllDTO(vms);
    }

    //강좌 취소
    @Transactional
    public void cancel(Long id, User user){
        courseUserRepository.deleteByCourseIdAndUserId(id, user.getId());
    }

    //강좌 생성
    @Transactional
    public void create(CourseRequest.SaveDTO requestDTO, User user){
        if(user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("권한이 없는 사용자입니다.");
        }

        Course course = Course.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .instructorId(user.getId())
                .build();
        courseRepository.save(course);

        CourseUser courseUser = CourseUser.builder().course(course).user(user).accept(true).build();
        courseUserRepository.save(courseUser);
    }

    //수강생 관리
    public CourseResponse.FindStudentsDTO findStudents(Long id, User user){
        if(user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("권한이 없는 사용자입니다.");
        }
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
    public void accept(Long courseId, Long userId, User user){
        if(!user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("권한이 없는 사용자입니다.");
        }
        courseUserRepository.updateAccept(courseId, userId);
    }

    //강좌 신청 거절
    @Transactional
    public void reject(Long courseId, Long userId, User user){
        if(user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("권한이 없는 사용자입니다.");
        }
        courseUserRepository.deleteByCourseIdAndUserId(courseId, userId);
    }

    //강좌 삭제
    @Transactional
    public void delete(Long id, User user){
        if(user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("권한이 없는 사용자입니다.");
        }
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