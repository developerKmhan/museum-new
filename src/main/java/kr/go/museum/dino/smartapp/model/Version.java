package kr.go.museum.dino.smartapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_settings")
public class Version {
	@Id
	private String version;
	
	@Column(name="reg_date")
	private String regDate;
}