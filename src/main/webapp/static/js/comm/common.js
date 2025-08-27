/**
 * 공통 script
 */
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

// 장치구분
let agent = navigator.userAgent.toUpperCase();
// CA 목록
let caList;
// URL SCHEME
let urlScheme;

// SP정보 생성
function fnMakeSpInfo(servceEl, caEl, ifType) {
	let param = {
		  url: contextPath + '/mip/spinfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (servceEl) {
					for (let i=0; i<resultData.serviceList.length; i++) {
						if (resultData.serviceList[i].presentType != 3) {
							$(servceEl).append('<option value="' + resultData.serviceList[i].svcCode + '">' + resultData.serviceList[i].svcCode + '</option>');
						}
					}
				}
				
				if (caEl) {
					caList = resultData.caList;
					
					fnMakeRandomCaList(caEl, resultData.caList, ifType);
				}
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
function fnMakeRandomCaList(caEl, caList, ifType) {
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
		
		if (ifType == 'PUSH') {
			$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
		} else {
			if (agent.indexOf('ANDROID') > -1) {
				$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
			} else if ((agent.indexOf('IPHONE') > -1 || agent.indexOf('IPAD') > -1 || agent.indexOf('IPOD') > -1) && ca.appLinkIos) {
				$(caEl).append('<option value="' + ca.appCode + '">' + ca.appName + '</option>');
			}
		}
	}
	
	// 앱 클릭시
	$(caEl).change(function() {
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

// 서비스 목록 생성
function fnMakeServiceList(servceEl) {
	let param = {
		  url: contextPath + '/mip/spinfo'
		, dataType: 'json'
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (servceEl) {
					for (let i=0; i<resultData.serviceList.length; i++) {
						if (resultData.serviceList[i].presentType != 3) {
							$(servceEl).append('<option value="' + resultData.serviceList[i].svcCode + '">' + resultData.serviceList[i].svcCode + '</option>');
						}
					}
				}
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

let TRX_CODE = '';  // 거래코드

// 거래상태 조회
function fnGetTrxsts() {
	let trxcode = TRX_CODE;
	
	let errMsg = new StringBuffer();

	if ((trxcode || '') == '') {
		errMsg.append('거래코드가 없습니다.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	let param = {
		  url: contextPath + '/mip/trxsts'
		, dataType: 'json'
		, data: JSON.stringify({'data': Base64.encode(JSON.stringify({'trxcode': trxcode}))})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				let trxStsCodeVal = {
					  '0001': '서비스 요청'
					, '0002': 'profile 요청'
					, '0003': '검증 요청'
					, '0004': '검증 완료'
					, '0005': '검증 오류'
				};
				
				$('#trxcodeTag').text(resultData.trxcode);
				$('#trxStsCodeTag').text(trxStsCodeVal[resultData.trxStsCode] + '(' + resultData.trxStsCode + ')');
				$('#vpVerifyResultTag').text(resultData.vpVerifyResult);
				$('#regDtTag').text(resultData.regDt);
				$('#profileSendDtTag').text(resultData.profileSendDt);
				$('#vpReceptDtTag').text(resultData.vpReceptDt);
				$('#imgSendDtTag').text(resultData.imgSendDt);
				$('#udtDtTag').text(resultData.udtDt || resultData.regDt);
				
				let vp = resultData.vp;
				
				if (vp) {
					$('#vpTag').text('보기');
					
					$('#vpArea').val(vp);
					
					$('#vpTag').click(function() {
						if ($(this).text() == '보기') {
							$('#vpArea').show();
						
							$(this).text('닫기');
						} else {
							$('#vpArea').hide();
						
							$(this).text('보기');
						}
					});
				} else {
					$('#vpTag').text('');
					$('#vpArea').val('');
					$('#vpArea').hide();
				}
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

// 초기화
function fnResetTrxsts() {
	TRX_CODE = '';
	
	$('#trxcodeTag').val('');
	$('#trxStsCodeTag').val('');
	$('#vpVerifyResultTag').val('');
	$('#regDtTag').val('');
	$('#profileSendDtTag').val('');
	$('#vpReceptDtTag').val('');
	$('#imgSendDtTag').val('');
	$('#udtDtTag').val('');
}
