package com.myspring.bookarchive.goods.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.myspring.bookarchive.goods.dao.GoodsDAO;
import com.myspring.bookarchive.goods.vo.GoodsVO;
import com.myspring.bookarchive.goods.vo.ImageFileVO;

@Service("goodsService")
@Transactional(propagation = Propagation.REQUIRED)
public class GoodsServiceImpl implements GoodsService {
	@Autowired
	private GoodsDAO goodsDAO;

	public Map<String, List<GoodsVO>> listGoods() throws Exception {
		Map<String, List<GoodsVO>> goodsMap = new HashMap<String, List<GoodsVO>>();

		// newbook, bestseller, steadyseller를 조건으로 각각 도서 정보를 조회해서 HashMap에 저장한 후 반환
		List<GoodsVO> goodsList = goodsDAO.selectGoodsList("bestseller");
		goodsMap.put("bestseller", goodsList);
		goodsList = goodsDAO.selectGoodsList("newbook");
		goodsMap.put("newbook", goodsList);

		goodsList = goodsDAO.selectGoodsList("steadyseller");
		goodsMap.put("steadyseller", goodsList);
		return goodsMap;
	}

	public Map goodsDetail(String _goods_id) throws Exception {
		Map goodsMap = new HashMap();
		
		GoodsVO goodsVO = goodsDAO.selectGoodsDetail(_goods_id);
		goodsMap.put("goodsVO", goodsVO);
		List<ImageFileVO> imageList = goodsDAO.selectGoodsDetailImage(_goods_id);
		goodsMap.put("imageList", imageList);
		// 상품 정보와 이미지 정보를 조회한 후 HashMap에 저장
		return goodsMap;
	}

	public List<String> keywordSearch(String keyword) throws Exception {
		List<String> list = goodsDAO.selectKeywordSearch(keyword);
		return list;
	}

	public List<GoodsVO> searchGoods(String searchWord) throws Exception {
		List goodsList = goodsDAO.selectGoodsBySearchWord(searchWord);
		return goodsList;
	}

}
