/**
 * VerifyConfig 생성
 * verifyConfig.js
 */

$(function() {
	const blockchainServerDomain_DEV_PUBLIC = 'https://bcdev.mobileid.go.kr:18888';
	const blockchainServerDomain_DEV_FINENCE = 'https://금융개발:18888';
	const blockchainServerDomain_DEV_NORMAL = 'https://bcdev.mobileid.go.kr:18888';
	const blockchainServerDomain_PROD_PUBLIC = 'https://bcg.mobileid.go.kr:18888,https://bcg2.mobileid.go.kr:18888,https://bcc.mobileid.go.kr:18888';
	const blockchainServerDomain_PROD_FINENCE = 'https://금융운영:18888';
	const blockchainServerDomain_PROD_NORMAL = 'https://bcc.mobileid.go.kr:18888';
	
	const proxyProxyServer_DEV = 'wss://mvadev.mobileid.go.kr:9090/proxyServer';
	const proxyProxyServer_PROD = 'wss://mva01.mobileid.go.kr:9090/proxyServer';
	
	const pushPushServer_DEV = 'https://mvadev.mobileid.go.kr:11001/api/sendVPAPI.do';
	const pushPushServer_PROD = 'https://psh.mobileid.go.kr:8443/api/sendVPAPI.do';
	
	const opnPushServerList_DEV = 'https://mipdev.mobileid.go.kr:22443/v1/capush/list';
	const opnPushServerList_PROD = 'https://pub.mobileid.go.kr:10443/v1/capush/list';
	const opnPushServerUse_DEV = 'https://mipdev.mobileid.go.kr:22443/v1/capush/use';
	const opnPushServerUse_PROD = 'https://pub.mobileid.go.kr:10443/v1/capush/use';
	
	const zkpSchemaName_ALL_DEV = '["devmdl","mnh","devmep","mrcdev","mfc"]';
	const zkpSchemaName_MDL_DEV = '["devmdl"]';
	const zkpSchemaName_MNH_DEV = '["mnh"]';
	const zkpSchemaName_MEP_DEV = '["devmep"]';
	const zkpSchemaName_MRC_DEV = '["mrcdev"]';
	const zkpSchemaName_MFC_DEV = '["mfc"]';
	const zkpSchemaName_ALL_PROD = '["mdlschema","mnh","mep","mrcshema","mfc"]';
	const zkpSchemaName_MDL_PROD = '["mdlschema"]';
	const zkpSchemaName_MNH_PROD = '["mnh"]';
	const zkpSchemaName_MEP_PROD = '["mep"]';
	const zkpSchemaName_MRC_PROD = '["mrcshema"]';
	const zkpSchemaName_MFC_PROD = '["mfc"]';
	
	const dbDriverClassName_cubrid = 'cubrid.jdbc.driver.CUBRIDDriver';
	const dbDriverClassName_db2 = 'com.ibm.db2.jcc.DB2Driver';
	const dbDriverClassName_h2 = 'org.h2.Driver';
	const dbDriverClassName_hsql = 'org.hsqldb.jdbc.JDBCDriver';
	const dbDriverClassName_maria = 'org.mariadb.jdbc.Driver';
	const dbDriverClassName_oracle = 'oracle.jdbc.OracleDriver';
	const dbDriverClassName_postgresql = 'org.postgresql.Driver';
	
	let systemDiv = 'DEV';
	let orgDiv = 'NORMAL';
	let presentTypeDiv = 'VP';
	let proxyUseDiv = 'N';
	let pushUseDiv = 'N';
	
	$('div.j-box-group button.j-btn').click(function() {
		let id = this.id;
		
		if (id.indexOf('systemDiv') != -1) {
			systemDiv = this.value;
		} else if (id.indexOf('orgDiv') != -1) {
			orgDiv = this.value;
		} else if (id.indexOf('presentTypeDiv') != -1) {
			presentTypeDiv = this.value;
		} else if (id.indexOf('proxyUseDiv') != -1) {
			proxyUseDiv = this.value;
		} else if (id.indexOf('pushUseDiv') != -1) {
			pushUseDiv = this.value;
		}
		
		if (id.indexOf('pushUseDiv') != -1 || (id.indexOf('presentTypeDiv') != -1 && orgDiv != 'PUBLIC')) {
			$('#checkVerifyConfigForm').hide();
			
			fnSetVerifyConfigForm();
		} else {
			if (id.indexOf('presentTypeDiv') != -1) {
				if (presentTypeDiv == 'VP' && orgDiv == 'PUBLIC') {
					$(this).closest('div.j-box-group').hide().next().show();
				} else {
					$(this).closest('div.j-box-group').hide().next().next().show();
				}
			} else {
				$(this).closest('div.j-box-group').hide().next().show();
			}
		}
	});
	
	// 생성 버튼 클릭시
	$('#makeVerifyConfigBtn').click(function() {
		fnMakeVerifyConfigJson();
	});
	
	// DB 종류 변경시
	$('#provider').change(function() {
		fnChangeDb();
	});
	
	fnChangeDb();
	
	// 입력폼 설정
	function fnSetVerifyConfigForm() {
		$('#verifyConfigForm').show();
		
		if (systemDiv == 'DEV') {
			if (orgDiv == 'PUBLIC') {
				$('#blockchainServerDomain').val(blockchainServerDomain_DEV_PUBLIC);
			} else if(orgDiv == 'FINENCE') {
				$('#blockchainServerDomain').val(blockchainServerDomain_DEV_FINENCE);
			} else if(orgDiv == 'NORMAL') {
				$('#blockchainServerDomain').val(blockchainServerDomain_DEV_NORMAL);
			}
		} else {
			if (orgDiv == 'PUBLIC') {
				$('#blockchainServerDomain').val(blockchainServerDomain_PROD_PUBLIC);
			} else if(orgDiv == 'FINENCE') {
				$('#blockchainServerDomain').val(blockchainServerDomain_PROD_FINENCE);
			} else if(orgDiv == 'NORMAL') {
				$('#blockchainServerDomain').val(blockchainServerDomain_PROD_NORMAL);
			}
		}
		
		$('#blockchainServerDomain').hide().prev().hide();
		$('#blockchainConnectTimeout').hide().prev().hide();
		$('#blockchainReadTimeout').hide().prev().hide();
		$('#blockchainUseCache').hide().prev().hide();
		$('#blockchainSdkDetailLog').hide().prev().hide();
		
		$('#spCheckRequiredPrivacy').hide().prev().hide();
		$('#spCheckVcExpirationDate').hide().prev().hide();
		
		$('#servicesPresentType').hide().prev().hide();
		$('#servicesEncryptType').hide().prev().hide();
		$('#servicesKeyType').hide().prev().hide();
		
		if (presentTypeDiv == 'VP') {
			$('#servicesPresentType').val(1);
			
			$('#servicesZkpSchemaName').hide().prev().hide();
			$('#servicesAttrList').hide().prev().hide();
			$('#servicesPredList').hide().prev().hide();
		} else if (presentTypeDiv == 'ZKP') {
			$('#servicesPresentType').val(2);
			
			$('#spIsCi').hide().prev().hide();
			$('#spIsTelno').hide().prev().hide();
		} else {
			$('#servicesPresentType').val(3);
			
			$('#servicesAuthType').hide().prev().hide();
			$('#servicesZkpSchemaName').hide().prev().hide();
			$('#servicesAttrList').hide().prev().hide();
			$('#servicesPredList').hide().prev().hide();
			
			$('#servicesAuthType').val('["pin","face"]');
		}
		
		if (proxyUseDiv == 'Y') {
			if (systemDiv == 'DEV') {
				$('#proxyProxyServer').val(proxyProxyServer_DEV);
			} else {
				$('#proxyProxyServer').val(proxyProxyServer_PROD);
			}
			
			$('#proxy').show();
		} else {
			$('#spBiImageBase64').hide().prev().hide();
			
			$('#proxy').children().hide();
		}
		
		if (pushUseDiv == 'Y') {
			if (systemDiv == 'DEV') {
				$('#pushPushServer').val(pushPushServer_DEV).hide().prev().hide();
				
				$('#opnPushServerList').val(opnPushServerList_DEV).hide().prev().hide();
				$('#opnPushServerUse').val(opnPushServerUse_DEV).hide().prev().hide();
			} else {
				$('#pushPushServer').val(pushPushServer_PROD).hide().prev().hide();
				
				$('#opnPushServerList').val(opnPushServerList_PROD).hide().prev().hide();
				$('#opnPushServerUse').val(opnPushServerUse_PROD).hide().prev().hide();
			}
			
			$('#pushPushType').hide().prev().hide();
			
			$('#push').show();
		} else {
			$('#push').children().hide();
		}
		
		$('#blockchainAccount').focus();
	}
	
	// JSON 파일 생성
	function fnMakeVerifyConfigJson() {
		let errMsg = new StringBuffer();
		
		$('#verifyConfigForm input, #verifyConfigForm select').each(function() {
			if ($(this).css('display') == 'inline-block' && $(this).val() == '' && $(this).attr('id') != 'spBiImageBase64' && $(this).attr('id') != 'servicesCallBackUrl') {
				errMsg.append($(this).prev().text().split(':')[0].trim() + '을(를) 입력해주세요.');
			}
		});
		
		if (errMsg.toString() != '') {
			alert(errMsg.toString('\n'));
			
			return;
		}
		
		let obj = new Object();
		let blockchain = new Object();
		
		blockchain.account = $('#blockchainAccount').val();
		blockchain.serverDomain = $('#blockchainServerDomain').val();
		blockchain.connectTimeout = $('#blockchainConnectTimeout').val();
		blockchain.readTimeout = $('#blockchainReadTimeout').val();
		blockchain.useCache = $('#blockchainUseCache').val() == 'true' ? true:false;
		blockchain.sdkDetailLog = $('#blockchainSdkDetailLog').val() == 'true' ? true:false;
		
		obj.blockchain = blockchain;
		
		let didWalletFile = new Object();
		
		didWalletFile.keymanagerPath = $('#didWalletFileKeymanagerPath').val();
		didWalletFile.keymanagerPassword = $('#didWalletFileKeymanagerPassword').val();
		didWalletFile.signKeyId = $('#didWalletFileSignKeyId').val();
		didWalletFile.encryptKeyId = $('#didWalletFileEncryptKeyId').val();
		didWalletFile.didFilePath = $('#didWalletFileDidFilePath').val();
		
		obj.didWalletFile = didWalletFile;
		
		let sp = new Object();
		
		sp.serverDomain = $('#spServerDomain').val();
		sp.biImageUrl = $('#spBiImageUrl').val();
		sp.biImageBase64 = $('#spBiImageBase64').val();
		sp.isCi = $('#spIsCi').val() == 'true' ? true:false;
		sp.isTelno = $('#spIsTelno').val() == 'true' ? true:false;
		sp.checkRequiredPrivacy = $('#spCheckRequiredPrivacy').val() == 'true' ? true:false;
		sp.checkVcExpirationDate = $('#spCheckVcExpirationDate').val() == 'true' ? true:false;
		
		obj.sp = sp;
		
		let services = new Object();
		let service = new Object();
		
		service.spName = $('#servicesSpName').val();
		service.serviceName = $('#servicesServiceName').val();
		service.svcCode = $('#servicesSvcCode').val();
		service.presentType = $('#servicesPresentType').val();
		service.encryptType = $('#servicesEncryptType').val();
		service.keyType = $('#servicesKeyType').val();
		service.authType = $('#servicesAuthType').val() == 'null' ? null: JSON.parse($('#servicesAuthType').val());
		
		if (presentTypeDiv == 'ZKP') {
			const servicesZkpSchemaName = $('#servicesZkpSchemaName').val();
			
			if (systemDiv == 'DEV') {
				if (servicesZkpSchemaName == 'ALL') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_ALL_DEV);
				} else if (servicesZkpSchemaName == 'MDL') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MDL_DEV);
				} else if (servicesZkpSchemaName == 'MNH') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MNH_DEV);
				} else if (servicesZkpSchemaName == 'MEP') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MEP_DEV);
				} else if (servicesZkpSchemaName == 'MRC') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MRC_DEV);
				} else if (servicesZkpSchemaName == 'MFC') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MFC_DEV);
				}
			} else {
				if (servicesZkpSchemaName == 'ALL') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_ALL_PROD);
				} else if (servicesZkpSchemaName == 'MDL') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MDL_PROD);
				} else if (servicesZkpSchemaName == 'MNH') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MNH_PROD);
				} else if (servicesZkpSchemaName == 'MEP') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MEP_PROD);
				} else if (servicesZkpSchemaName == 'MRC') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MRC_PROD);
				} else if (servicesZkpSchemaName == 'MFC') {
					service.zkpSchemaName = JSON.parse(zkpSchemaName_MFC_PROD);
				}
			}
			
			const attrList = $('#servicesAttrList').val();
			const predList = $('#servicesPredList').val();
			
			if (attrList == 'null' && predList == 'null') {
				alert('영지식 항목, 영지식 조건 둘 중 최소 하나는 선택 하셔야 합니다.');
				
				return false;
			}
			
			if (attrList != 'null') {
				service.attrList = JSON.parse(attrList);
			}
			
			if (predList != 'null') {
				service.predList = JSON.parse(predList);
			}
		}
		
		service.callBackUrl = $('#servicesCallBackUrl').val();
		
		services[service.svcCode] = service;
		
		obj.services = services;
		
		if ($('#proxy').css('display') != 'none') {
			let proxy = new Object();
			
			proxy.proxyServer = $('#proxyProxyServer').val();
			proxy.proxyConnTimeOut = $('#proxyProxyConnTimeOut').val();
			
			obj.proxy = proxy;
		}
		
		if ($('#push').css('display') != 'none') {
			let push = new Object();
			
			push.pushServer = $('#pushPushServer').val();
			push.opnPushServerList = $('#opnPushServerList').val();
			push.opnPushServerUse = $('#opnPushServerUse').val();
			push.caListApiUse = $('#caListApiUse').val();
			push.pushMsCode = $('#pushPushMsCode').val();
			push.pushType = $('#pushPushType').val();
			
			obj.push = push;
		}
		
		let ca100 = new Object();
		
		ca100.appCode = "100";
		ca100.appName = "정부앱";
		ca100.appIcon = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyNDAiIGhlaWdodD0iMjQwIiB2aWV3Qm94PSIwIDAgMjQwIDI0MCI+DQogIDxnIGlkPSLqt7jro7lfMjM0MDA4IiBkYXRhLW5hbWU9Iuq3uOujuSAyMzQwMDgiIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0zMjQgLTMzMikiPg0KICAgIDxyZWN0IGlkPSLsgqzqsIHtmJVfNSIgZGF0YS1uYW1lPSLsgqzqsIHtmJUgNSIgd2lkdGg9IjI0MCIgaGVpZ2h0PSIyNDAiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDMyNCAzMzIpIiBmaWxsPSIjZmZmIi8+DQogICAgPGcgaWQ9Iuq3uOujuV8yMzM5NzgiIGRhdGEtbmFtZT0i6re466O5IDIzMzk3OCIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMzY3IDM4NCkiPg0KICAgICAgPHBhdGggaWQ9Iu2MqOyKpF8yNDE5NjMiIGRhdGEtbmFtZT0i7Yyo7IqkIDI0MTk2MyIgZD0iTTU1MS4wOCw0MTkuNzYzbC00Ni4yMTYsMzkuMDE2TDQ3Mi44LDQ0Ny4yMTFsNDYuMi0zOSwyOS44MSwxMC43NDNaIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtNDM1LjQgLTM2NS42ODgpIiBmaWxsPSIjMDkxZjJjIi8+DQogICAgICA8cGF0aCBpZD0i7Yyo7IqkXzI0MTk2NCIgZGF0YS1uYW1lPSLtjKjsiqQgMjQxOTY0IiBkPSJNNjA1LjU2LDQyNi42MThsLTc1LjQwOCw2My42NmExOC42NjUsMTguNjY1LDAsMCwxLTE4LjM2MywzLjI5NGwtNTkuMzMyLTIxLjQsMjkuMTE4LTI0LjU4LDQwLjM0NywxNC41NSw1NC41Mi00Ni4wMjhaIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtNDUyLjQ1NyAtMzU5LjA2MikiIGZpbGw9IiMwMDQ3YTAiLz4NCiAgICAgIDxwYXRoIGlkPSLtjKjsiqRfMjQxOTY1IiBkYXRhLW5hbWU9Iu2MqOyKpCAyNDE5NjUiIGQ9Ik02MDUuNTYsNDA3LjU2NGwtOC4zMDcsNy4wMTMtMjkuMTE3LDI0LjU3OC0yLjI2OC0uODA1LTI5LjgxLTEwLjc0My00Ni4yLDM5LTI5LjExNy0xMC41LTguMjg3LTIuOTgsMzEuNTkzLTI2LjY3OSw0My43ODEtMzYuOTY0YTE4LjY2MiwxOC42NjIsMCwwLDEsMTguMzU5LTMuM2wxOS42NzksNy4wOTQsNy42MzEsMi43MzdaIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtNDUyLjQ1NyAtMzg1LjA4KSIgZmlsbD0iI2NkMmUzYSIgc3R5bGU9Im1peC1ibGVuZC1tb2RlOiBtdWx0aXBseTtpc29sYXRpb246IGlzb2xhdGUiLz4NCiAgICA8L2c+DQogIDwvZz4NCjwvc3ZnPg0K";
		ca100.caPushUrl = "https://psh.mobileid.go.kr";
		ca100.caDid = "did:kr:mobileid:C5XSaSqexRSvUJVE3jxGaqNJRfA";
		ca100.caPubkey = "zEJ1So8pW7fw4QSMhNmo63Wa9MwDEKYPY6fL7gvz8u4z";
		ca100.appLinkAos = "mobileid";
		ca100.appLinkIos = "MobileID";
		ca100.useCallBackYn = "N";
		ca100.caServerCallBackUrl = null;
		
		let caList = new Array();
		
		caList.push(ca100);
		
		obj.caList = caList;
		
		if ($('#provider').val() != 'memory') {
			let db = new Object();
		
			db.provider = $('#provider').val();
			db.url = $('#url').val();
			db.username = $('#username').val();
			db.password = $('#password').val();
			
			if (db.provider == 'cubrid') {
				db.driverClassName = dbDriverClassName_cubrid;
			} else if (db.provider == 'db2') {
				db.driverClassName = dbDriverClassName_db2;
			} else if (db.provider == 'h2') {
				db.driverClassName = dbDriverClassName_h2;
			} else if (db.provider == 'hsql') {
				db.driverClassName = dbDriverClassName_hsql;
			} else if (db.provider == 'maria') {
				db.driverClassName = dbDriverClassName_maria;
			} else if (db.provider == 'oracle') {
				db.driverClassName = dbDriverClassName_oracle;
			} else if (db.provider == 'postgresql') {
				db.driverClassName = dbDriverClassName_postgresql;
			}
			
			obj.db = db;
		}
		
		$('#verifyConfig').text(JSON.stringify(obj, null, 4));
		
		$('#downloadVerifyConfigBtn').attr('href', 'data:text/json:charset=utf-8,' + encodeURIComponent($('#verifyConfig').text()));
		
		$('#verifyConfigForm').hide();
		
		$('#downloadForm').show();
	}
	
	function fnChangeDb() {
		if ($('#provider').val() == 'memory') {
			$('#db label, #db select, #db input').hide();
			
			$('#provider').show().prev().show();
		} else {
			$('#db label, #db select, #db input').show();
		}
	}
});
