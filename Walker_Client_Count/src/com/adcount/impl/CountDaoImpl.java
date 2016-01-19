package com.adcount.impl;

import org.apache.ibatis.session.SqlSession;

import com.adcount.domain.CountBean;

public class CountDaoImpl extends BasicDaoImpl {
	// 添加用户
	public void add(CountBean count) {
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {
			sqlSession.insert("insertMyUser", count);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
	}

	public CountBean checkIsExist(String date) {
		CountBean countBean = null;
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {
			countBean = sqlSession.selectOne("getdate", date);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return countBean;
	}

	public boolean updateWebclickCount(String date) {

		int i;
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {

			i = sqlSession.update("updateWebClickCount", date);

			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return i > 0 ? true : false;
	}
	
	public boolean updateAdShowCount(String date) {
		
		int i;
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {
			
			i = sqlSession.update("updateAdShowCount", date);
			
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return i > 0 ? true : false;
	}

	public boolean insertCount(CountBean countBean) {
		int y;
		SqlSession sqlSession = BasicDaoImpl.getSqlSessionFactory().openSession();
		try {

			y = sqlSession.insert("insertCount", countBean);

			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return y > 0 ? true : false;
	}

}
