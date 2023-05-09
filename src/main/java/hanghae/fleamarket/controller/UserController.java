package hanghae.fleamarket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import hanghae.fleamarket.config.GoogleConfigUtils;
import hanghae.fleamarket.dto.*;
import hanghae.fleamarket.jwt.JwtUtil;
import hanghae.fleamarket.service.GoogleService;
import hanghae.fleamarket.service.KakaoService;
import hanghae.fleamarket.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Controller
//@CrossOrigin(origins = "*", originPatterns = "*") // 컨트롤러에서 설정
//@CrossOrigin(originPatterns = "http://localhost:3000") //cors 설정
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final GoogleConfigUtils googleConfigUtils;

    //회원가입 페이지
    @GetMapping("/signup")
    public ModelAndView signupPage() {
        return new ModelAndView("signup");
    }

    @ResponseBody
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        log.info("signup 실행");
        userService.signup(signupRequestDto);
        return "success";
    }

    //로그인 페이지
    @GetMapping("/login-page")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

    //로그인 성공 후 홈페이지로 이동
    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        log.info("login 실행");
        userService.login(loginRequestDto, response);
        return "success";
    }

    //아이디 중복검사
    @ResponseBody
    @PostMapping("/doubleCheck")
    public boolean loginDoubleCheck(@RequestBody LoginDoubleCheckDto loginDoubleCheckDto){
        return userService.loginDoubleCheck(loginDoubleCheckDto);
    }

    //카카오 로그인
    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response, RedirectAttributes re) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        String createToken = kakaoService.kakaoLogin(code, response);

        // Cookie 생성 및 직접 브라우저에 Set, 서버에서 쿠키를 쿠키저장소에 넣어줌
        //키값                              밸류값 , substring(bearer과 공백을 삭제)
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        re.addAttribute("Authorization", createToken);

        return "redirect:http://localhost:3000";
    }

    @GetMapping(value = "/kakao/login")
    public String kakoRedirect(){
        return "redirect:/api/products";
    }

    //구글 로그인 인증토큰
    @GetMapping(value = "/google/logins")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = googleConfigUtils.googleInitUrl();
        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    //구글 로그인 redirect
    @GetMapping(value = "/google/login/redirect")
    public String redirectGoogleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse response, RedirectAttributes re) {
        String jwt = googleService.redirectGoogleLogin(authCode);

         Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, jwt.substring(7));
         cookie.setPath("/");
         response.addCookie(cookie);

        re.addAttribute("Authorization", jwt);

        return "redirect:http://localhost:3000";
    }

    //접근 제한
    @GetMapping("/forbidden")
    public ModelAndView getForbidden() {
        return new ModelAndView("forbidden");
    }

    //접근 제한
    @PostMapping("/forbidden")
    public ModelAndView postForbidden() {
        return new ModelAndView("forbidden");
    }

    @ResponseBody
    @GetMapping("/info")
    public UserResponseDto getUserInfo(HttpServletRequest request) {
        return userService.getUserInfo(request);
    }

    @ResponseBody
    @GetMapping("/mypage")
    public List<MyPageDto> getMyPage(HttpServletRequest request) {
        List<MyPageDto> myPage = userService.getMyPage(request);
        log.info("마이페이지 !!! {} ", myPage.size());
        return myPage;
    }
}