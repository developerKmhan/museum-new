package kr.go.museum.dino.smartapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.go.museum.dino.smartapp.model.Dino;
import kr.go.museum.dino.smartapp.model.DinoMaster;

@Repository
public interface DinoRepository extends JpaRepository<Dino, Long> {
	int countDinoByPid(String pid);
	boolean existsByPid(String pid);
	Dino findByPid(String pid);
	List<Dino> findAllByPid(String pid);
}