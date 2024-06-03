package com.jjayo802.comciplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ComciplusApplication {

	/*
	TODO

	이전/다음달 겹치면 그것도 크롤링
	weekValues 얻어서 model에 넣기
	 */

	public static void main(String[] args) {
		SpringApplication.run(ComciplusApplication.class, args);
	}

}
