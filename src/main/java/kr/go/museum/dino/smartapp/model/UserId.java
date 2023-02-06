package kr.go.museum.dino.smartapp.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;

import lombok.Getter;
import lombok.Setter;

@Entity
@NamedNativeQuery(
		name = "findUserIdList",
		query = "SELECT if(name = 'GUEST','게스트','회원') guest_yn,"
	                + "email, if(name = 'GUEST','',name) name, concat(subString(reg_date,1,4),'-',subString(reg_date,5,2),'-',subString(reg_date,7,2)) reg_date "
	                + "FROM tb_user "
	                + "WHERE del_yn != true "
	                + "  AND admin_yn != true "
	                + "ORDER BY reg_date desc"
)
@Getter
@Setter
public class UserId {
	@Id
	private String email;
	
	private String guestYn;
	private String checkYn;
	private String name;
	private String regDate;
}