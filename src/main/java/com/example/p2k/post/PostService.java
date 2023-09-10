package com.example.p2k.post;

import com.example.p2k._core.exception.Exception404;
import com.example.p2k.course.Course;
import com.example.p2k.course.CourseRepository;
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

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CourseRepository courseRepository;

    //강좌 카테고리 별 게시글 찾기
    public PostResponse.FindPostsDTO findPostsByCategory(Long id, int page, Category category, User user){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate")); //정렬조건
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Post> posts = postRepository.findPostByCategory(pageable, id, category, user.getId());
        return new PostResponse.FindPostsDTO(posts);
    }

    //게시글 아이디로 게시글 찾기
    public PostResponse.FindPostByIdDTO findPostById(Long postId){
        Post post = postRepository.findById(postId).get();
        return new PostResponse.FindPostByIdDTO(post);
    }

    //게시글 작성하기
    @Transactional
    public void savePost(PostRequest.SaveDTO saveDTO, User user, Long courseId){
        Course course = courseRepository.findById(courseId).get();

        Post post = Post.builder()
                .title(saveDTO.getTitle())
                .author(user.getName())
                .content(saveDTO.getContent())
                .category(saveDTO.getCategory())
                .open(saveDTO.getOpen())
                .course(course)
                .user(user)
                .build();

        postRepository.save(post);
    }

    //게시글 수정하기
    @Transactional
    public void updatePost(PostRequest.UpdateDTO updateDTO, Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );
        if(post.getUser().getId().equals(user.getId())){
            postRepository.update(updateDTO.getTitle(), updateDTO.getContent(), updateDTO.getOpen(), postId);
        }
    }

    //게시글 삭제하기
    @Transactional
    public void deletePost(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );
        if(post.getUser().getId().equals(user.getId())){
            postRepository.deleteById(postId);
        }
    }
}
