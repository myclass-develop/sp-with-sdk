/**
 * VP 복호화 & CI 조회
 * vpdata.js
 */

$(function() {
	// VP 복호화 요청 버튼 클릭시
	$('#reqVpDataBtn').click(function() {
		fnReq();
	});
	
	// 입력조건 초기화 버튼 클릭시
	$('#resetBtn').click(function() {
		$('#vp').val('');
		$('#resultTag').val('');
	});

});

// 요청
function fnReq() {
	let vp = $('#vp').val();
	
	let errMsg = new StringBuffer();
	
	if ($('#vp').val().trim() == '') {
		errMsg.append('VP정보를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}

	let param = {
		  url: contextPath + '/mip/vpdata'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': Base64.encode(vp)
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
			
			if (data.result) {
				$('#resultTag').text(resultData.data);
			} else {
				alert(resultData.errorMsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}
