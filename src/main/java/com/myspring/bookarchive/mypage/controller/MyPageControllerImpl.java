package com.myspring.bookarchive.mypage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.myspring.bookarchive.common.base.BaseController;
import com.myspring.bookarchive.member.vo.MemberVO;
import com.myspring.bookarchive.mypage.service.MyPageService;
import com.myspring.bookarchive.order.vo.OrderVO;

@Controller("myPageController")
@RequestMapping(value = "/mypage")
public class MyPageControllerImpl extends BaseController implements MyPageController {
	@Autowired
	private MyPageService myPageService;

	@Autowired
	private MemberVO memberVO;

	@Override
	@RequestMapping(value = "/myPageMain.do", method = RequestMethod.GET)
	public ModelAndView myPageMain(@RequestParam(required = false, value = "message") String message,
			HttpServletRequest request, HttpServletResponse response) throws Exception { // 마이페이지 최초 화면 요청, 주문 취소시 결과
																							// 메시지 받음
		HttpSession session = request.getSession();
		session = request.getSession();
		session.setAttribute("side_menu", "my_page"); // 마이페이지 사이드 메뉴로 설정한다.

		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		memberVO = (MemberVO) session.getAttribute("memberInfo");
		String member_id = memberVO.getMember_id();

		List<OrderVO> myOrderList = myPageService.listMyOrderGoods(member_id);
		// 회원 ID를 이용해 주문 상품을 조회

		mav.addObject("message", message); // 주문 취소 시 결과 메시지를 JSP로 전달
		mav.addObject("myOrderList", myOrderList); // 주문 상품 목록을 JSP로 전달

		return mav;
	}

	@Override
	@RequestMapping(value = "/myOrderDetail.do", method = RequestMethod.GET)
	public ModelAndView myOrderDetail(@RequestParam("order_id") String order_id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session = request.getSession();
		MemberVO orderer = (MemberVO) session.getAttribute("memberInfo");

		List<OrderVO> myOrderList = myPageService.findMyOrderInfo(order_id);
		mav.addObject("orderer", orderer);
		mav.addObject("myOrderList", myOrderList);
		return mav;
	}

	@Override
	@RequestMapping(value = "/listMyOrderHistory.do", method = RequestMethod.GET)
	public ModelAndView listMyOrderHistory(@RequestParam Map<String, String> dateMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session = request.getSession();
		memberVO = (MemberVO) session.getAttribute("memberInfo");
		String member_id = memberVO.getMember_id();

		String fixedSearchPeriod = dateMap.get("fixedSearchPeriod");
		String beginDate = null, endDate = null;

		String[] tempDate = calcSearchPeriod(fixedSearchPeriod).split(",");
		beginDate = tempDate[0];
		endDate = tempDate[1];
		dateMap.put("beginDate", beginDate);
		dateMap.put("endDate", endDate);
		dateMap.put("member_id", member_id);
		List<OrderVO> myOrderHistList = myPageService.listMyOrderHistory(dateMap);

		String beginDate1[] = beginDate.split("-"); // �˻����ڸ� ��,��,�Ϸ� �и��ؼ� ȭ�鿡 �����մϴ�.
		String endDate1[] = endDate.split("-");
		mav.addObject("beginYear", beginDate1[0]);
		mav.addObject("beginMonth", beginDate1[1]);
		mav.addObject("beginDay", beginDate1[2]);
		mav.addObject("endYear", endDate1[0]);
		mav.addObject("endMonth", endDate1[1]);
		mav.addObject("endDay", endDate1[2]);
		mav.addObject("myOrderHistList", myOrderHistList);
		return mav;
	}

	@Override
	@RequestMapping(value = "/cancelMyOrder.do", method = RequestMethod.POST)
	public ModelAndView cancelMyOrder(@RequestParam("order_id") String order_id, HttpServletRequest request,
			HttpServletResponse response) throws Exception { // 주문 취소 클릭시 수행 (취소할 주문 번호 전달
		ModelAndView mav = new ModelAndView();
		myPageService.cancelOrder(order_id); // 주문 취소
		mav.addObject("message", "cancel_order"); // 주문 메시지를 다시 마이페이지 최초 화면으로 전달
		mav.setViewName("redirect:/mypage/myPageMain.do");
		return mav;
	}

	@Override
	@RequestMapping(value = "/myDetailInfo.do", method = RequestMethod.GET)
	public ModelAndView myDetailInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		return mav;
	}

	@Override
	@RequestMapping(value = "/modifyMyInfo.do", method = RequestMethod.POST)
	public ResponseEntity modifyMyInfo(@RequestParam("attribute") String attribute, @RequestParam("value") String value,
			HttpServletRequest request, HttpServletResponse response) throws Exception { //수정할 회원 정보 속성 저장("attribute"), 회원 정보의 속성값 저장("value")
		Map<String, String> memberMap = new HashMap<String, String>();
		String val[] = null;
		HttpSession session = request.getSession();
		memberVO = (MemberVO) session.getAttribute("memberInfo");
		String member_id = memberVO.getMember_id();
		if (attribute.equals("member_birth")) {
			val = value.split(",");
			memberMap.put("member_birth_y", val[0]);
			memberMap.put("member_birth_m", val[1]);
			memberMap.put("member_birth_d", val[2]);
			memberMap.put("member_birth_gn", val[3]);
		} else if (attribute.equals("tel")) {
			val = value.split(",");
			memberMap.put("tel1", val[0]);
			memberMap.put("tel2", val[1]);
			memberMap.put("tel3", val[2]);
		} else if (attribute.equals("hp")) {
			val = value.split(",");
			memberMap.put("hp1", val[0]);
			memberMap.put("hp2", val[1]);
			memberMap.put("hp3", val[2]);
			memberMap.put("smssts_yn", val[3]);
		} else if (attribute.equals("email")) {
			val = value.split(",");
			memberMap.put("email1", val[0]);
			memberMap.put("email2", val[1]);
			memberMap.put("emailsts_yn", val[2]);
		} else if (attribute.equals("address")) {
			val = value.split(",");
			memberMap.put("zipcode", val[0]);
			memberMap.put("roadAddress", val[1]);
			memberMap.put("jibunAddress", val[2]);
			memberMap.put("namujiAddress", val[3]);
		} else {
			memberMap.put(attribute, value);
		}

		memberMap.put("member_id", member_id);

		//수정된 회원 정보를 다시 세션에 저장한다.
		memberVO = (MemberVO) myPageService.modifyMyInfo(memberMap); //수정 후 다시 갱신된 회원 정보 조회
		session.removeAttribute("memberInfo");
		session.setAttribute("memberInfo", memberVO);
		//세션에 저장된 기존 회원 정보를 삭제한 후 갱신된 회원 정보 저장

		String message = null;
		ResponseEntity resEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		message = "mod_success";
		resEntity = new ResponseEntity(message, responseHeaders, HttpStatus.OK);
		return resEntity;
	}

}
