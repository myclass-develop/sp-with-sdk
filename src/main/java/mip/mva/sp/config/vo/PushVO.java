package mip.mva.sp.config.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.vo
 * @FileName PushVO.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 푸시 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
public class PushVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 푸시서버주소 */
	private String pushServer;
	/** 민간개방 푸시 CA정보 목록 조회 URL */
	private String opnPushServerList;
	/** 민간개방 푸시 발송 URL */
	private String opnPushServerUse;
	/** CA정보 목록 조회 API 사용 */
	private Boolean caListApiUse;
	/** 푸시 연계시스템 코드(MS-CODE) */
	private String pushMsCode;
	/** 푸시유형 */
	private String pushType;

	public String getPushServer() {
		return pushServer;
	}

	public void setPushServer(String pushServer) {
		this.pushServer = pushServer;
	}

	public String getOpnPushServerList() {
		return opnPushServerList;
	}

	public void setOpnPushServerList(String opnPushServerList) {
		this.opnPushServerList = opnPushServerList;
	}

	public String getOpnPushServerUse() {
		return opnPushServerUse;
	}

	public void setOpnPushServerUse(String opnPushServerUse) {
		this.opnPushServerUse = opnPushServerUse;
	}

	public Boolean getCaListApiUse() {
		return caListApiUse;
	}

	public void setCaListApiUse(Boolean caListApiUse) {
		this.caListApiUse = caListApiUse;
	}

	public String getPushMsCode() {
		return pushMsCode;
	}

	public void setPushMsCode(String pushMsCode) {
		this.pushMsCode = pushMsCode;
	}

	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
