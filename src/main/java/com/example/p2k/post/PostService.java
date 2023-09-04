package com.example.p2k.post;

import com.example.p2k.course.Course;
import com.example.p2k.course.CourseRepository;
import com.example.p2k.course.CourseRequest;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.user.User;
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
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CourseRepository courseRepository;

    //강좌 카테고리 별 게시글 찾기
    public PostResponse.FindPostsDTO findPostsByCategory(Long id, int page, Category category){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate")); //정렬조건
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Post> posts = postRepository.findPostByCategory(pageable, id, category);
        return new PostResponse.FindPostsDTO(posts);
    }

    //게시글 아이디로 게시글 찾기
    public PostResponse.FindPostByIdDTO findPostById(Long postId){
        Post post = postRepository.findById(postId).get();
        log.info("post=" + post.getUser().getName());
        return new PostResponse.FindPostByIdDTO(post);
    }

    //게시글 작성하기
    @Transactional
    public void savePost(CourseRequest.PostDTO postDTO, User user, Long courseId){
        Course course = courseRepository.findById(courseId).get();

        Post post = Post.builder()
                .title(postDTO.getTitle())
                .author(user.getName())
                .content(postDTO.getContent())
                .category(postDTO.getCategory())
                .course(course)
                .user(user)
                .build();

        postRepository.save(post);
    }

    //게시글 수정하기
    @Transactional
    public void updatePost(CourseRequest.PostDTO postDTO, Long postId, User user){
        Post post = postRepository.findById(postId).get();
        if(post.getUser().getId().equals(user.getId())){
            postRepository.update(postDTO.getTitle(), postDTO.getContent(), postId);
        }
    }

    //게시글 삭제하기
    @Transactional
    public void deletePost(Long postId, User user){
        Post post = postRepository.findById(postId).get();
        if(post.getUser().getId().equals(user.getId())){
            postRepository.deleteById(postId);
        }
    }
}
