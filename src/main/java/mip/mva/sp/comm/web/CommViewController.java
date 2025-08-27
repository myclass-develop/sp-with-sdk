package mip.mva.sp.comm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.web
 * @FileName CommViewController.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 공통 페이지 이동 Controller
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Controller
public class CommViewController {

	/**
	 * Index 페이지 이동
	 * 
	 * @MethodName index
	 * @return 페이지 URL
	 */
	@GetMapping("/")
	public String index() {
		return "redirect:/index.html";
	}

}
