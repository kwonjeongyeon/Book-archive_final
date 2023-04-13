package com.myspring.bookarchive.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.coobird.thumbnailator.Thumbnails;

@Controller
public class FileDownloadController {
	private static String CURR_IMAGE_REPO_PATH = "C:\\shopping\\file_repo";

	@RequestMapping("/download")
	protected void download(@RequestParam("fileName") String fileName, @RequestParam("goods_id") String goods_id,
			HttpServletResponse response) throws Exception {
		// 이미지 파일 이름을 바로 설정
		// Annotation which indicates that a method parameter should be bound to a web
		// request parameter.
		
		OutputStream out = response.getOutputStream();
		String filePath = CURR_IMAGE_REPO_PATH + "\\" + goods_id + "\\" + fileName;
		// 글 번호와 파일 이름으로 다운로드할 파일 경로 설정
		File image = new File(filePath);

		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment; fileName=" + fileName);
		FileInputStream in = new FileInputStream(image);
		
		// 데이터를 한 곳에서 다른 한 곳으로 전송하는 동안 일시적으로 그 데이터를 보관하는 메모리의 영역
		byte[] buffer = new byte[1024 * 8];
		while (true) {
			int count = in.read(buffer); //버퍼에 읽어들인 문자개수
			if (count == -1)  //버퍼의 마지막에 도달했는지 체크
				break;
			out.write(buffer, 0, count);
		}
		in.close();
		out.close();
	}

	@RequestMapping("/thumbnails.do")
	protected void thumbnails(@RequestParam("fileName") String fileName, @RequestParam("goods_id") String goods_id,
			HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		String filePath = CURR_IMAGE_REPO_PATH + "\\" + goods_id + "\\" + fileName;
		File image = new File(filePath);

		if (image.exists()) {
			Thumbnails.of(image).size(121, 154).outputFormat("png").toOutputStream(out);
			//메인 페이지 이미지를 썸네일로 표시
		}
		byte[] buffer = new byte[1024 * 8];
		out.write(buffer);
		out.close();
	}
}
