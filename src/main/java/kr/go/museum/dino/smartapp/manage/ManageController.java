package kr.go.museum.dino.smartapp.manage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class ManageController {
	private final ManageService manageService;
	
	// 관리자 로그인 화면
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView manage() { 
		return new ModelAndView("login");
	}

	// 관리자 로그인
	@PostMapping("/manage-login")
	public ModelAndView manageLogin(HttpServletRequest request, Model model, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = manageService.manageLogin(request, response);
		Boolean successGubun = (Boolean) map.get("loginGubun");
		ModelAndView mav;
		if (successGubun) {
			mav = new ModelAndView("index");
		} else {
			model.addAttribute("error", true);
			model.addAttribute("exception", map.get("message"));

			mav = new ModelAndView("login");
		}
		return mav;
	}
	
	// 관리자 로그아웃
	@RequestMapping(value = "/manage-logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		Cookie loginCookie = new Cookie("loginCookie", null); // choiceCookieName(쿠키 이름)에 대한 값을 null로 지정
	    loginCookie.setMaxAge(0); // 유효시간을 0으로 설정
		response.addCookie(loginCookie); // 응답 헤더에 추가해서 없어지도록 함
		
		return new ModelAndView("login");
	}

	// 관리자 가입자 월별 통계
	@GetMapping("/subscriber")
	public Map<String,String> subscriber(Model model) {
		return manageService.monthUserList();
	}
	
	// 관리자 메인 화면
	@RequestMapping("/manage-month-user")
	public ModelAndView monthUserPage() {
		// 토큰 값의 여부에 따라 접근 판단하도록 변경 예정
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}
	
	// 가입자 목록 화면
	@RequestMapping("/manage-user-id")
	public ModelAndView userIdPage(Model model) {
		model.addAttribute("userList", manageService.userIdList());
		ModelAndView mav = new ModelAndView("userId");
		return mav;	
	}
}
