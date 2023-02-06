package kr.go.museum.dino.smartapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.go.museum.dino.smartapp.model.Fossil;

@Repository
public interface FossilRepository extends JpaRepository<Fossil, Long> {
	Boolean existsByPid(String pid);
	List<Fossil> findAllByPid(String pid);
	Boolean existsByPidAndManageNum(String email, String manageNum);

	Boolean existsByPidAndFossilNum(String pid, int fossilNum);
	Fossil findByPidAndFossilNum(String pid, int fossilNum);
	Fossil findByPidAndManageNum(String pid, String manageNum);
	void deleteByPidAndFossilNum(String pid, int fossilNum);
}