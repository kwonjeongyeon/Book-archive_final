package com.myspring.bookarchive.order.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.myspring.bookarchive.goods.vo.GoodsVO;
import com.myspring.bookarchive.member.vo.MemberVO;
import com.myspring.bookarchive.order.service.ApiService;
import com.myspring.bookarchive.order.service.OrderService;
import com.myspring.bookarchive.order.vo.OrderVO;

@Controller
public class KakaoController2 {

	@Autowired
	private ApiService apiService;
	
	@RequestMapping(value = "/test/kakaoPay.do")
	public ModelAndView kakaoPay(@RequestParam Map<String, String> map) throws Exception {

		ModelAndView mav = new ModelAndView();
		// 주문 DB에 저장 등등의 기능이 구현되어야 함

		System.out.println(map.toString());

		// 인증데이터로 결제 요청하기 (rest api)
		String res_cd = map.get("res_cd"); // 암호화 정보, 인증완료 후 받은 값
		String enc_info = map.get("enc_info"); // 암호화 정보, 인증완료 후 받은 값
		String enc_data = map.get("enc_data"); // 암호화 정보, 인증완료 후 받은 값
		String tran_cd = map.get("tran_cd"); // 인증코드
		String card_pay_method = map.get("card_pay_method"); // 결제타입
		String ordr_idxx = map.get("ordr_idxx"); // 거래 번호

		Map<String, String> returnMap = new HashMap<String, String>();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// map에 요청데이터값들을 넣기
		returnMap.put("res_cd", res_cd);
		returnMap.put("enc_info", enc_info);
		returnMap.put("enc_data", enc_data);
		returnMap.put("tran_cd", tran_cd);
		returnMap.put("card_pay_method", card_pay_method);
		returnMap.put("ordr_idxx", ordr_idxx);

		String url = "https://api.testpayup.co.kr/ep/api/kakao/himedia/pay";

		resultMap = apiService.restApi(map, url); // (map(맵으로 된 데이터, 요청값들), url(풀 url))

		// 테스트 데이터 (임의로 넣기)
//		resultMap.put("responseCode", "0000");
//		resultMap.put("responseMsg", "성공");
//		resultMap.put("type", "KAKAO_MONEY");

		System.out.println("카카오페이 결제 응답 = " + resultMap.toString());
		// view 설정
		// 승인 성공 or 실패
		String responseCode = (String) resultMap.get("responseCode");

		// "0000" == responseCode (X)
		if ("0000".equals(responseCode)) {
			// 성공 (페이지 설정)
			mav.setViewName("/kakao/kakaoResult");

			// 화면에서 보여줄 값을 mav에 넣기 (하나씩)
//			mav.addObject("type", resultMap.get("type"));
//			mav.addObject("authDateTime", resultMap.get("authDateTime")); // 승인 시간

			// 화면에서 보여줄 값을 통째로 보내기
			mav.addObject("resultMap", resultMap);

		} else {
			// 실패 (페이지 설정)
			mav.setViewName("/kakao/kakaoResultFail");
			mav.addObject("resultMap", resultMap);

//			mav.addObject("responseCode", returnMap.get("responseCode"));
//			mav.addObject("responseMsg", returnMap.get("responseMsg"));

		}

		return mav;

	}

}
