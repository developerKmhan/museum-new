package kr.go.museum.dino.smartapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_dino_card_master")
public class DinoMaster {
	@Id
	@Column(name="seq_num")
	private int seqNum;
	
	@Column(name="qr_num")
	private String qrNum;
	
	@Column(name="dino_num")
	private int dinoNum;
	
	@Column(name="kor_name")
	private String korName;
	@Column(name="eng_name")
	private String engName;
	@Column(name="img_path")
	private String imgPath;
	@Column(name="time_of_apprearance")
	private String timeOfApprearance;
	private String weight;
	private String size;
	private String times;
	@Column(name="herbivore_yn")
	private String herbivoreYn;
	private String note;
	private String grade;
	@Column(name="reg_date", insertable=true, updatable=false)
	private String regDate;
	@Column(name="mod_date", insertable=false, updatable=true)
	private String modDate;
}