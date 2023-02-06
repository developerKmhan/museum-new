package kr.go.museum.dino.smartapp.version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.go.museum.dino.smartapp.common.Common;
import kr.go.museum.dino.smartapp.config.PropertyInitializer;
import kr.go.museum.dino.smartapp.model.Version;
import kr.go.museum.dino.smartapp.repository.VersionRepository;
import lombok.RequiredArgsConstructor;

@RestController
public class VersionController {
    EntityManagerFactory emf;
	EntityManager em;
	EntityTransaction tx;
	
	void setEntityManager() {
		emf = Persistence.createEntityManagerFactory("jpabook", PropertyInitializer.prop);
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}
	
	@GetMapping("/version")
	public Map<String, String> version () {
		HashMap<String, String> resultMap = new HashMap<String, String>();

		try {
			setEntityManager();
			
            tx.begin();
            
            String sql = "select v.version from Version v order by v.version desc";
            Query query = em.createQuery(sql);
            List lst = query.setFirstResult(0).getResultList();
			if (lst.size() > 0) {
				String version = (String) lst.get(0);
	            //Version version = em.find(Version.class, "1.1");
				if (Common.isNumeric(version)) {
					resultMap.put("result", "ok");
					resultMap.put("version", version);
			
					// Version version = versionRepository.findTop1ByOrderByVersionDesc();
				} else {
					resultMap.put("result", "fail");
					resultMap.put("message", "version is not number");
				}
			} else {
				resultMap.put("result", "fail");
				resultMap.put("message", "no data found");
			}
			
            tx.commit(); 

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
}