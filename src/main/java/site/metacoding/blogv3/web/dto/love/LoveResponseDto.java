package site.metacoding.blogv3.web.dto.love;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoveResponseDto {
    private Integer loveId;
    private PostDto post;

    // 모델 사용하면 LazyLoading 됨 !
    @Data
    public class PostDto {
        private Integer postId;
        private String title;
    }
}
