package com.example.p2k.course;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception403;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k.courseuser.CourseUser;
import com.example.p2k.courseuser.CourseUserRepository;
import com.example.p2k.post.PostRepository;
import com.example.p2k.reply.ReplyRepository;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
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

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CourseService {
    private final ReplyRepository replyRepository;

    public static final int DEFAULT_PAGE_SIZE = 10;

    private final CourseRepository courseRepository;
    private final CourseUserRepository courseUserRepository;
    private final PostRepository postRepository;
    private final VmRepository vmRepository;
    private final UserRepository userRepository;

    //강좌 아이디로 강좌 조회
    public CourseResponse.FindById findCourse(Long id){
        Course course = getCourse(id);
        return new CourseResponse.FindById(course);
    }

    //사용자 아이디로 강좌 목록 조회
    public CourseResponse.VmCoursesDTO findCourses(Long userId) {
        List<Course> courses = courseUserRepository.findByUserIdAndAcceptIsTrue(userId);
        return new CourseResponse.VmCoursesDTO(courses);
    }

    //나의 강좌 조회
    public CourseResponse.FindCoursesDTO findCourses(Long userId, int page){
        Pageable pageable = getPageable(page);
        Page<Course> courses = courseUserRepository.findByUserIdAndAcceptIsTrue(pageable, userId);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청 페이지
    public CourseResponse.FindCoursesDTO findSearchCourses(String keyword, int page){
        Pageable pageable = getPageable(page);
        Page<Course> courses = courseRepository.findByNameContaining(pageable, keyword);
        return new CourseResponse.FindCoursesDTO(courses);
    }

    //강좌 신청
    @Transactional
    public void apply(Long courseId, Long userId){
        User user = getUser(userId);
        checkStudentAuthorization(user);

        courseUserRepository.findByCourseIdAndUserId(courseId, userId)
                .ifPresent(courseUser -> {
                    throw new Exception400("이미 신청한 강좌입니다.");
                });

        Course course = getCourse(courseId);
        CourseUser courseUser = CourseUser.builder().course(course).user(user).accept(false).build();
        courseUserRepository.save(courseUser);
    }

    //나의 가상 환경 조회
    public VmResponse.FindAllDTO findMyVm(User user, Long id){
        List<Vm> vms = vmRepository.findUserIdAndCourseId(user.getId(), id); //TODO: user 찾아야 할 듯?
        return new VmResponse.FindAllDTO(vms);
    }

    //교육자의 가상 환경 조회
    public VmResponse.FindAllDTO findInstructorVm(Long courseId, User user){
        checkStudentAuthorization(user);

        Course course = getCourse(courseId);

        if(course.getInstructorId() == null){
            throw new Exception400("해당 강좌의 교육자가 존재하지 않습니다.");
        }

        List<Vm> vms = vmRepository.findUserIdAndCourseIdOpen(course.getInstructorId(), courseId);
        return new VmResponse.FindAllDTO(vms);
    }

    //강좌 취소
    @Transactional
    public void cancel(Long courseId, Long userId){
        User user = getUser(userId);
        checkStudentAuthorization(user);
        courseUserRepository.deleteByCourseIdAndUserId(courseId, userId);
    }

    //강좌 생성
    @Transactional
    public void create(CourseRequest.SaveDTO requestDTO, User user){
        checkInstructorAuthorization(user);

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
        checkInstructorAuthorization(user);
        List<User> students = courseUserRepository.findAcceptedUserByCourseId(id);
        return new CourseResponse.FindStudentsDTO(students);
    }

    //강좌 신청 대기 수강생 목록
    public CourseResponse.FindUnacceptedUserDTO findApplications(Long courseId, User user){
        checkInstructorAuthorization(user);
        List<User> users = courseUserRepository.findUnacceptedUserByCourseId(courseId);
        return new CourseResponse.FindUnacceptedUserDTO(users);
    }

    //강좌 신청 수락
    @Transactional
    public void accept(Long courseId, Long applicantId, User user){
        checkInstructorAuthorization(user);
        courseUserRepository.updateAccept(courseId, applicantId);
    }

    //강좌 신청 거절
    @Transactional
    public void reject(Long courseId, Long applicantId, User user){
        checkInstructorAuthorization(user);
        courseUserRepository.deleteByCourseIdAndUserId(courseId, applicantId);
    }

    //강좌 삭제
    @Transactional
    public void delete(Long courseId, User user){
        checkInstructorAuthorization(user);
        Course course = getCourse(courseId);
        courseUserRepository.deleteByCourseId(course.getId());
        postRepository.deleteByCourseId(course.getId());
        courseRepository.deleteById(course.getId());
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(
                () -> new Exception404("해당 강좌를 찾을 수 없습니다.")
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
    }

    private static void checkInstructorAuthorization(User user) {
        if(user.getPending() || user.getRole() != Role.ROLE_INSTRUCTOR){
            throw new Exception403("교육자 권한이 없는 사용자입니다.");
        }
    }

    private static void checkStudentAuthorization(User user) {
        if(user.getRole() != Role.ROLE_STUDENT){
            throw new Exception403("학생 권한이 없는 사용자입니다.");
        }
    }

    private static Pageable getPageable(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        return PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(sorts));
    }
}