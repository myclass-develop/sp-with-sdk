package mip.mva.sp.comm.enums;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.enums
 * @FileName CaInfoEnum.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 7.
 * @Description CA 정보 Enum
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
public class CaInfoEnum {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaInfoEnum.class);
	
	private static final Map<String, CaInfoEnum> CAINFO_LIST = new HashMap<String, CaInfoEnum>();

	/** 패키지명 */
	private String packageName;
	/** 앱명 */
	private String caName;

	/**
	 * 생성자
	 * 
	 * @param packageName 패키지명
	 * @param caName     앱명
	 */
	CaInfoEnum(String packageName, String caName) {
		this.packageName = packageName;
		this.caName = caName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCaName() {
		return caName;
	}

	public void setCaName(String caName) {
		this.caName = caName;
	}
	
	/**
	 * Enum 추가
	 * 
	 * @MethodName add
	 * @return CaInfoEnum 목록
	 */
	public static synchronized CaInfoEnum add(String packageName, String caName) {
		CaInfoEnum caInfoEnum = CAINFO_LIST.get(packageName);
		
		if (caInfoEnum == null) {
			caInfoEnum = new CaInfoEnum(packageName, caName);
			
			CAINFO_LIST.put(packageName, caInfoEnum);
		} else {
			LOGGER.error("duplication cainfo!");
		}
		
		return caInfoEnum;
	}
	
	/**
	 * Enum 목록 조회
	 * 
	 * @MethodName values
	 * @return CaInfoEnum 목록
	 */
	public static Collection<CaInfoEnum> values() {
		return CAINFO_LIST.values();
	}

	/**
	 * Enum 조회
	 * 
	 * @MethodName getEnum
	 * @param packageName 패키지명
	 * @return CaInfoEnum
	 */
	public static CaInfoEnum getEnum(String packageName) {
		for (CaInfoEnum item : CaInfoEnum.values()) {
			if (item.getPackageName().equals(packageName)) {
				return item;
			}
		}

		return null;
	}

}
