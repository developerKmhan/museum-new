package kr.go.museum.dino.smartapp.user;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.Random;

import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.go.museum.dino.smartapp.common.Common;
import kr.go.museum.dino.smartapp.config.JwtTokenProvider;
import kr.go.museum.dino.smartapp.config.PropertyInitializer;
import kr.go.museum.dino.smartapp.repository.DinoRepository;
import kr.go.museum.dino.smartapp.repository.FossilRepository;
import kr.go.museum.dino.smartapp.model.Dino;
import kr.go.museum.dino.smartapp.model.Fossil;
import kr.go.museum.dino.smartapp.model.Token;
import kr.go.museum.dino.smartapp.model.User;
import kr.go.museum.dino.smartapp.model.UserId;
import kr.go.museum.dino.smartapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final FossilRepository fossilRepository;
	private final DinoRepository dinoRepository;
	private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;
	
	void setEntityManager() throws FileNotFoundException, IOException {
		emf = Persistence.createEntityManagerFactory("jpabook", PropertyInitializer.prop);
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}
	
    // 회원 가입
	public Map<String, String> join(Map<String, String> pUser) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String email = pUser.get("email");
		if (userRepository.existsByEmail(email)) {
			resultMap.put("result", "fail");
			resultMap.put("message", "이미 가입된 이메일입니다.");
		} else {
			try {
				setEntityManager();
				tx.begin();

				// id 가입
				em.persist(User.builder()
		                .email(email)
		                .name(pUser.get("name"))
		                .password(passwordEncoder.encode(pUser.get("password")))
		                .phone(passwordEncoder.encode(pUser.get("phone")))
		                .regDate(Common.todayYmd())
		                .build());

				Dino dino = Dino.builder()
						.pid(email)
		                .dinoNumArr("")
		                .regDate(Common.todayYmd())
		                .build();
				
				if(dinoRepository.existsByPid(email)) {
					em.merge(dino);
				} else {
					em.persist(dino);
				}
				
				tx.commit();
				
				resultMap.put("result", "ok");
			} catch (Exception e) {
				resultMap.put("result", "fail");
				resultMap.put("message", e.getMessage());
			} finally {
				em.close();
				emf.close();
			}
		}
		
		return resultMap;
	}

	// 로그인
	public Map<String, Object> login (Map<String, String> pUser, HttpServletResponse response) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		try {
			setEntityManager();
			
			String email = pUser.get("email");
			if (userRepository.existsByEmail(email)) {
				String jpql = "SELECT u FROM User u WHERE u.email = :email";
				Query nativeQuery = em.createQuery(jpql, User.class)
				 .setParameter("email", email);
				User user = (User) nativeQuery.getSingleResult();
				String name = user.getName();
				String password = user.getPassword();
				Boolean delYn = user.getDelyn();
				String phone = user.getPhone();
				
				Boolean guestGubun;
				try {
					guestGubun = name.equals("GUEST");
				} catch (Exception e) {
					guestGubun = false;
				}
				
				if (guestGubun) {
					// 게스트 계정은 패스
				} else if (!passwordEncoder.matches(pUser.get("password"), password)) {
					resultMap.put("result", "fail");
					resultMap.put("message", "잘못된 비밀번호입니다.");
					em.close();
					emf.close();
					return resultMap;
				} else if (delYn != null && delYn) {
					resultMap.put("result", "fail");
					resultMap.put("message", "삭제된 계정입니다.");
					em.close();
					emf.close();
					return resultMap;
				}
				
				// email과 password 일치 확인
				if (!userRepository.existsByEmailAndPassword(email, password)) {
					resultMap.put("result", "fail");
					resultMap.put("message", "계정정보를 다시 입력해주세요.");
					em.close();
					emf.close();
					return resultMap;
				}
				// 게스트 계정이 아니면 로그인 했다고 처리하기
				else if (!guestGubun) {
					// 자동 로그인 대기
					Cookie loginCookie;
					if (pUser.get("remember") != null && pUser.get("remember").equalsIgnoreCase("true")) {
		                //loginCookie라는 키로 세션아이디를 담아 쿠키를 생성합니다.
		                loginCookie = new Cookie("loginCookie", email); 
		                loginCookie.setPath("/"); // 쿠키의 저장경로는 기본 uri 경로 홈페이지 시작uri
		                long limitTime = 60*60*24*90; // 90일의 시간을 저장
		                loginCookie.setMaxAge((int) limitTime); // 초단위로 쿠키유지시간 설정
					} else {
					    loginCookie = new Cookie("loginCookie", null); // choiceCookieName(쿠키 이름)에 대한 값을 null로 지정
					    loginCookie.setMaxAge(0); // 유효시간을 0으로 설정
					}
					//쿠키는 클라이언트에 보낼떄 응답객체에 담아서 보냅니다.
					response.addCookie(loginCookie); // 응답 헤더에 추가해서 없어지도록 함
					
					tx.begin();
					// 객체를 생성만 한 상태 (비영속)
					user = em.find(User.class, email);
					
					user.setLoginYn(true);
					user.setModDate(Common.todayYmd());

					em.merge(user);
					tx.commit();
					
					// 회원명은 겹칠 수 있어서 수정
					String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
					response.setHeader("X-AUTH-TOKEN", token);
					if(token != "") {
						resultMap.put("result", "ok");
						resultMap.put("token", token);

						HashMap<String, Object> userMap = new HashMap<String, Object>();
						userMap.put("email", user.getEmail());
						userMap.put("name", user.getName());
						userMap.put("checkYn", user.getCheckYn());
						
						resultMap.put("user", userMap);
					} else {
						resultMap.put("result", "fail");
						resultMap.put("message", "토큰을 생성하지 못했습니다.");
					}
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "가입되지 않은 이메일입니다.");
			}
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		} finally {
			em.close();
			emf.close();
		}
		return resultMap;
	}

	// 토큰 로그인
	public Map<String, Object> findByEmail(HttpServletRequest request) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		String token = request.getHeader("X-AUTH-TOKEN");
		
		Boolean boolData = jwtTokenProvider.checkClaim(token);
		if(boolData) {
			String email = jwtTokenProvider.getUserPk(token);
			if(email != "") {
				if(userRepository.existsByEmail(email)) {
					User user = userRepository.findByEmail(email);
		
					resultMap.put("result", "ok");
		
					HashMap<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("email", user.getEmail());
					userMap.put("name", user.getName());
					userMap.put("checkYn", user.getCheckYn());
					
					resultMap.put("user", userMap);
				} else {
					resultMap.put("result", "fail");
					resultMap.put("message", "토큰에 해당하는 유저 정보가 없습니다.");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "토큰에 저장된 이메일이 없습니다.");
			}
		} else {
			resultMap.put("result", "fail");
			resultMap.put("message", "자동로그인 시간이 만료되었습니다.\n다시 로그인을 진행해 주세요.");
		}
		return resultMap;
	}
	
	// 로그아웃
	public Map<String, String> logout (HttpServletResponse response) {
		HashMap<String, String> resultMap = new HashMap<String, String>();

		try {
			response.setHeader("X-AUTH-TOKEN", null);
			resultMap.put("result", "ok");
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", "token delete fail");
		}
		return resultMap;
	}
	
	// 비밀번호 변경
	public Map<String, String> changePassword(Map<String, String> user) {
		HashMap<String, String> resultMap = new HashMap<String, String>();

		try {
			setEntityManager();
			
			String email = user.get("email");
			Boolean existGubun = userRepository.existsByEmail(email);
			if(existGubun) {
				User member = userRepository.findByEmail(email);
				if (user.get("password").equals(user.get("newPassword"))) {
					resultMap.put("result", "fail");
					resultMap.put("message", "새로운 비밀번호가 기존 비밀번호와 일치합니다.");
				} else if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
					resultMap.put("result", "fail");
					resultMap.put("message", "기존 비밀번호가 일치하지 않습니다.");
				} else {
					// 반드시 새 비밀번호인 newPassword를 추가해야된다.
					member.setPassword(passwordEncoder.encode(user.get("newPassword")));
					member.setModDate(Common.todayYmd());

					tx.begin();
					
					em.merge(member);
					
					tx.commit();
					resultMap.put("result", "ok");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "가입되지 않은 이메일입니다.");
				tx.rollback();
			}
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
		return resultMap;
	}

	// 게스트 계정 생성
	public Map<String, String> guestJoin() {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			setEntityManager();
			
			// 게스트 ID 생성
			UUID guestId = UUID.randomUUID();
			tx.begin();

			em.persist(User.builder()
		            .email(guestId.toString())
		            .name("GUEST")
	                .loginYn(false)
	                .delyn(false)
	                .regDate(Common.todayYmd())
	                .build());
			tx.commit();
			resultMap.put("result", "ok");
			resultMap.put("guestId", guestId.toString());
		} catch (Exception e) {
			tx.rollback();
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		} finally {
			em.close();
			emf.close();
		}
		return resultMap;
	}

	// 회원 계정 연동
	public Map<String, String> loginLink(@RequestBody Map<String, Object> pUser, HttpServletResponse response) {
		HashMap<String, String> resultMap = new HashMap<String, String>();

		try {		
			setEntityManager();

			// 게스트 ID
			String guestId;
			if(pUser.get("guestId") != null) {
				guestId = pUser.get("guestId").toString();
			} else {
				guestId = null;
			}

			Boolean existGubun;
			
			// 1. 기존 id를 삭제 처리합니다.
			if(guestId != "" && guestId != null) {
				// guest계정이 있으면서 del_yn이 0이면 (테스트 완료해서 일단 주석)
				existGubun = userRepository.existsByEmail(guestId);
				if (existGubun) {
					User user = userRepository.findByEmail(guestId);
					
					if (user.getDelyn() == null || !user.getDelyn()) {
						Common.guestIdDel(userRepository, guestId, true, em, tx);
						resultMap.put("result", "ok");
					} else {
						resultMap.put("result", "fail");
						resultMap.put("message", "This Id is deleted Id");
						em.close();
						emf.close();
						return resultMap;
					}
				} else {
					resultMap.put("result", "fail");
					resultMap.put("message", "guestId not found in DB");
					em.close();
					emf.close();
					return resultMap;
				}
			}
			
			// 1. 게스트 데이터 및 회원계정이 있는지 확인
			String email = pUser.get("email").toString();
			// email 확인
			existGubun = userRepository.existsByEmail(email);
			if (!existGubun) {
				resultMap.put("result", "fail");
				resultMap.put("message", "email not found in DB");
				em.close();
				emf.close();
				return resultMap;
			}

			// 3. 공룡 및 칭호 데이터 연동
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = new JSONArray();
			JSONObject obj = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			String jsonString;
			
			try {
				String dinoNumArrStr = pUser.get("dinoNumArr").toString();
				Boolean dinoNumGubun = StringUtils.isNumeric(dinoNumArrStr.replaceAll(",", ""));
				if(!dinoNumGubun && dinoNumArrStr != "") {
					if(guestId != "" && guestId != null) {
						Common.guestIdDel(userRepository, guestId, false, em, tx);
					}
					resultMap.put("result", "fail");
					resultMap.put("message", "공룡 파라미터 형식이 정확하지 않습니다.");
					em.close();
					emf.close();
					return resultMap;
				}

				String titleNumArrStr = pUser.get("titleNumArr").toString();
				Boolean titleNumGubun = StringUtils.isNumeric(titleNumArrStr.replaceAll(",", ""));
				if(!titleNumGubun && titleNumArrStr != "") {
					if(guestId != "" && guestId != null) {
						Common.guestIdDel(userRepository, guestId, false, em, tx);
					}
					resultMap.put("result", "fail");
					resultMap.put("message", "칭호 파라미터 형식이 정확하지 않습니다.");
					em.close();
					emf.close();
					return resultMap;
				}
				
				tx.begin();
				Dino dino = Dino.builder()
						.pid(email)
		                .dinoNumArr(pUser.get("dinoNumArr").toString())
		                .titleNumArr(pUser.get("titleNumArr").toString())
		                .regDate(Common.todayYmd())
		                .build();
				
				if(dinoRepository.existsByPid(email)) {
					em.merge(dino);
				} else {
					em.persist(dino);
				}
				tx.commit();
				resultMap.put("result", "ok");
			} catch (Exception e) {
				if(guestId != "" && guestId != null) {
					Common.guestIdDel(userRepository, guestId, false, em, tx);
				}
				resultMap.put("result", "fail");
				resultMap.put("message", "에러 발생");
				em.close();
				emf.close();
				return resultMap;
			}
			
			// 4. 화석 데이터 연동은 안하기로 결정 (2022-02-07)
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		} finally {
			em.close();
			emf.close();
		}
		return resultMap;
		
	}

	// 인증 번호로 비밀번호 변경
	public Map<String, String> mailSend(String email, HttpServletRequest request) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		// 이메일 정규식 체크
		Pattern pattern = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"); 
		Matcher m = pattern.matcher(email);
		
		// 이메일 형식에 맞으면 메일 전송 아니면 예외처리
		if(m.matches()) {
			Properties p = System.getProperties();
			p.put("mail.smtp.starttls.enable", "true");
			p.put("mail.smtp.host", "smtp.gmail.com");
			p.put("mail.smtp.auth", "true");
			p.put("mail.smtp.port", "587");
			
			Authenticator auth = new MyAuthentication();
			
			// session 생성 및 MimeMessage 생성
			Session session = Session.getDefaultInstance(p, auth);
			MimeMessage msg = new MimeMessage(session);
			
			try {
				// 편지보낸시간
				msg.setSentDate(new Date());
				
				InternetAddress from = new InternetAddress();
				
				from = new InternetAddress(new String("진주/고성 박물관".getBytes("UTF-8"), "8859_1")+"<kmhan@learnershi.com>");
				
				// 이메일 발신자
				msg.setFrom(from);
				
				// 이메일 수신자
				InternetAddress to = new InternetAddress(email);
				msg.setRecipient(Message.RecipientType.TO, to);
				
				// 이메일 제목
				msg.setSubject("메일 전송 테스트", "UTF-8");
				
				Random r = new Random();
				int num = r.nextInt(999999); // 랜덤난수설정
				
				// 이메일 내용
				msg.setText("비밀번호 변경 인증번호 : "+num, "UTF-8");
								
				// 이메일 헤더
				msg.setHeader("content-Type",  "text/html");
				
				// 메일 보내기
				javax.mail.Transport.send(msg);
				
				resultMap.put("message", "이메일 전송 성공");
				
				// 패스워드 인증키를 세션에 추가
				request.getSession().setAttribute("AuthenticationKey", num);
			} catch (AddressException addr_e) {
				addr_e.printStackTrace();
			} catch (MessagingException msg_e) {
				msg_e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			resultMap.put("result", "ok");
		} else {
			resultMap.put("result", "fail");
			resultMap.put("message", "정확한 이메일 형식이 아닙니다.");
		}
		
		return resultMap;
	}
	
	// 인증 번호로 비밀번호 변경
	public Map<String, String> mailChangePassword(Map<String, Object> pUser, HttpServletRequest request) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		String key = pUser.get("authenticationKey").toString(); 
		
		String pKey = null;
		if(request.getSession().getAttribute("AuthenticationKey") != null) {
			pKey = request.getSession().getAttribute("AuthenticationKey").toString();
		} else {
			// Postman 테스트용으로 쿠키값 가져와서 이메일과 비교
			Cookie[] cookies = request.getCookies();
			
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("AuthenticationKey")) {
					pKey = cookie.getValue();
					break;
				}
			}
		}
		
		if(!pUser.get("password").equals(pUser.get("passwordConfirm"))) {
			resultMap.put("result", "fail");
			resultMap.put("message", "새 비밀번호가 일치하지 않습니다.");
		} else if(key.equals(pKey)) {
			String email = (String) pUser.get("email");
			if (userRepository.existsByEmail(email)) {
				try {
					setEntityManager();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				tx.begin();
				// 객체를 생성만 한 상태 (비영속)
				User user = em.find(User.class, email);
				
				user.setPassword(passwordEncoder.encode((String) pUser.get("password")));
				user.setModDate(Common.todayYmd());

				em.merge(user);
				tx.commit();

				em.close();
				emf.close();
				
				resultMap.put("result", "ok");
				resultMap.put("message", "비밀번호가 변경되었습니다.");
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "해당 아이디가 없습니다.");
			}
		} else {
			resultMap.put("result", "fail");
			resultMap.put("message", "인증번호가 일치하지 않습니다.");
		}
		
		return resultMap;
	}

	public Map<String, String> tosCheck(String email) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		try {
			setEntityManager();
			
			if (userRepository.existsByEmail(email)) {
				User user = userRepository.findByEmail(email);
				if(!user.getCheckYn()) {
					user.setCheckYn(true);
					user.setModDate(Common.todayYmd());
					//userRepository.save(user);
					tx.begin();
					em.merge(user);
					tx.commit();
					resultMap.put("result", "ok");
				} else {
					resultMap.put("result", "fail");
					resultMap.put("message", "이용약관을 동의한 상태입니다.");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "해당 아이디가 없습니다.");
			}
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", "이용약관 확인 실패");
		} finally {
			em.close();
			emf.close();
		}
		return resultMap;
	}
}