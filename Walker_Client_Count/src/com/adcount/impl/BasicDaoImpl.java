package com.adcount.impl;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


//访问持久化数据库
public class BasicDaoImpl {

	private static SqlSessionFactory sqlSessionFactory = null;

	static {
		String resource = "conf.xml";
		InputStream is = BasicDaoImpl.class.getClassLoader().getResourceAsStream(resource);
		// 构建sqlSession的工�?
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
		
	}

}
