package mip.mva.sp.config.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.vo
 * @FileName CaVO.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description CA 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
public class CaVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 앱 코드 */
	private String appCode;
	/** 앱 이름 */
	private String appName;
	/** 앱 아이콘 */
	private String appIcon;
	/** 앱 링크 안드로이드 */
	private String appLinkAos;
	/** 앱 링크 아이폰 */
	private String appLinkIos;
	/** CA DID */
	private String caDid;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppLinkAos() {
		return appLinkAos;
	}

	public void setAppLinkAos(String appLinkAos) {
		this.appLinkAos = appLinkAos;
	}

	public String getAppLinkIos() {
		return appLinkIos;
	}

	public void setAppLinkIos(String appLinkIos) {
		this.appLinkIos = appLinkIos;
	}

	public String getCaDid() {
		return caDid;
	}

	public void setCaDid(String caDid) {
		this.caDid = caDid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
