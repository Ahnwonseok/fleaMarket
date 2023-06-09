package hanghae.fleamarket.controller;

import hanghae.fleamarket.dto.ProductRequestDto;
import hanghae.fleamarket.dto.ProductResponseDto;
import hanghae.fleamarket.service.S3Service;
import hanghae.fleamarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final S3Service s3Service;

    //게시글 전체 조회
    @GetMapping("/api/products")
    public List<ProductResponseDto> findAllProducts() {
        return productService.findAllProducts();
    }

    //게시글 단일 조회
    @GetMapping("/api/products/{productId}")
    public ProductResponseDto findProduct(@PathVariable Long productId) {
        log.info("product add 실행");
        return productService.getProduct(productId);
    }


//    @ResponseBody
//    @PostMapping(value = "/api/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ProductResponseDto createProduct(ProductRequestDto dto, HttpServletRequest request) throws IOException {
//        //String imgUrl = s3Service.upload(image);
//        return productService.createProduct(dto, request);
//    }

    //게시글 등록
    @ResponseBody
    @PostMapping(value = "/api/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDto createProduct(@RequestParam MultipartFile image, ProductRequestDto dto, HttpServletRequest request) throws IOException {
        String imgUrl = s3Service.upload(image);
        return productService.createProduct(dto, request, imgUrl);
    }

    //게시글 수정
    @PatchMapping(value = "/api/products/{productId}" /*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE*/)
    public ProductResponseDto updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDto requestDto, HttpServletRequest request) throws IOException {
        return productService.update(productId, requestDto, request);
    }

    //게시글 삭제
    @DeleteMapping("/api/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        return productService.deleteProduct(productId, request);
    }


}
