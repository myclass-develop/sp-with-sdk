$(function() {
	// 팝업 호출 클릭시
	$('#openPageBtn').click(function() {
		fnOpenPage();
	});
	
	fnMakeServiceList('#svcCode');
});

function fnOpenPage() {
	let svcCode = $('#svcCode').val();
	let testBed = $('#testBed').val();
	
	let errMsg = new StringBuffer();
	
	if (svcCode.trim() == '') {
		errMsg.append('서비스코드를 입력해주세요.');
	}
	
	if (errMsg.toString() != '') {
		alert(errMsg.toString('\n'));
		
		return;
	}
	
	window.open('../../html/vp/vpMobilePopup.html?svcCode=' + svcCode + '&testBed=' + testBed, '_mobile');
}