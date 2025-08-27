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

let trxcode = '';  // 거래코드

let qrCounter = 180;
let qrCounterInterval;
let countStart;

$(function() {
	$('#layout').css('display', 'inline');
	$('#pushCrtfc').css('display', 'none');
	
	$('.ca-list li').click(function() {
		$('#appCode').val(this.id);
	});
	
	// 앱 클릭시
	$('.ca-list li').click(function() {
		$('.ca-list li').removeClass('selected');
		$(this).addClass('selected');
		
		$('#selectedIcon').attr({'src': $('.ca-list li.selected img').attr('src'), 'alt': $('.ca-list li.selected img').attr('alt')});
	});
	
	// PUSH 버튼 클릭시
	$('#push').click(function() {
		$('#caCerver').attr('class', 'cerver-hide');
		
		$('#' + $('#appCode').val()).trigger('click');
		
		$('#qr').removeClass('selected');
		$('#push').addClass('selected');
		
		$('#tabPush').css('display', 'inline');
		$('#tabQr').css('display', 'none');
		
		$('#btnReqPush').css('display', 'inline');
		$('#btnChkCrtfcCmpl').css('display', 'none');
	});
	
	// PUSH 엔터키 입력시
	$('#push').keypress(function(e) {
		if (e.key == 'Enter') {
			$('#caCerver').attr('class', 'cerver-hide');
			
			$('#' + $('#appCode').val()).trigger('click');
			
			$('#qr').removeClass('selected');
			$('#push').addClass('selected');
			
			$('#tabPush').css('display', 'inline');
			$('#tabQr').css('display', 'none');
			
			$('#btnReqPush').css('display', 'inline');
			$('#btnChkCrtfcCmpl').css('display', 'none');
		}
	});
	
	// QR 버튼 클릭시
	$('#qr').click(function() {
		$('#caCerver').attr('class', 'cerver');
		
		$('.ca-list li').removeClass('selected');
		
		$('#push').removeClass('selected');
		$('#qr').addClass('selected');
		
		$('#tabPush').css('display', 'none');
		$('#tabQr').css('display', 'flex');
		
		$('#btnReqPush').css('display', 'none');
		$('#btnChkCrtfcCmpl').css('display', 'inline');
		
		if ($('#allAgree').prop('checked') && $('#qrCounter').text() == '180') {
			fnReqQr();
		}
	});
	
	// QR 엔터키 입력시
	$('#qr').keypress(function(e) {
		if (e.key == 'Enter') {
			$('#caCerver').attr('class', 'cerver');
			
			$('.ca-list li').removeClass('selected');
			
			$('#push').removeClass('selected');
			$('#qr').addClass('selected');
			
			$('#tabPush').css('display', 'none');
			$('#tabQr').css('display', 'flex');
			
			$('#btnReqPush').css('display', 'none');
			$('#btnChkCrtfcCmpl').css('display', 'inline');
			
			if ($('#allAgree').prop('checked') && $('#qrCounter').text() == '180') {
				fnReqQr();
			}
		}
	});
	
	// PUSH 이름, 전화번호 변경시
	$('#name, #telno').change(function() {
		fnReqPushActive();
	});
	
	// 전체동의 클릭시
	$('#allAgree').change(function() {
		let checked = $(this).prop('checked');
		let isQr = $('#qr.selected').length == 1 ? true:false;
		
		if (isQr) {
			if (checked) {
				fnReqQr();
			} else {
				fnCancelQrCounter();
			}
		}
		
		$('.agree-list input').prop('checked', checked);
		
		fnReqPushActive();
	});
	
	// 동의 클릭시
	$('.agree dd > ul > li input').change(function() {
		if ($(this).prop('checked')) {
			let flag = true;
			
			$('.agree dd > ul > li input').each(function() {
				if (!$(this).prop('checked')) {
					flag = false;
				}
			});
			
			if (flag) {
				$('#allAgree').trigger('click');
			} else {
				$('#allAgree').prop('checked', flag);
			}
		} else {
			$('#allAgree').prop('checked', false);
		}
		
		fnReqPushActive();
	});
	
	// 보기 버튼 클릭시
	$('.agree-list .btn').click(function() {
		$('.agree-popup > div').css('display', 'none');
		
		$('#' + $(this).siblings('input').attr('id').replace('Chk', '')).css('display', 'inline');
		
		$('.agree-popup').css('display', 'flex');
	});
	
	// 동의팝업 닫기 버튼 클릭시
	$('.policy .btn-area button').click(function() {
		$('.agree-popup').css('display', 'none');
	});
	
	// push 인증 요청 버튼 클릭시
	$('#btnReqPush').click(function() {
		if ($(this).hasClass('active')) {
			fnReqPush();
		}
	});
	
	// pushCrtfc 인증 완료 버튼 클릭시
	$('#pushCrtfcCmplBtn').click(function() {
		if ($(this).hasClass('active')) {
			fnChkCrtfcCmpl('push');
		}
	});
	
	// pushCrtfc 닫기 버튼 클릭시
	$('#pushCrtfcCloseBtn').click(function() {
		$('#layout').css('display', 'inline');
		$('#pushCrtfc').css('display', 'none');
	});
	
	// 인증팝업 닫기 버튼 클릭시
	$('#btnCloseMidCrtfc').click(function() {
		fnCloseMidCrtfc();
	});
	
	// 인증 완료 버튼 클릭시
	$('#btnChkCrtfcCmpl').click(function() {
		if ($(this).hasClass('active')) {
			fnChkCrtfcCmpl('qr');
		}
	});
	
	fnGetParamater();
	
	fnMakeCaList('#caList');
});

