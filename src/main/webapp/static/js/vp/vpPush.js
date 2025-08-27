$(function() {
	// PUSH 요청 버튼 클릭시
	$('#btnReqPush').click(function() {
		fnPushReq();
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
	
	fnMakeSpInfo('#svcCode', '#appCode', 'PUSH');
});

// PUSH 요청
function fnPushReq() {
	let ifType = 'PUSH';
	let mode = 'direct';
	let appCode = $('#appCode').val();
	let svcCode = $('#svcCode').val();
	let name = $('#name').val();
	let telno = $('#telno').val();
	
	let errMsg = new StringBuffer();
	
	if (appCode.trim() == '') {
		errMsg.append('앱을 선택해주세요.');
	}
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
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
	
	TRX_CODE = '';

	let param = {
		  url: contextPath + '/push/start'
		, dataType: 'json'
		, data: JSON.stringify({
			  'ifType': ifType
			, 'mode': mode
			, 'appCode': appCode
			, 'svcCode': svcCode
			, 'name': name
			, 'telno': telno
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				$('#pushResultTag').val(JSON.stringify(resultData));
				
				TRX_CODE = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
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
