package kr.go.museum.dino.smartapp.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
	private String result;
	private String accessToken;
	private String refreshToken;
	private String message;
	
	@Builder
	public Token(String result, String accessToken, String refreshToken, String message) {
		this.result = result;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.message = message;
	}
}