package kr.go.museum.dino.smartapp.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import kr.go.museum.dino.smartapp.model.Version;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long>{
	Version findTop1ByOrderByVersionDesc();
	long count();
}