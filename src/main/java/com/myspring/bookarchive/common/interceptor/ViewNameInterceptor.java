package com.myspring.bookarchive.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ViewNameInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {

			String viewName = getViewName(request);
			// getViewName()메서드를 이용해 브라우저의 요청명에서 뷰이름을 가져옴

			request.setAttribute("viewName", viewName);
			// 뷰이름을 request에 바인딩

			System.out.println("인터셉터에서 찍은 뷰네임" + viewName);
			System.out.println("-------여기까지 인터셉터의 역할");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	// 요청명에서 뷰이름을 반환
	private String getViewName(HttpServletRequest request) throws Exception {
		String contextPath = request.getContextPath();
		String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
		if (uri == null || uri.trim().equals("")) {
			uri = request.getRequestURI();
		}
		System.out.println("getRequestURI()통해 가져온 uri:" + uri);

		int begin = 0;
		if (!((contextPath == null) || ("".equals(contextPath)))) {
			begin = contextPath.length();
		}
		System.out.println("처음 인덱스 위치(컨텍스트 패스 길이):" + begin);

		int end;

		if (uri.indexOf(";") != -1) {
			end = uri.indexOf(";");
		} else if (uri.indexOf("?") != -1) {
			end = uri.indexOf("?");
		} else {
			end = uri.length();
		}
		System.out.println("uri의 길이 : " + end);

		String fileName = uri.substring(begin, end);
		System.out.println("최종 뷰네임 : " + fileName);

		System.out.println("뷰네임에 .이 없으면 -1, 만약 있으면 그 위치 : " + fileName.indexOf("."));

		if (fileName.indexOf(".") != -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			System.out.println("마지막 점 앞으로 있는 부분만 가져옴 : " + fileName);
		}

		System.out.println("뷰네임에 /이 없으면 -1, 만약 있으면 그 위치 : " + fileName.lastIndexOf("/"));

		if (fileName.lastIndexOf("/") != -1) {
			fileName = fileName.substring(fileName.lastIndexOf("/", 1), fileName.length());
			System.out.println(fileName);
		}
		return fileName;
	}
}
