/**
 * Util
 * util.js
 */

$(function() {
	// Image to Hex 변환 버튼 클릭시
	$('#imageToHexBtn').click(function() {
		fnImageToHex();
	});

	// Hex to Image 변환 버튼 클릭시
	$('#hexToImageBtn').click(function() {
		fnHexToImage();
	});
	
	// String to Hex 변환 버튼 클릭시
	$('#stringToHexBtn').click(function() {
		fnStringToHex();
	});

	// Hex to String 변환 버튼 클릭시
	$('#hexToStringBtn').click(function() {
		fnHexToString();
	});
	
	// Image to Base64 변환 버튼 클릭시
	$('#imageToBase64Btn').click(function() {
		fnImageToBase64();
	});

	// Base64 to Image 변환 버튼 클릭시
	$('#base64ToImageBtn').click(function() {
		fnBase64ToImage();
	});
	
	// Json to Base64 변환 버튼 클릭시
	$('#jsonToBase64Btn').click(function() {
		fnJsonToBase64();
	});

	// Base64 to Json 변환 버튼 클릭시
	$('#base64ToJsonBtn').click(function() {
		fnBase64ToJson();
	});
	
	// String to RSA 변환 버튼 클릭시
	$('#rsaEncryptBtn').click(function() {
		fnRsaEncrypt();
	});
	
	// RSA to String 변환 버튼 클릭시
	$('#rsaDecryptBtn').click(function() {
		fnRsaDecrypt();
	});
});

// Image to Hex 변환
function fnImageToHex() {
	let errMsg = new StringBuffer();
	
	const files = $('#imageToHex1')[0].files;
	
	if (files.length == 0) {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	const reader = new FileReader();
	
	reader.readAsArrayBuffer(files[0]);
	
	reader.onloadend = (e) => {
		if (e.target.readyState == FileReader.DONE) {
			const b = e.target.result;
			const u = new Uint8Array(b);
			const hs = Array.from(u, function(a) {
				return ('0' + (a & 0xff).toString(16)).slice(-2);
			}).join('');
			
			$('#imageToHex2').text(hs);
		}
	}
}

// Hex to Image 변환
function fnHexToImage() {
	let errMsg = new StringBuffer();
	
	const hex = $('#hexToImage1').val();
	
	if (hex == '') {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	if (hex.replace(/[^A-Fa-f0-9]/g, '').length % 2) {
		alert('자리수가 맞지 않습니다.');
		
		return;
	}
	
	let b = new Array();
	
	for (let i = 0; i < hex.length / 2; i++) {
		let h = hex.substr(i * 2, 2);
		
		b[i] = parseInt(h, 16);
	}
	
	const ba = new Uint8Array(b);
	
	$('#hexToImage2').css('display', 'block');
	$('#hexToImage2').attr('src', URL.createObjectURL(new Blob([ba], {'type': 'application/octet-stream'})));
}

// String to Hex 변환
function fnStringToHex() {
	let errMsg = new StringBuffer();
	
	const str = $('#stringToHex1').val();
	
	if (str == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	const b = new TextEncoder().encode(str);
	const u = new Uint8Array(b);
	const hs = Array.from(u, function(a) {
		return ('0' + (a & 0xff).toString(16)).slice(-2);
	}).join('');
	
	$('#stringToHex2').val(hs);
}

// Hex to String 변환
function fnHexToString() {
	let errMsg = new StringBuffer();
	
	const hex = $('#hexToString1').val();
	
	if (hex == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	if (hex.replace(/[^A-Fa-f0-9]/g, '').length % 2) {
		alert('자리수가 맞지 않습니다.');
		
		return;
	}
	
	let b = new Array();
	
	for (let i = 0; i < hex.length / 2; i++) {
		let h = hex.substr(i * 2, 2);
		
		b[i] = parseInt(h, 16);
	}
	
	const ba = new Uint8Array(b);
	
	let str = new TextDecoder().decode(ba);
	
	$('#hexToString2').val(str);
}

// Image to Base64 변환
function fnImageToBase64() {
	let errMsg = new StringBuffer();
	
	const files = $('#imageToBase641')[0].files;
	
	if (files.length == 0) {
		errMsg.append('파일을 선택해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	let reader = new FileReader();
	
	reader.readAsDataURL(files[0]);
	
	reader.onload = () => {
		const base64 = reader.result;
		
		$('#imageToBase642').text(base64);
	}
}

// Base64 to Image 변환
function fnBase64ToImage() {
	let errMsg = new StringBuffer();
	
	const base64 = $('#base64ToImage1').val();
	
	if (base64 == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	const base64Unsafe = base64.replace(/\-/g, '+').replace(/\_/g, '/');
	
	$('#base64ToImage2').css('display', 'block');
	$('#base64ToImage2').attr('src', 'data:application/octet-stream;base64,' + base64Unsafe);
}

// Json to Base64 변환
function fnJsonToBase64() {
	let errMsg = new StringBuffer();
	
	const json = $('#jsonToBase641').val();
	
	if (json == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	const base64 = Base64.encode(json);
	
	$('#jsonToBase642').val(base64.replace(/\+/g, '-').replace(/\//g, '_'));
}

// Base64 to Json 변환
function fnBase64ToJson() {
	let errMsg = new StringBuffer();
	
	const base64 = $('#base64ToJson1').val();
	
	if ($('#base64ToJson1').val() == '') {
		errMsg.append('데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	let json = Base64.decode(base64).replace(/\\u003d/g, '=');
	
	$('#base64ToJson2').val(json);
}

// String to RSA 변환
function fnRsaEncrypt() {
	let rsaEncrypt1 = $('#rsaEncrypt1').val();
	let decryptTargetDid = $('#decryptTargetDid').val();
	
	let errMsg = new StringBuffer();
	
	if (rsaEncrypt1.trim() == '') {
		errMsg.append('암호화 할 데이터를 입력해주세요.');
	}
	
	if (decryptTargetDid.trim() == '') {
		errMsg.append('복호화 대상 DID를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}

	let param = {
		  url: contextPath + '/web/rsa/encrypt'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': rsaEncrypt1, 'decryptTargetDid': decryptTargetDid
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				$('#rsaEncrypt2').text(data.data);
			} else {
				let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}

// RSA to String 변환
function fnRsaDecrypt() {
	let rsaDecrypt1 = $('#rsaDecrypt1').val();
	
	let errMsg = new StringBuffer();
	
	if (rsaDecrypt1.trim() == '') {
		errMsg.append('복호화 할 데이터를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}

	let param = {
		  url: contextPath + '/web/rsa/decrypt'
		, dataType: 'json'
		, data: JSON.stringify({
			'data': rsaDecrypt1
		})
		, contentType: 'application/json; charset=utf-8'
		, type: 'POST'
		, success: function(data) {
			if (data.result) {
				$('#rsaDecrypt2').text(data.data);
			} else {
				let resultData = data.data ? JSON.parse(Base64.decode(data.data)):null;
				
				alert(resultData.errmsg);
			}
		}
		, error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR, textStatus, errorThrown);
		}
	};
	
	$.ajax(param);
}