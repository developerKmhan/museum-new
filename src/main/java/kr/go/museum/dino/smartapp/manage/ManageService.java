package kr.go.museum.dino.smartapp.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.go.museum.dino.smartapp.config.PropertyInitializer;
import kr.go.museum.dino.smartapp.model.Manage;
import kr.go.museum.dino.smartapp.model.UserId;
import kr.go.museum.dino.smartapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManageService {
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    EntityManagerFactory emf;
    EntityManager em;
	
	void setEntityManager() {
		emf = Persistence.createEntityManagerFactory("jpabook", PropertyInitializer.prop);
		em = emf.createEntityManager();
	}
    
	public HashMap<String, Object> manageLogin(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();
				
		String email = request.getParameter("email");
		Boolean existGubun = userRepository.existsByEmailAndAdminYn(email, true);
		
		if (existGubun) {
			setEntityManager();
			
			String jpql = "SELECT password FROM tb_user "
	                + "WHERE email = ? "
	                + "AND admin_yn = ? ";
			List lst = em.createNativeQuery(jpql)
					.setParameter(1, email)
					.setParameter(2, true)
					.setHint("org.hibernate.readOnly", true)
					.getResultList();
			//User user = userRepository.findByEmailAndAdminYn(request.getParameter("email"), true);
			
			try {
				if(!passwordEncoder.matches(request.getParameter("password"),lst.get(0).toString())) {
					map.put("message", "잘못된 비밀번호입니다.");
					map.put("loginGubun", false);
				} else {
					// 자동 로그인 대기
					Cookie loginCookie;
					if (request.getParameter("remember") != null && request.getParameter("remember").equalsIgnoreCase("true")) {
		                //loginCookie라는 키로 세션아이디를 담아 쿠키를 생성합니다.
		                loginCookie = new Cookie("loginCookie",email); 
		                loginCookie.setPath("/");//쿠키의 저장경로는 기본 uri 경로 홈페이지 시작uri
		                long limitTime = 60*60*24*90; //90일의 시간을 저장
		                loginCookie.setMaxAge((int) limitTime);//초단위로 쿠키유지시간 설정
					} else {
					    loginCookie = new Cookie("loginCookie", null); // choiceCookieName(쿠키 이름)에 대한 값을 null로 지정
					    loginCookie.setMaxAge(0); // 유효시간을 0으로 설정
					}
					//쿠키는 클라이언트에 보낼떄 응답객체에 담아서 보냅니다.
					response.addCookie(loginCookie); // 응답 헤더에 추가해서 없어지도록 함
					
					map.put("loginGubun", true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				em.close();
				emf.close();
			}
		} else {
			map.put("message", "아이디를 다시 입력해주세요.");
			map.put("loginGubun", false);
		}
		return map;
	}
	
	public Map<String, String> monthUserList() {
		HashMap<String, String> map = new HashMap<String, String>();

		setEntityManager();
		List<Manage> cntList = em.createNamedQuery("findByMonthUserCntList").getResultList();
		em.close();
		emf.close();
		String cntStr = "";
		String ymStr = "";
				
		for(int i=0;i<cntList.size();i++) {
			cntStr = (i==0)?cntList.get(i).getCnt():cntStr+","+cntList.get(i).getCnt();
			ymStr = (i==0)?cntList.get(i).getYm():ymStr+","+cntList.get(i).getYm();
		}
		map.put("cntList", cntStr);
		map.put("ymList", ymStr);
		return map;
	}

	public List userIdList() {
		setEntityManager();
		List<UserId> resultList = em.createNamedQuery("findUserIdList").getResultList();
		em.close();
		emf.close();
		return resultList;
	}
}
