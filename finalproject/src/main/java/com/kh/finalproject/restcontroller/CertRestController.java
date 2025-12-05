package com.kh.finalproject.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dao.CertDao;
import com.kh.finalproject.dto.CertDto;
import com.kh.finalproject.service.EmailService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/cert")
public class CertRestController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	
	//이메일 전송
	@PostMapping("/send")
	public void send(@Valid @RequestBody CertDto certDto) {
		emailService.sendCertNumber(certDto.getCertEmail());
	}
	
	//이메일 체크
//	@PostMapping("/check")
//	public CertResultVO 
	
}