// 파라미터 추출 및 설정
function fnGetParamater() {
	let url = window.location.search.replace('?', '');
	let params = url.split('&');
	
	for (let i in params) {
		if (params[i].indexOf('svcCode') != -1) {
			$('#svcCode').val(params[i].split('=')[1]);
		}
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
				fnMakeRandomCaList(caEl, resultData.caList);
				
				$('.ca-list li').click(function() {
					$('.ca-list li').removeClass('selected');
					$(this).addClass('selected')
					
					$('#selectedIcon').attr({'src': $('.ca-list li.selected img').attr('src'), 'alt': $('.ca-list li.selected img').attr('alt')});
					
					$('#appCode').val(this.id);
				});
				
				$('.ca-list li').keypress(function(e) {
					if (e.key == 'Enter') {
						$('.ca-list li').removeClass('selected');
						$(this).addClass('selected')
						
						$('#selectedIcon').attr({'src': $('.ca-list li.selected img').attr('src'), 'alt': $('.ca-list li.selected img').attr('alt')});
						
						$('#appCode').val(this.id);
					}
				});
				
				$('.ca-list li:first-child').trigger('click');
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
function fnMakeRandomCaList(caEl, caList) {
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
		
		$(caEl).append('<li id="' + ca.appCode + '"><span class="img-box"><img src="' + ca.appIcon + '" alt="' + ca.appName + '"></span><span>' + ca.appName + '</span></li>');
	}
}

// PUSH 요청 활성화
function fnReqPushActive() {
	let flag = false;
	
	let selectMenu = $('.tab-menu li.selected').attr('id');
	
	if (selectMenu == 'push') {
		if ($('#name').val() && $('#telno').val() && $('#allAgree').prop('checked')) {
			flag = true;
		} else {
			flag = false;
		}
	} else {
		if ($('#allAgree').prop('checked')) {
			flag = true;
		} else {
			flag = false;
		}
	}
	
	if (flag) {
		$('#btnReqPush').addClass('active');
		
		$('#btnReqPush').focus();
	} else {
		$('#btnReqPush').removeClass('active');
	}
}

// PUSH 요청
function fnReqPush() {
	let ifType = 'PUSH';
	let mode = 'direct';
	let svcCode = $('#svcCode').val();
	let appCode = $('#appCode').val();
	let name = $('#name').val();
	let telno = $('#telno').val();
	
	let errMsg = new StringBuffer();
	
	if (svcCode == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (appCode == '') {
		errMsg.append('앱을 선택해주세요.');
	}
	
	if (name.trim() == '') {
		errMsg.append('이름을 입력해주세요.');
	}
	
	if (telno.trim() == '') {
		errMsg.append('전화번호를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	const telnoRegex = /^01[016789]-?\d{3,4}-?\d{4}$/;
	
	if (telno != '' && !telnoRegex.test(telno)) {
		alert('전화번호 형식이 맞지 않습니다.');
		
		$('#telno').val('');
		$('#telno').focus();
		
		return false
	}

	let param = {
		  url: contextPath + '/web/push/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'svcCode': svcCode
			, 'appCode': appCode
			, 'name': name
			, 'telno': telno
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				$('#layout').css('display', 'none');
				$('#pushCrtfc').css('display', 'inline');
				
				$('#pushCrtfc .btn-area button:nth-child(1)').focus();
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

// QR 요청
function fnReqQr() {
	qrCounter = 180;
	
	$('#tabQr > div').css('display', 'none');
	$('.qr-wait').css('display', 'block');
	
	let ifType = 'QR-MPM';
	let mode = 'direct';
	let svcCode = $('#svcCode').val();
	
	let errMsg = new StringBuffer();

	if (svcCode == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}

	trxcode = '';
	
	let param = {
		  url: contextPath + '/web/qr/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'svcCode': svcCode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			$('.qr-wait').css('display', 'none');
			
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				$('#qrCodeArea').empty();
				
				trxcode = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				let qrCodeArea = document.getElementById('qrCodeArea');
				let width = 200;
				let heigth = 200;
				
				new QRCode(qrCodeArea, {
					  width: width
					, height: heigth
					, text: resultData.m200Base64
				});
				
				$('.qr-success').css('display', 'flex');
				
				$('#btnChkCrtfcCmpl').addClass('active');
				
				countStart = Date.now();
				
				qrCounterInterval = setInterval(fnQrCounter, 1000);
			} else {
				$('.qr-fail').css('display', 'block');
				
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			$('.qr-wait').css('display', 'none');
			$('.qr-fail').css('display', 'block');
			
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// QR Counter
function fnQrCounter() {
	qrCounter = 180 - Math.round((Date.now() - countStart)/1000);
	
	if (qrCounter <= 0) {
		fnCancelQrCounter();
	} else {
		$('#qrCounter').text(qrCounter);
	}
}

// QR Counter Cancel
function fnCancelQrCounter() {
	qrCounter = 0;
		
	clearInterval(qrCounterInterval);
	
	$('#qrCodeArea').empty();
	$('#qrCodeArea').attr('title', '');
	$('#btnChkCrtfcCmpl').removeClass('active');
	
	$('#qrCodeArea').append('<button id="qrReReqBtn" type="button">재시도</button>').click(function() {
		fnReqQr();
	});
	
	$('#qrCounter').text(qrCounter);
	
	$('.qr-info').css('display', 'block');
	$('.qr-success').css('display', 'none');
	$('.qr-fail').css('display', 'none');
}

// 인증 완료 확인
function fnChkCrtfcCmpl(div) {
	let param = {
		  url: contextPath + '/web/crtfc/cmpl'
		, dataType: 'json'
		, data: JSON.stringify({
			  'trxcode': trxcode
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				if (div == 'push') {
					$('#layout').css('display', 'inline');
					$('#pushCrtfc').css('display', 'none');
				}
				
				alert('인증 완료되었습니다.');
				
				$('#btnCloseMidCrtfc').trigger('click');
			} else {
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
			$('.qr-wait').css('display', 'none');
			$('.qr-fail').css('display', 'block');
			
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// 모바일 신분증 팝업 닫기
function fnCloseMidCrtfc() {
	window.close();
}
