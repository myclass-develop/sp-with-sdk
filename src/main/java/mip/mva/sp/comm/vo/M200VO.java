package mip.mva.sp.comm.vo;

import java.io.Serializable;

import mip.mva.sp.config.ConfigBean;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.vo
 * @FileName M200VO.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description VP 요청 메시지 VO
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
public class M200VO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 유형 */
	private String type = ConfigBean.TYPE;
	/** 버전 */
	private String version = ConfigBean.VERSION;
	/** Command */
	private String cmd = ConfigBean.M200;
	/** 거래코드 */
	private String trxcode;
	/** 인터페이스구분 */
	private String ifType;
	/** 모드 */
	private String mode;
	/** Base64로 인코딩된 Profile */
	private String profile;
	/** BI 이미지 */
	private String image;
	/** VP에 패키지명 포함 여부 */
	private Boolean packageName;
	/** CI 포함 여부 */
	private Boolean ci;
	/** 전화번호 포함 여부 */
	private Boolean telno;
	/** 호스트명 */
	private String host;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getTrxcode() {
		return trxcode;
	}

	public void setTrxcode(String trxcode) {
		this.trxcode = trxcode;
	}

	public String getIfType() {
		return ifType;
	}

	public void setIfType(String ifType) {
		this.ifType = ifType;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getPackageName() {
		return packageName;
	}

	public void setPackageName(Boolean packageName) {
		this.packageName = packageName;
	}

	public Boolean getCi() {
		return ci;
	}

	public void setCi(Boolean ci) {
		this.ci = ci;
	}

	public Boolean getTelno() {
		return telno;
	}

	public void setTelno(Boolean telno) {
		this.telno = telno;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
