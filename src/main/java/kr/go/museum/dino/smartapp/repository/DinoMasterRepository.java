package kr.go.museum.dino.smartapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.go.museum.dino.smartapp.model.DinoMaster;

@Repository
public interface DinoMasterRepository extends JpaRepository<DinoMaster, Long>{
	Boolean existsByQrNum(String qrNum);
	DinoMaster findByQrNum(String qrNum);
	//Boolean existsByDinoNum(int dinoNum);
	DinoMaster findByDinoNum(int dinoNum);
}