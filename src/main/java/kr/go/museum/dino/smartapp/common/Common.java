package kr.go.museum.dino.smartapp.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import kr.go.museum.dino.smartapp.model.User;
import kr.go.museum.dino.smartapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Common {

	// 숫자 여부 확인
	public static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	// 오늘 날짜 구하기
	public static String todayYmd() {
		LocalDateTime now = LocalDateTime.now();
		return now.getYear() + String.format("%02d",now.getMonthValue()) + String.format("%02d",now.getDayOfMonth());
	}
	
	// 게스트 아이디 삭제 처리
	public static void guestIdDel(UserRepository userRepository, String guestId, Boolean delYn, EntityManager em, EntityTransaction tx) {
		try {
			if (userRepository.existsByEmail(guestId)) {
				tx.begin();
				em.merge(User.builder()
		                .email(guestId)
		                .loginYn(false)
		                .delyn(delYn)
		                .modDate(Common.todayYmd())
		                .build());
				tx.commit();
			} else {
				System.out.println("데이터를 확인바랍니다.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}