package kr.go.museum.dino.smartapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "tb_fossil")
@SequenceGenerator(name = "seq_fossil_generator",
sequenceName = "seq_fossil", initialValue = 1, allocationSize = 1)
public class Fossil {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
					generator = "seq_fossil_generator")
	@Column(name="fossil_num")
	private int fossilNum;
	private String pid;
	@Column(name="manage_num")
	private String manageNum;
	
	private String name;
	@Column(name="place_of_application")
	private String placeOfApplication;
	@Column(name="survival_time")
	private String survivalTime;
	private String size;
	@Column(name="img_path")
	private String imgPath;
	@Column(name="draw_path")
	private String drawPath; 
	private String note;
	@Column(name="reg_date", insertable=true, updatable=false)
	private String regDate;
	@Column(name="mod_date", insertable=false, updatable=true)
	private String modDate;
}