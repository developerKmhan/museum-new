package kr.go.museum.dino.smartapp.contents;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.go.museum.dino.smartapp.model.Fossil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value="/fossil")
@RequiredArgsConstructor
public class FossilController {
	//private final FossilRepository fossilRepository;
	private final FossilService fossilService;
	
	@GetMapping("/list-search")
	public Map<String, Object> listSearch (@RequestParam("pid") String pid) {
		return fossilService.listSearch(pid);
	}

	@GetMapping("/detail-search")
	public Map<String, Object> detailSearch (@RequestParam("pid") String pid, @RequestParam("fossilNum") int fossilNum) {
		return fossilService.detailSearch(pid, fossilNum);
	}
	
	@PostMapping("/detail-insert")
	public Map<String, String> detailInsert (@RequestBody Fossil fossil) {
		return fossilService.detailInsert(fossil);
	}
	
	@PostMapping("/detail-update")
	public Map<String, String> detailUpdate (@RequestBody Fossil fossil) {
		return fossilService.detailUpdate(fossil);
	}
	
	@GetMapping("/detail-delete")
	public Map<String, String> detailDelete (@RequestParam("pid") String pid, @RequestParam("fossilNum") int fossilNum) {
		return fossilService.detailDelete(pid, fossilNum);
	}
}
