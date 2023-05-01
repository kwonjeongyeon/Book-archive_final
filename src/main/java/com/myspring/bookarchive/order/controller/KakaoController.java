package com.myspring.bookarchive.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myspring.bookarchive.order.service.ApiService;

@RestController
public class KakaoController {

	@Autowired
	private ApiService apiService;

	@RequestMapping(value = "/test/kakaoOrder.do")
	public Map<String, Object> kakaoOrder(@RequestParam Map<String, String> dateMap) throws Exception {

		System.out.println("들어오는 데이터 = " + dateMap.toString());
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 결제 승인 값 (필수)
		String merchantId = "";
		String apiCertKey = "";
		String orderNumber = "";
		String amount = "";
		String itemName = "";
		String userName = "";
		String returnUrl = "";
		String signature = "";
		String timestamp = "";
		String userAgent = "";
		// userAgent(사용자환경), 꼭 WP로 보내야함 (모바일 : WM, PC:WP)

		// 값 세팅
		merchantId = "himedia";
		apiCertKey = "ac805b30517f4fd08e3e80490e559f8e";
		orderNumber = "TEST_hong1230"; // 주문번호 생성
		amount = "100";
		itemName = "book";
		userName = "홍지수";
		userAgent = "WP";
		returnUrl = "http://localhost:8080/bookarchive/main/main.do";
		timestamp = "20230501150526";
		signature = apiService
				.encrypt(merchantId + "|" + orderNumber + "|" + amount + "|" + apiCertKey + "|" + timestamp); // 서명값
		// 예시 )
		// sha256_hash({merchantId}|{orderNumber}|{amount}|{apiCertKey}|{timestamp})
		// 암호화 값을 만든다 => 통신상 암호화해서 보낸다음 서로 더블체크 (결제 위변조 방지를 위한 파라미터), 가장 기본적인 위변조 방지 기술

		// 주문 연동하기
		String url = "https://api.testpayup.co.kr/ep/api/kakao/" + merchantId + "/order";
		Map<String, String> map = new HashMap<String, String>();

		// map에 요청데이터값들을 넣기
		map.put("merchantId", merchantId);
		map.put("orderNumber", orderNumber);
		map.put("amount", amount);
		map.put("itemName", itemName);
		map.put("userName", userName);
		map.put("returnUrl", returnUrl);
		map.put("userAgent", userAgent);
		map.put("timestamp", timestamp);
		map.put("signature", signature);

		resultMap = apiService.restApi(map, url);
		System.out.println("나가는 데이터 = " + resultMap.toString());

		return resultMap;
	}

}
