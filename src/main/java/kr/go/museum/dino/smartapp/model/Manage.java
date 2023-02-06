package kr.go.museum.dino.smartapp.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedNativeQuery(
		name = "findByMonthUserCntList",
		query = "select a.ym, a.cnt "
				+ "  from ("
				+ "select  distinct substring(a.temp_date,1,7) as ym, ifnull(c.cnt,0) as cnt"
				+ "          from tb_temp_date a "
				+ "          left join ("
				+ "	          select concat(substring(u.reg_date,1,4),'-',substring(u.reg_date,5,2)) regDate, count(*) cnt"
				+ "	            from tb_user u"
				+ "	           where del_yn = false "
				+ "	             and admin_yn = false"
				+ "		 	   group by substring(reg_date,1,6) ) c"
				+ "	        on substring(a.temp_date,1,7) = c.regDate"
				+ "	     where substring(a.temp_date,1,7) <= concat(DATE_FORMAT(now(), '%Y'), '-', DATE_FORMAT(now(),'%m'))"
				+ "	     order by substring(a.temp_date,1,7) desc"
				+ "         limit 12"
				+ ") a "
				+ "order by a.ym",
		resultClass = Manage.class
)
@Getter
@Setter
public class Manage {
	// 년월
	@Id
	private String ym;
	
	// 개수
	private String cnt;
}