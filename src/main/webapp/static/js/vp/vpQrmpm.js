$(function() {
	// QR 정보요청 버튼 클릭시
	$('#qrInfoReqBtn').click(function() {
		fnQrInfoReq();
	});
	
	// 초기화
	$('#resetBtn').click(function() {
		$('#form')[0].reset();
		
		fnResetTrxsts();
		
		$('#qrCodeArea').text('QR 코드 영역');
	});
	
	// 거래상태 조회
	$('#trxstsBtn').click(function() {
		fnGetTrxsts();
	});
	
	fnMakeServiceList('#svcCode');
});

// QR 정보요청
function fnQrInfoReq() {
	let ifType = 'QR-MPM';
	let mode = 'direct';
	let svcCode = $('#svcCode').val();
	
	let errMsg = new StringBuffer();

	if (mode.trim() == '') {
		errMsg.append('Mode를 입력해주세요.');
	}
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	TRX_CODE = '';

	let param = {
		  url: contextPath + '/qrmpm/start'
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
				$('#qrCodeArea').empty();
				
				TRX_CODE = JSON.parse(Base64.decode(resultData.m200Base64)).trxcode;
				
				let qrCodeArea = document.getElementById('qrCodeArea');
				let width = qrCodeArea.clientWidth;
				let size = width > 300 ? 300:width;
				
				new QRCode(qrCodeArea, {
					  width: size
					, height: size
					, text: resultData.m200Base64
				});
				
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
