package kr.go.museum.dino.smartapp.contents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.springframework.stereotype.Service;

import kr.go.museum.dino.smartapp.common.Common;
import kr.go.museum.dino.smartapp.config.PropertyInitializer;
import kr.go.museum.dino.smartapp.model.Dino;
import kr.go.museum.dino.smartapp.repository.DinoRepository;
import lombok.RequiredArgsConstructor;
import net.sf.json.JSONObject;

@Service
@RequiredArgsConstructor
public class DinoService {
	private final DinoRepository dinoRepository;

	EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;
	
	void setEntityManager() {
		emf = Persistence.createEntityManagerFactory("jpabook", PropertyInitializer.prop);
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}

	// 공룡카드 매핑
	public Map<String, String> insert(Map<String, Object> pInfo){
		HashMap<String, String> resultMap = new HashMap<String, String>();		

		// 1. qrNum으로 dino_num을 찾음
		String email = (String) pInfo.get("email");
		String dinoNum = (String) pInfo.get("dinoNum");
		
		if(Common.isNumeric(dinoNum)) {
			Boolean existsGubun = dinoRepository.existsByPid(email);
			// 2-1. email 없으면 fail 있으면 error
			if (!existsGubun) {
				resultMap.put("result", "fail");
				resultMap.put("message", "해당 아이디가 없습니다. 재로그인하세요.");
			}
			// 2-2. email 있으면 재검색
			else {
				Dino dino = dinoRepository.findByPid(email);

				String dinoNumArrStr = dino.getDinoNumArr();
				String[] dinoNumArr;
				if (dinoNumArrStr == null || "".equals(dinoNumArrStr.trim())) {
					dinoNumArr = new String[1];
					dinoNumArr[0] = dinoNum;
				} else {
					dinoNumArr = dino.getDinoNumArr().split(",");

					for(String num : dinoNumArr) {
						if (num.equals(dinoNum)) {
							resultMap.put("result", "fail");
							resultMap.put("message", "이미 등록된 공룡카드입니다.");
							return resultMap;
						}
					}
					
					// 추가
					int n = dinoNumArr.length;
					dinoNumArr = Arrays.copyOf(dinoNumArr, n + 1);
					dinoNumArr[n] = dinoNum;
					// 오름차순으로 정렬
					Arrays.sort(dinoNumArr);
				}
				
				try {
					setEntityManager();
					
					dinoNumArrStr = String.join(",", dinoNumArr);
					
					tx.begin();
					
					em.merge(Dino.builder()
							.pid(email)
							.dinoNumArr(dinoNumArrStr)
							.titleNumArr(dino.getTitleNumArr())
							.regDate(dino.getRegDate())
							.modDate(Common.todayYmd())
							.build());
					
					tx.commit();
					resultMap.put("result", "ok");
				} catch (Exception e) {
					tx.rollback();
					resultMap.put("result", "fail");
					resultMap.put("message", e.getMessage());
				} finally {
					em.close();
					emf.close();
				}
			}
		} else {
			resultMap.put("result", "fail");
			resultMap.put("message", "dinoNum은 숫자가 아닙니다. ID 값인지 다시 확인해주세요.");
		}
		return resultMap;
	}

	// 칭호 매핑
	public Map<String, String> entitle(Map<String, Object> pInfo){
		HashMap<String, String> resultMap = new HashMap<String, String>();		

		// 1. qrNum으로 title_num을 찾음
		String email = (String) pInfo.get("email");
		String titleNum = (String) pInfo.get("titleNum");
		
		if(Common.isNumeric(titleNum)) {
			Boolean existsGubun = dinoRepository.existsByPid(email);
			// 2-1. email 없으면 fail 있으면 error
			if (!existsGubun) {
				resultMap.put("result", "fail");
				resultMap.put("message", "해당 아이디가 없습니다. 재로그인하세요.");
			}
			// 2-2. email 있으면 재검색
			else {
				Dino dino = dinoRepository.findByPid(email);

				String titleNumArrStr = dino.getTitleNumArr();
				String[] titleNumArr = null;
				if (titleNumArrStr == null || "".equals(titleNumArrStr.trim())) {
					titleNumArr = new String[1];
					titleNumArr[0] = titleNum;
				} else {
					titleNumArr = dino.getTitleNumArr().split(",");

					for(String num : titleNumArr) {
						if (num.equals(titleNum)) {
							resultMap.put("result", "fail");
							resultMap.put("message", "이미 등록된 칭호입니다.");
							return resultMap;
						}
					}
					
					// 추가
					int n = titleNumArr.length;
					titleNumArr = Arrays.copyOf(titleNumArr, n + 1);
					titleNumArr[n] = titleNum;
					// 오름차순으로 정렬
					Arrays.sort(titleNumArr);
				}
				
				try {
					setEntityManager();
					
					titleNumArrStr = String.join(",", titleNumArr);
					
					tx.begin();
					
					em.merge(Dino.builder()
							.pid(email)
							.dinoNumArr(dino.getDinoNumArr())
							.titleNumArr(titleNumArrStr)
							.regDate(dino.getRegDate())
							.modDate(Common.todayYmd())
							.build());
					
					tx.commit();
					resultMap.put("result", "ok");
				} catch (Exception e) {
					tx.rollback();
					resultMap.put("result", "fail");
					resultMap.put("message", e.getMessage());
				} finally {
					em.close();
					emf.close();
				}
			}
		} else {
			resultMap.put("result", "fail");
			resultMap.put("message", "titleNum은 숫자가 아닙니다. ID 값인지 다시 확인해주세요.");
		}
		return resultMap;
	}
	
	// 공룡 획득정보 조회
	public Map<String, String> infoSearch (String pid) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			Boolean existsGubun = dinoRepository.existsByPid(pid);
			if (existsGubun) {
				try {
					Dino dino = dinoRepository.findByPid(pid);
					int count = 0;
					int entitleLevel = 0;
					if (!dino.getDinoNumArr().equals("")) {
						String[] dinoNumArr = dino.getDinoNumArr().split(",");
						count = dinoNumArr.length;
						entitleLevel = count / 10;
					}
					resultMap.put("result", "ok");
					resultMap.put("entitleLevel", Integer.toString(entitleLevel));
					resultMap.put("getCount", Integer.toString(count));
				} catch (Exception e) {
					resultMap.put("result", "fail");
					resultMap.put("message", "데이터에 문제가 있습니다. 관리자에 문의하세요.");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "email에 해당하는 공룡 획득정보가 없습니다.");
			}
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		}

		return resultMap;
	}

	// 공룡 목록 조회
	public Map<String, Object> listSearch (String pid) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		try {
			Dino dino = dinoRepository.findByPid(pid);
			JSONObject jsonObject = JSONObject.fromObject(dino);

			resultMap.put("result", "ok");
			resultMap.put("data", jsonObject);
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		}

		return resultMap;
	}
}