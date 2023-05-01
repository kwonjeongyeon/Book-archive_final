package com.myspring.bookarchive.order.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.myspring.bookarchive.common.base.BaseController;
import com.myspring.bookarchive.goods.vo.GoodsVO;
import com.myspring.bookarchive.member.vo.MemberVO;
import com.myspring.bookarchive.order.service.ApiService;
import com.myspring.bookarchive.order.service.OrderService;
import com.myspring.bookarchive.order.vo.OrderVO;

@Controller("orderController")
@RequestMapping(value = "/order")
public class OrderControllerImpl extends BaseController implements OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderVO orderVO;

	@Autowired
	private ApiService apiService;

	@RequestMapping(value = "/orderEachGoods.do", method = RequestMethod.POST)
	public ModelAndView orderEachGoods(@ModelAttribute("orderVO") OrderVO _orderVO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		session = request.getSession();

		Boolean isLogOn = (Boolean) session.getAttribute("isLogOn");
		String action = (String) session.getAttribute("action");
		// 로그인 여부 체크
		// 이전에 로그인 상태인 경우는 주문과정 진행
		// 로그아웃 상태인 경우 로그인 화면으로 이동
		if (isLogOn == null || isLogOn == false) {
			session.setAttribute("orderInfo", _orderVO);
			session.setAttribute("action", "/order/orderEachGoods.do");
			return new ModelAndView("redirect:/member/loginForm.do");
		} else { // 로그인 후 세션에서 주문 정보를 가져와 바로 주문창으로 이동
			if (action != null && action.equals("/order/orderEachGoods.do")) {
				orderVO = (OrderVO) session.getAttribute("orderInfo");
				session.removeAttribute("action");
			} else { // 미리 로그인을 했다면 바로 주문 처리
				orderVO = _orderVO;
			}
		}

		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		List myOrderList = new ArrayList<OrderVO>(); // 주문 정보를 저장할 주문 ArrayList 생성
		myOrderList.add(orderVO); // 브라우저에서 전달한 주문 정보를 ArrayList에 저장

		MemberVO memberInfo = (MemberVO) session.getAttribute("memberInfo");

		// 주문 정보와 주문자 정보를 세션에 바인딩한 후 주문창으로 전달
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberInfo);
		return mav;
	}

	@RequestMapping(value = "/orderAllCartGoods.do", method = RequestMethod.POST)
	public ModelAndView orderAllCartGoods(@RequestParam("cart_goods_qty") String[] cart_goods_qty,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session = request.getSession();
		Map cartMap = (Map) session.getAttribute("cartMap");
		List myOrderList = new ArrayList<OrderVO>();

		List<GoodsVO> myGoodsList = (List<GoodsVO>) cartMap.get("myGoodsList");
		MemberVO memberVO = (MemberVO) session.getAttribute("memberInfo");

		for (int i = 0; i < cart_goods_qty.length; i++) {
			String[] cart_goods = cart_goods_qty[i].split(":");
			for (int j = 0; j < myGoodsList.size(); j++) {
				GoodsVO goodsVO = myGoodsList.get(j);
				int goods_id = goodsVO.getGoods_id();
				if (goods_id == Integer.parseInt(cart_goods[0])) {
					OrderVO _orderVO = new OrderVO();
					String goods_title = goodsVO.getGoods_title();
					int goods_sales_price = goodsVO.getGoods_sales_price();
					String goods_fileName = goodsVO.getGoods_fileName();
					_orderVO.setGoods_id(goods_id);
					_orderVO.setGoods_title(goods_title);
					_orderVO.setGoods_sales_price(goods_sales_price);
					_orderVO.setGoods_fileName(goods_fileName);
					_orderVO.setOrder_goods_qty(Integer.parseInt(cart_goods[1]));
					myOrderList.add(_orderVO);
					break;
				}
			}
		}
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberVO);
		return mav;
	}

	@RequestMapping(value = "/payToOrderGoods.do", method = RequestMethod.POST)
	public ModelAndView payToOrderGoods(@RequestParam Map<String, String> receiverMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception { // 주문창에서 입력한 상품 수령자 정보와 배송지 정보 Map에 바로 저장
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);

		// 1. 주문 데이터 생성
		HttpSession session = request.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("orderer");
		String member_id = memberVO.getMember_id();
		String orderer_name = memberVO.getMember_name();
		String orderer_hp = memberVO.getHp1() + "-" + memberVO.getHp2() + "-" + memberVO.getHp3();
		List<OrderVO> myOrderList = (List<OrderVO>) session.getAttribute("myOrderList");

		// 주문창에서 입력한 수령자 정보와 배송지 정보를 주문 상품 정보 목록과 합친다
		for (int i = 0; i < myOrderList.size(); i++) {
			OrderVO orderVO = (OrderVO) myOrderList.get(i);

			// 각 orderVO에 수령자 정보를 설정한 후 다시 myOrderList에 저장
			orderVO.setMember_id(member_id);
			orderVO.setOrderer_name(orderer_name);
			orderVO.setReceiver_name(receiverMap.get("receiver_name"));

			orderVO.setReceiver_hp1(receiverMap.get("receiver_hp1"));
			orderVO.setReceiver_hp2(receiverMap.get("receiver_hp2"));
			orderVO.setReceiver_hp3(receiverMap.get("receiver_hp3"));
			orderVO.setReceiver_tel1(receiverMap.get("receiver_tel1"));
			orderVO.setReceiver_tel2(receiverMap.get("receiver_tel2"));
			orderVO.setReceiver_tel3(receiverMap.get("receiver_tel3"));

			orderVO.setDelivery_address(receiverMap.get("delivery_address"));
			orderVO.setDelivery_message(receiverMap.get("delivery_message"));
			orderVO.setDelivery_method(receiverMap.get("delivery_method"));
			orderVO.setGift_wrapping(receiverMap.get("gift_wrapping"));
			orderVO.setPay_method(receiverMap.get("pay_method"));
			orderVO.setCard_com_name(receiverMap.get("card_com_name"));
			orderVO.setCard_pay_month(receiverMap.get("card_pay_month"));
			orderVO.setPay_orderer_hp_num(receiverMap.get("pay_orderer_hp_num"));
			orderVO.setOrderer_hp(orderer_hp);
			myOrderList.set(i, orderVO); // 각 orderVO에 주문자 정보를 세팅한 후 다시 myOrderList에 저장한다.
		}

		orderService.addNewOrder(myOrderList); // 주문 정보를 SQL문으로 전달

		// 결제하기
		System.out.println("확인 :" + receiverMap.toString());

		// 결제 승인 값 (필수만 넣음)
		String merchantId = ""; // 가맹점 아이디
		String orderNumber = ""; // 주문번호
		String cardNo = ""; // 카드번호
		String expireMonth = ""; // 유효기간 월
		String expireYear = ""; // 유효기간 년
		String birthday = ""; // 생년월일
		String cardPw = ""; // 카드 비밀번호
		String amount = ""; // 결제금액
		String quota = ""; // 할부기간 (0~24)
		String itemName = ""; // 상품명
		String userName = ""; // 구매자명
		String signature = ""; // 서명값
		String timestamp = ""; // 타임스탬프
		String apiCertKey = ""; // api 인증키

		// 값 세팅
		merchantId = "himedia";
		apiCertKey = "ac805b30517f4fd08e3e80490e559f8e";
		orderNumber = "TEST_hong1230"; // 주문번호 생성
		cardNo = receiverMap.get("cardNo"); // 화면에서 받은 값
		expireMonth = receiverMap.get("expireMonth");
		expireYear = receiverMap.get("expireYear");
		birthday = receiverMap.get("birthday");
		cardPw = receiverMap.get("cardPw");
		amount = "1000";
		quota = "0"; // 일시불
		itemName = "book";
		userName = "홍지수";
		timestamp = "20230501150526";
		signature = apiService
				.encrypt(merchantId + "|" + orderNumber + "|" + amount + "|" + apiCertKey + "|" + timestamp); // 서명값
		// 예시 )
		// sha256_hash({merchantId}|{orderNumber}|{amount}|{apiCertKey}|{timestamp})
		// 암호화 값을 만든다 => 통신상 암호화해서 보낸다음 서로 더블체크 (결제 위변조 방지를 위한 파라미터), 가장 기본적인 위변조 방지 기술

		// rest api를 라이브러리 써서 사용
		// 가장 평범한 통신은 httpURLconnection으로 하는 통신 (X)
		String url = "https://api.testpayup.co.kr/v2/api/payment/" + merchantId + "/keyin2";
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> returnMap = new HashMap<String, Object>();

		// map에 요청데이터값들을 넣으면 된다
		map.put("merchantId", merchantId);
		map.put("orderNumber", orderNumber);
		map.put("cardNo", cardNo);
		map.put("expireMonth", expireMonth);
		map.put("expireYear", expireYear);
		map.put("birthday", birthday);
		map.put("cardPw", cardPw);
		map.put("amount", amount);
		map.put("quota", quota);
		map.put("itemName", itemName);
		map.put("userName", userName);
		map.put("signature", signature);
		map.put("timestamp", timestamp);

		returnMap = apiService.restApi(map, url);
		System.out.println("카드 결제 승인 응답 데이터 = " + returnMap.toString());
		// 응답값을 잘 받으면 수기결제 (구인증) 연동 완료

		// 승인 성공 or 실패
		String responseCode = (String) returnMap.get("responseCode");

		// "0000" == responseCode (X)
		if ("0000".equals(responseCode)) {
			// 성공 (페이지 설정)
			mav.setViewName("/order/payToOrderGoods");

			mav.addObject("responseCode", returnMap.get("responseCode"));
			mav.addObject("responseMsg", returnMap.get("responseMsg"));
			mav.addObject("cardName", returnMap.get("cardName")); //카드사명
			mav.addObject("authNumber", returnMap.get("authNumber")); //승인번호
			mav.addObject("authDateTime", returnMap.get("authDateTime")); //승인 시간

		} else {
			// 실패 (페이지 설정)
			mav.setViewName("/order/orderResultFail");

			mav.addObject("responseCode", returnMap.get("responseCode"));
			mav.addObject("responseMsg", returnMap.get("responseMsg"));

		}

		mav.addObject("myOrderInfo", receiverMap); // OrderVO로 주문결과 페이지에 주문자 정보를 표시하도록 전달
		mav.addObject("myOrderList", myOrderList); // 주문결과 페이지에 주문 상품 목록을 표시하도록 전달
		return mav;
	}

}
