package hanghae.fleamarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequestDto {

    private Long productId;
    private Long userId;
}