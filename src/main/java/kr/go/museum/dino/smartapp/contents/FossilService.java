package kr.go.museum.dino.smartapp.contents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import kr.go.museum.dino.smartapp.common.Common;
import kr.go.museum.dino.smartapp.config.PropertyInitializer;
import kr.go.museum.dino.smartapp.model.Fossil;
import kr.go.museum.dino.smartapp.repository.FossilRepository;
import lombok.RequiredArgsConstructor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@RequiredArgsConstructor
public class FossilService {
	private final FossilRepository fossilRepository;

	EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;
	
	void setEntityManager() {
		emf = Persistence.createEntityManagerFactory("jpabook", PropertyInitializer.prop);
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}

	public Map<String, Object> listSearch(String pid) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			List<Fossil> fossil = fossilRepository.findAllByPid(pid);
			JSONArray jsonArray = JSONArray.fromObject(fossil);
			resultMap.put("list", jsonArray);
			resultMap.put("result", "ok");
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		}
		
		return resultMap;
	}

	public Map<String, Object> detailSearch(String pid, int fossilNum) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			Boolean existsGubun = fossilRepository.existsByPidAndFossilNum(pid, fossilNum);
			if (existsGubun) {
				Fossil fossil = fossilRepository.findByPidAndFossilNum(pid, fossilNum);
				JSONObject jsonObject = JSONObject.fromObject(fossil);
				resultMap.put("result", "ok");
				resultMap.put("detail", jsonObject);
			} else {
				// DB에 등록되지 않았을때 발생하는 현상
				resultMap.put("result", "fail");
				resultMap.put("message", "조건에 해당하는 데이터가 없습니다.");
			}
		} catch (Exception e) {
			resultMap.put("result", "fail");
			resultMap.put("message", e.getMessage());
		}
		
		return resultMap;
	}

	public Map<String, String> detailInsert(Fossil pFossil) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			setEntityManager();
			
			Boolean existGubun = fossilRepository.existsByPidAndManageNum(pFossil.getPid(), pFossil.getManageNum());
			if (!existGubun) { 
				tx.begin();
				
				pFossil.setRegDate(Common.todayYmd());
				em.persist(pFossil);
				
				tx.commit();
				
				resultMap.put("result", "ok");
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "이미 등록된 관리번호입니다.");
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

	public Map<String, String> detailUpdate(Fossil pFossil) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			setEntityManager();
			
			Boolean existsGubun = fossilRepository.existsByPidAndManageNum(pFossil.getPid(), pFossil.getManageNum());
			if (existsGubun) {
				Fossil fossil = fossilRepository.findByPidAndFossilNum(pFossil.getPid(), pFossil.getFossilNum());
				if(fossil.getManageNum().equalsIgnoreCase(pFossil.getManageNum())) {
					tx.begin();
					
					em.merge(Fossil.builder()
							.fossilNum(pFossil.getFossilNum())
							.pid(pFossil.getPid())
							.manageNum(pFossil.getManageNum())
							.name(pFossil.getName())
							.placeOfApplication(pFossil.getPlaceOfApplication())
							.survivalTime(pFossil.getSurvivalTime())
							.size(pFossil.getSize())
							.imgPath(pFossil.getImgPath())
							.drawPath(pFossil.getDrawPath())
							.note(pFossil.getNote())
							.regDate(pFossil.getRegDate())
							.modDate(Common.todayYmd())
							.build()
							);
					
					tx.commit();
					resultMap.put("result", "ok");	
				} else {
					resultMap.put("result", "fail");
					resultMap.put("message", "이미 등록된 관리번호입니다. 관리번호 변경시 기존에 없는 관리번호를 입력해야 됩니다.");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "수정할 데이터가 없습니다.");
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

	public Map<String, String> detailDelete(String pid, int fossilNum) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		try {
			setEntityManager();

			if(fossilRepository.existsByPidAndFossilNum(pid, fossilNum)) {
				Fossil fossil = fossilRepository.findByPidAndFossilNum(pid, fossilNum);
				tx.begin();
				fossil = em.find(Fossil.class, fossil.getFossilNum());
				em.remove(fossil);
				tx.commit();
				resultMap.put("result", "ok");
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "삭제할 데이터가 없습니다.");
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
}
