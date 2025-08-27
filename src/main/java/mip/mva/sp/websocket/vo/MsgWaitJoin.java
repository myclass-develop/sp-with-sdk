package mip.mva.sp.websocket.vo;

import mip.mva.sp.config.ConfigBean;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.websocket.vo
 * @FileName MsgWaitJoin.java
 * @Author Min Gi Ju
 * @Date 2022. 5. 31.
 * @Description wait_join 메세지 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
public class MsgWaitJoin {

	private String msg = ConfigBean.WAIT_JOIN;
	private String trxcode;

	/**
	 * 생성자
	 * 
	 * @param trxcode 거래코드
	 */
	public MsgWaitJoin(String trxcode) {
		this.trxcode = trxcode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTrxcode() {
		return trxcode;
	}

	public void setTrxcode(String trxcode) {
		this.trxcode = trxcode;
	}

}
