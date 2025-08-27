const contextPath = '';

//StringBuffer 설정
let StringBuffer = function() {
	this.buffer = new Array();
};

StringBuffer.prototype.append = function(str) {
	this.buffer[this.buffer.length] = str;
};

StringBuffer.prototype.toString = function(s) {
	return this.buffer.join((s || ''));
};

// 서비스코드
let svcCode;
// 앱코드
let appCode;
// 테스트베드 여부
let testBed;
// 장치구분
let agent = navigator.userAgent.toUpperCase();
// CA 목록
let caList;
// URL SCHEME
let urlScheme;
// 거래코드
let trxcode;

$(function() {
	// 모바일 신분증 팝업 닫기 버튼 클릭시
	$('#btnCloseMidCrtfc').click(function() {
		fnCloseMidCrtfc();
	});
	
	// 보기 버튼 클릭시
	$('.agree-layout .body li').click(function() {
		$('#' + this.id.replace('Policy', '')).css('display', 'block');
		$('.agree-popup-layout').css('display', 'block');
	});
	
	// 동의 팝업 닫기 버튼 클릭시
	$('.agree-popup-layout .policy .bottom').click(function() {
		$('.agree-popup-layout .policy').css('display', 'none');
		$('.agree-popup-layout').css('display', 'none');
	});
	
	// 동의 창 닫기 버튼 클릭시
	$('#btnCloseAgreeLayout').click(function() {
		$('.app-layout').css({'display': 'block', 'top': '0'});
		$('.agree-layout').css('bottom', '-33rem');
	});
	
	// 모두 동의하고 인증요청 버튼 클릭시
	$('#btnDoAgreeCrtfc').click(function() {
		fnReqCrtfc();
	});
	
	// 인증요청 창 닫기 버튼 클릭시
	$('#btnCloseCrtfcReq').click(function() {
		$('#btnCloseMidCrtfc').trigger('click');
	});
	
	// 인증완료 버튼 클릭시
	$('#btnChkCrtfcCompl').click(function() {
		fnChkCrtfcCmpl();
	});
	
	fnInit();
});

// 초기화 설정
function fnInit() {
	let url = window.location.search.replace('?', '');
	let params = url.split('&');
	
	for (let i in params) {
		if (params[i].indexOf('svcCode') != -1) {
			svcCode = params[i].split('=')[1];
		}
		
		if (params[i].indexOf('testBed') != -1) {
			testBed = params[i].split('=')[1];
		}
	}
	
	if (!svcCode) {
		alert('서비스코드가 입력해주세요');
	}
	
	if (!testBed) {
		alert('테스트베드 여부를 입력해주세요');
	}
	
	if (agent.indexOf('ANDROID') > -1) {
		$('.app-layout').css({'display': 'none', 'top': '100%'});
		$('.agree-layout').css('bottom', '0');
		$('#btnCloseAgreeLayout').css('display', 'none');
	} else if (agent.indexOf('IPHONE') > -1 || agent.indexOf('IPAD') > -1 || agent.indexOf('IPOD') > -1) {
		$('.app-layout').css('display', 'block');
		$('.app-layout').css({'display': 'block', 'top': '0'});
		$('.agree-layout').css('bottom', '-33rem');
		
		fnMakeCaList('.app-layout .body ul');
	} else {
		alert('지원하지 않는 기기입니다.');
		
		//window.close();
	}
}

// CA 목록 생성
function fnMakeCaList(caEl) {
	let param = {
		  url: contextPath + '/mip/spinfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				caList = resultData.caList;
				
				fnMakeRandomCaList(caEl);
			} else {
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// CA 목록 랜덤 생성
function fnMakeRandomCaList(caEl) {
	const nums = new Array();
	const rannums = new Array();

	for (let i = 0; i < caList.length; i++) {
		nums[i] = i;
	}
	
	for (let i = caList.length; i > 0; i--) {
		const r = Math.floor(Math.random() * i);
		
		rannums.push(nums[r]);
		
		nums.splice(r, 1);
	}
	
	for (let i=0; i<caList.length; i++) {
		let ca = caList[rannums[i]];
		
		if ((agent.indexOf('IPHONE') > -1 || agent.indexOf('IPAD') > -1 || agent.indexOf('IPOD') > -1) && ca.appLinkIos) {
			$(caEl).append('<li id="' + ca.appCode + '"><a class="img-box"><img src="' + ca.appIcon + '" alt="' + ca.appName + '"></a><span>' + ca.appName + '</span></li>');
		}
	}
	
	// 앱 클릭시
	$('.app-layout .body li').click(function() {
		$('.app-layout .body li').removeClass('selected');
		
		$(this).addClass('selected');
		
		appCode = this.id;
		
		$('.app-layout').css({'display': 'none', 'top': '100%'});
		$('.agree-layout').css('bottom', '0');
		
		for (let i=0; i<caList.length; i++) {
			if (appCode == caList[i].appCode) {
				if (agent.indexOf('ANDROID') > -1) {
					urlScheme = caList[i].appLinkAos;
				} else if (agent.indexOf('IPHONE') > -1 || agent.indexOf('IPAD') > -1 || agent.indexOf('IPOD') > -1) {
					urlScheme = caList[i].appLinkIos;
				} else {
					alert('지원하지 않는 기기입니다.');
				}
				
				break;
			}
		}
	});
}

// 모바일 신분증 팝업 닫기
function fnCloseMidCrtfc() {
	window.close();
}

// 인증 요청
function fnReqCrtfc() {
	let ifType = 'App2App';
	let mode = 'direct';
	
	trxcode = '';
	
	let param = {
		  url: contextPath + '/web/apptoapp/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'svcCode': svcCode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				if (!urlScheme) {
					urlScheme = 'mobileid';
				}
				
				document.location.href = (testBed == 'Y' ? 't':'') + urlScheme + '://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=';
				
				$('.agree-layout').css('display', 'none');
				$('.crtfc-layout').css('display', 'block');
			} else {
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// 인증 완료 확인
function fnChkCrtfcCmpl() {
	let param = {
		  url: contextPath + '/web/crtfc/cmpl'
		, dataType: 'json'
		, data: JSON.stringify({
			  'trxcode': trxcode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				alert('인증 완료되었습니다.');
				
				$('#btnCloseMidCrtfc').trigger('click');
			} else {
				let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				if (resultData.errcode == '10201') {
					alert('인증 완료 후 시도하시기 바랍니다.');
				} else if (resultData.errcode == '10202') {
					alert('인증 시간이 만료되었습니다.');
					
					$('#btnCloseMidCrtfc').trigger('click');
				} else if (resultData.errcode == '19003') {
					alert('인증 오류가 발생 했습니다. 다시시도하시기 바랍니다.');
					
					$('#btnCloseMidCrtfc').trigger('click');
				} else {
					alert(resultData.errmsg);
				}
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}
