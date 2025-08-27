package mip.mva.sp.comm.service;

import java.util.List;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.service
 * @FileName MipZkpVpService.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 영지식 VP 검증 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 * </pre>
 */
public interface MipZkpVpService {

	/**
	 * Profile 요청
	 * 
	 * @MethodName getProfile
	 * @param trxInfo 거래정보
	 * @return Base64로 인코딩된 Profile
	 * @throws SpException
	 */
	public String getProfile(TrxInfoVO trxInfo) throws SpException;

	/**
	 * VP 검증
	 * 
	 * @MethodName verifyVp
	 * @param trxInfo 거래정보
	 * @param vp VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	public Boolean verifyVp(TrxInfoVO trxInfo, VP vp) throws SpException;

	/**
	 * VP data 조회
	 * 
	 * @MethodName getVPData
	 * @param vp VP
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	public String getVPData(VP vp) throws SpException;

	/**
	 * ZkpSchemaName 조회
	 * 
	 * @MethodName getZkpSchemaName
	 * @param trxcode String
	 * @return ZkpSchemaName 목록
	 * @throws SpException
	 */
	public List<String> getZkpSchemaName(String trxcode) throws SpException;

}
