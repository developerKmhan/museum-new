package kr.go.museum.dino.smartapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "tb_users_dino_card")
public class Dino {
	@Id
	private String pid;
	@Column(name="dino_num_arr")
	private String dinoNumArr;
	@Column(name="title_num_arr")
	private String titleNumArr;
	@Column(name="reg_date", insertable=true, updatable=false)
	private String regDate;
	@Column(name="mod_date", insertable=false, updatable=true)
	private String modDate;
}