/* 거래정보 */
CREATE TABLE tb_trx_info (
	trxcode VARCHAR(50) NOT NULL,                          /* 거래코드 */
	if_type VARCHAR(50) NOT NULL,                          /* 인터페이스유형 */
	svc_code VARCHAR(50) NOT NULL,                         /* 서비스코드 */
	mode VARCHAR(50) NOT NULL,                             /* 모드 */
	nonce VARCHAR(100) DEFAULT NULL,                       /* nonce(presentType=1) */
	vp_verify_result VARCHAR(1) NOT NULL DEFAULT 'N',      /* VP 검증 결과 여부 */
	vp LONGTEXT DEFAULT NULL,                              /* VP Data */
	trx_sts_code VARCHAR(4) NOT NULL DEFAULT '0001',       /* 거래상태코드(0001: 서비스요청, 0002: profile요청, 0003: VP 검증요청, 0004: VP 검증완료) */
	profile_send_dt TIMESTAMP NULL DEFAULT NULL,           /* profile 송신일시(M310) */
	img_send_dt TIMESTAMP NULL DEFAULT NULL,               /* 이미지 송신일시(M320) */
	vp_recept_dt TIMESTAMP NULL DEFAULT NULL,              /* VP 수신일시(M400) */
	error_cn VARCHAR(4000) DEFAULT NULL,                   /* 오류 내용 */
	reg_dt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(), /* 등록일시 */
	udt_dt TIMESTAMP NULL DEFAULT NULL,                    /* 수정일시 */
	PRIMARY KEY (trxcode)
);