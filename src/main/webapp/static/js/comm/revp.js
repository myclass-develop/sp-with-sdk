/**
 * 재검증 - 부인방지
 * revp.js
 */

$(function() {
	// 재검증 요청 버튼 클릭시
	$('#reqBtn').click(function() {
		fnRevp();
	});
	
	// 초기화 버튼 클릭시
	$('#resetBtn').click(function() {
		$('#form')[0].reset();
		
		fnResetTrxsts();
	});

});

// 재검증
function fnRevp() {
	let vp = $('#vp').val();
	
	let errMsg = new StringBuffer();
	
	if (vp.trim() == '') {
		errMsg.append('VP정보를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}

	let param = {
		  url: contextPath + '/mip/revp'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': Base64.encode(vp)
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				$('#resultTag').text('검증이 성공 했습니다.');
			} else {
				let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				if (resultData.errcode) {
					$('#resultTag').text(resultData.errmsg);
				} else {
					$('#resultTag').text('검증이 실패 했습니다.');
				}
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}
