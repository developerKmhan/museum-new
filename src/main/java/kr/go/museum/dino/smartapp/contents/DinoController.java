package kr.go.museum.dino.smartapp.contents;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value="/dino")
@RequiredArgsConstructor
public class DinoController {
	private final DinoService dinoService;

	// 진주/고성 탐사 - 공룡 매핑
	@PostMapping("/insert")
	public Map<String, String> insert (@RequestBody Map<String, Object> info) {
		return dinoService.insert(info);
	}

	// 칭호 매핑
	@PostMapping("/entitle")
	public Map<String, String> entitle (@RequestBody Map<String, Object> info) {
		return dinoService.entitle(info);
	}
	
	// 공룡 획득정보 조회 (안쓸 예정)
	@GetMapping("/info-search")
	public Map<String, String> infoSearch (@RequestParam("pid") String pid) {
		return dinoService.infoSearch(pid);
	}

	// 공룡 목록 조회 
	@GetMapping("/list-search")
	public Map<String, Object> listSearch (@RequestParam("pid") String pid) {
		return dinoService.listSearch(pid);
	}
}