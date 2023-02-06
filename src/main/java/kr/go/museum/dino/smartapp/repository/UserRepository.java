package kr.go.museum.dino.smartapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.go.museum.dino.smartapp.model.User;
import kr.go.museum.dino.smartapp.model.UserId;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	
	Boolean existsByEmail(String email);
	Boolean existsByEmailAndAdminYn(String email, Boolean adminYn);
	Boolean existsByEmailAndPassword(String email, String password);
	
}

