package com;

import org.apache.log4j.Logger;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String s = "abcsefg";
		//
		// System.out.println(s.substring(2, 5));

		Logger log = Logger.getLogger(Test.class);

		try {
			throw new Exception();
		} catch (Exception e) {
			log.error(e);
		}

	}

}
