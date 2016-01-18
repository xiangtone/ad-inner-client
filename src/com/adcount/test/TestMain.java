package com.adcount.test;

import com.adcount.domain.CountBean;
import com.adcount.impl.CountDaoImpl;

public class TestMain {

	public static void main(String[] args) {
		 CountDaoImpl countDao = new CountDaoImpl();
		 //com.adcount.domain.CountBean  countBean =  countDao.checkIsExist("20160118");
		 CountBean countBean = new CountBean();
			countBean.setDate("20160118");
			countBean.setAdClickWeb(0);
			countBean.setAdShow(0);
			countBean.setClickToUrl("");
			countBean.setIsAdShow("adshow");
			
//	System.out.println(countDao.insertCount(countBean));
//	 System.out.println(countDao.updateWebclickCount("20160118")); 
	 System.out.println(countDao.updateAdShowCount("20160118")); 
	 
		 //System.out.println(countBean.getDate());
//		boolean results = countDao.updateCount("20160116");
//			if (results) {
//				System.out.println("更新成功");
//			} else {
//				// 插入新的行
//				System.out.println("更新失败");
//				countDao.insertCount("20160116");
//				
//			}
	}
	
}
