package mip.mva.sp.config.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.vo
 * @FileName VerifyConfigVO.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 검증 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
public class VerifyConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 블록체인 설정 */
	private BlockchainVO blockchain;
	/** DID 파일 설정 */
	private DidWalletFileVO didWalletFile;
	/** SP 설정 */
	private SpVO sp;
	/** 서비스 설정 */
	private LinkedHashMap<String, ServiceVO> services;
	/** 중계서버 설정 */
	private ProxyVO proxy;
	/** 푸시 설정 */
	private PushVO push;
	/** DB 설정 */
	private DbVO db;

	/** 서비스 목록 */
	private List<ServiceVO> serviceList;
	/** CA 목록 */
	private List<CaVO> caList;

	public BlockchainVO getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(BlockchainVO blockchain) {
		this.blockchain = blockchain;
	}

	public DidWalletFileVO getDidWalletFile() {
		return didWalletFile;
	}

	public void setDidWalletFile(DidWalletFileVO didWalletFile) {
		this.didWalletFile = didWalletFile;
	}

	public SpVO getSp() {
		return sp;
	}

	public void setSp(SpVO sp) {
		this.sp = sp;
	}

	public LinkedHashMap<String, ServiceVO> getServices() {
		return services;
	}

	public void setServices(LinkedHashMap<String, ServiceVO> services) {
		this.services = services;
	}

	public ProxyVO getProxy() {
		return proxy;
	}

	public void setProxy(ProxyVO proxy) {
		this.proxy = proxy;
	}

	public PushVO getPush() {
		return push;
	}

	public void setPush(PushVO push) {
		this.push = push;
	}

	public List<ServiceVO> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ServiceVO> serviceList) {
		this.serviceList = serviceList;
	}

	public List<CaVO> getCaList() {
		return caList;
	}

	public void setCaList(List<CaVO> caList) {
		this.caList = caList;
	}

	public DbVO getDb() {
		return db;
	}

	public void setDb(DbVO db) {
		this.db = db;
	}
	
	public CaVO getCa(String appCode) {
		CaVO result = null;
		
		for (CaVO ca : caList) {
			if (appCode.equals(ca.getAppCode())) {
				result = ca;
			}
		}
		
		return result;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
