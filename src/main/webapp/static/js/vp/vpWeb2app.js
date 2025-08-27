$(function() {
	// M200 요청 버튼 클릭시
	$('#m200ReqBtn').click(function() {
		fnM200Req();
	});
	
	// App 호출 버튼 클릭시
	$('#appCallBtn').click(function() {
		fnAppCallBtn();
	});
	
	// 초기화 버튼 클릭시
	$('#resetBtn').click(function() {
		$('#form')[0].reset();
		
		fnResetTrxsts();
	});
	
	// 응답 상태 확인 버튼 클릭시
	$('#trxstsBtn').click(function() {
		fnGetTrxsts();
	});
	
	fnInit();
});

// 초기화 설정
function fnInit() {
	if (agent.indexOf('ANDROID') > -1) {
		$('#appCodeDiv').hide();
	} else if (agent.indexOf('IPHONE') > -1 || agent.indexOf('IPAD') > -1 || agent.indexOf('IPOD') > -1) {
		$('#appCodeDiv').show();
	} else {
		alert('지원하지 않는 기기입니다.');
		
		document.location.href = '/static/html/vp/vp.html';
	}
	
	fnMakeSpInfo('#svcCode', '#appCode', 'App2App');
}

// M200 요청
function fnM200Req() {
	let ifType = 'App2App';
	let mode = 'direct';
	let svcCode = $('#svcCode').val();
	
	let errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	TRX_CODE = '';

	let param = {
		  url: contextPath + '/app2app/start'
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
				TRX_CODE = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				if (!urlScheme) {
					urlScheme = 'mobileid';
				} 
				
				$('#reqDataArea').text(($('#testBed').val() == 'Y' ? 't':'') + urlScheme + '://verify?data_type=byte&mode=direct&data=' + resultData.m200Base64 + '&clientScheme=');
				
				fnGetTrxsts();
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

function fnAppCallBtn() {
	document.location.href = $('#reqDataArea').text();
}
