package kr.go.museum.dino.smartapp.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.jsonwebtoken.ExpiredJwtException;
//import kr.go.museum.dino.smartapp.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import kr.go.museum.dino.smartapp.config.JwtTokenProvider;
import kr.go.museum.dino.smartapp.model.Token;
import kr.go.museum.dino.smartapp.model.User;
import kr.go.museum.dino.smartapp.model.UserId;
import kr.go.museum.dino.smartapp.repository.UserRepository;
import kr.go.museum.dino.smartapp.repository.VersionRepository;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserRepository userRepository;
	private final VersionRepository versionRepository;
    private final JwtTokenProvider jwtTokenProvider;
	
	// 회원가입
	@PostMapping("/join")
	public Map<String, String> join (@RequestBody Map<String, String> user) {
		return userService.join(user);
	}

	// 로그인
	@PostMapping("/login")
	public Map<String, Object> login (@RequestBody Map<String, String> pUser, HttpServletResponse response) {
		return userService.login(pUser, response);
	}
	
	@GetMapping("/user")
	public Map<String, Object> findByEmail(HttpServletRequest request) {
		return userService.findByEmail(request);
	}

	// 로그아웃
	@GetMapping("/logout")
	public Map<String, String> logout (HttpServletResponse response) {
		return userService.logout(response);
	}

	// 비밀번호 변경	
	@PostMapping("/change-password")
	public Map<String, String> changePassword (@RequestBody Map<String, String> user) {
		return userService.changePassword(user);
	}

	// 게스트 계정 생성
	@GetMapping("/guest-join")
	public Map<String, String> guestJoin () {
		return userService.guestJoin();
	}
	
	// 회원 계정 연동
	@PostMapping("/login-link")
	public Map<String, String> loginLink (@RequestBody Map<String, Object> user, HttpServletResponse response) {
		return userService.loginLink(user, response);
	}

	// 비밀번호 변경 화면
	@GetMapping("/change-password-form")
	public ModelAndView changePasswordForm() {
		return new ModelAndView("changePassword");
	}
	
	// 인증 번호 발송
	@GetMapping("/email-send")
	public Map<String, String> emailSend (@RequestParam("pid") String pid, HttpServletRequest request) {
		return userService.mailSend(pid, request);
	}
	
	// 인증 번호로 비밀번호 변경
	@PostMapping("/email-change-password")
	public Map<String, String> emailChangePassword (@RequestBody Map<String, Object> user, HttpServletRequest request) {
		return userService.mailChangePassword(user, request);
	}
	
	// 이용약관 확인 체크
	@GetMapping("/tos-check")
	public Map<String, String> tosCheck (@RequestParam("email") String email) {
		return userService.tosCheck(email);
	}
}