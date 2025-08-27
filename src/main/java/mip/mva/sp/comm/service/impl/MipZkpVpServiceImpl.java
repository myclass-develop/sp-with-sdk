package mip.mva.sp.comm.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonSyntaxException;
import com.raonsecure.omnione.core.data.iw.profile.CommonProfile;
import com.raonsecure.omnione.core.data.iw.profile.result.VCVerifyProfileResult;
import com.raonsecure.omnione.core.data.rest.ResultJson;
import com.raonsecure.omnione.core.data.rest.ResultProfile;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.TrxStsCodeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.MipZkpVpService;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.util.VerifyManager;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;
import mip.mva.sp.config.ConfigBean;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.service.impl
 * @FileName MipZkpVpServiceImpl.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description VP 검증(영지식) ServiceImpl
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Service("mipZkpVpService")
public class MipZkpVpServiceImpl implements MipZkpVpService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MipZkpVpServiceImpl.class);

	/** 검증 Manager */
	private final VerifyManager verifyManager;
	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;

	/**
	 * 생성자
	 * 
	 * @param verifyManager  검증 Manager
	 * @param trxInfoService 서비스 Service
	 * @param trxInfoService 거래정보 Service
	 */
	public MipZkpVpServiceImpl(VerifyManager verifyManager, TrxInfoService trxInfoService) {
		this.verifyManager = verifyManager;
		this.trxInfoService = trxInfoService;
	}

	/**
	 * Profile 요청
	 * 
	 * @MethodName getProfile
	 * @param trxInfo 거래정보
	 * @return Base64로 인코딩된 Profile
	 * @throws SpException
	 */
	@Override
	public String getProfile(TrxInfoVO trxInfo) throws SpException {
		ResultProfile resultProfile = null;

		String trxcode = trxInfo.getTrxcode();

		try {
			resultProfile = verifyManager.profileZkp(trxInfo.getSvcCode());

			CommonProfile commonProfile = ConfigBean.gson.fromJson(Base64Util.decode(resultProfile.getProfileBase64()),
					CommonProfile.class);

			String nonce = commonProfile.getProfile().getProofRequest().getNonce().toString();

			TrxInfoVO trxInfoNew = new TrxInfoVO();

			trxInfoNew.setTrxcode(trxcode);
			trxInfoNew.setTrxStsCode(TrxStsCodeEnum.PROFILE_REQ.getVal());
			trxInfoNew.setNonce(nonce);

			trxInfoService.modifyTrxInfo(trxInfoNew);
		} catch (SpException e) {
			e.setTrxcode(trxcode);

			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return resultProfile.getProfileBase64();
	}

	/**
	 * VP 검증
	 * 
	 * @MethodName verifyVp
	 * @param trxInfo 거래정보
	 * @param vp      VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	@Override
	public Boolean verifyVp(TrxInfoVO trxInfo, VP vp) throws SpException {
		Boolean result = false;

		String trxcode = trxInfo.getTrxcode();
		String profileNonce = trxInfo.getNonce();

		try {
			trxInfo.setTrxStsCode(TrxStsCodeEnum.VERIFY_REQ.getVal());

			trxInfoService.modifyTrxInfo(trxInfo);

			// VP 검증 Start
			VCVerifyProfileResult vCVerifyProfileResult = new VCVerifyProfileResult();

			vCVerifyProfileResult.setEncryptType(vp.getEncryptType());
			vCVerifyProfileResult.setKeyType(vp.getKeyType());
			vCVerifyProfileResult.setType(vp.getType());
			vCVerifyProfileResult.setData(vp.getData());
			vCVerifyProfileResult.setAuthType(vp.getAuthType());
			vCVerifyProfileResult.setDid(vp.getDid());
			vCVerifyProfileResult.setZkpNonce(vp.getZkpNonce());

			ResultJson resultJson = verifyManager.verifyZkp(trxInfo.getSvcCode(), vCVerifyProfileResult);

			if (resultJson == null || !resultJson.isResult()) {
				return result;
			} else {
				result = true;
			}
			// VP 검증 End

			// Nonce 위변조 확인 Start
			String vpNonce = vp.getZkpNonce();

			LOGGER.debug("profileNonce : {}, vpNonce : {}", profileNonce, vpNonce);

			if (ObjectUtils.isEmpty(vpNonce) || vp.getZkpNonce().indexOf(profileNonce) == -1) {
				throw new SpException(MipErrorEnum.SP_MISMATCHING_NONCE, trxcode);
			}
			// Nonce 위변조 확인 End

			TrxInfoVO trxInfoNew = new TrxInfoVO();

			trxInfoNew.setTrxcode(trxcode);
			trxInfoNew.setTrxStsCode(TrxStsCodeEnum.VERIFY_COM.getVal());
			trxInfoNew.setVpVerifyResult(result ? "Y" : "N");
			trxInfoNew.setVp(ConfigBean.gson.toJson(vp));

			trxInfoService.modifyTrxInfo(trxInfoNew);
		} catch (SpException e) {
			e.setTrxcode(trxcode);

			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return result;
	}

	/**
	 * VP data 조회
	 * 
	 * @MethodName getVPData
	 * @param vp VP
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	@Override
	public String getVPData(VP vp) throws SpException {
		VCVerifyProfileResult vCVerifyProfileResult = new VCVerifyProfileResult();

		vCVerifyProfileResult.setEncryptType(vp.getEncryptType());
		vCVerifyProfileResult.setKeyType(vp.getKeyType());
		vCVerifyProfileResult.setType(vp.getType());
		vCVerifyProfileResult.setData(vp.getData());
		vCVerifyProfileResult.setAuthType(vp.getAuthType());
		vCVerifyProfileResult.setDid(vp.getDid());
		vCVerifyProfileResult.setNonce(vp.getNonce());

		String data = verifyManager.getVPData(vCVerifyProfileResult);

		return data;
	}
	
	/**
	 * ZkpSchemaName 조회
	 * 
	 * @MethodName getZkpSchemaName
	 * @param trxcode 거래코드
	 * @return ZkpSchemaName 목록
	 * @throws SpException
	 */
	@Override
	public List<String> getZkpSchemaName(String trxcode) throws SpException {
		TrxInfoVO trxInfo = trxInfoService.getTrxInfo(trxcode);

		VP vp = null;

		try {
			vp = ConfigBean.gson.fromJson(trxInfo.getVp(), VP.class);
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "getPrivacy");
		}

		VCVerifyProfileResult vCVerifyProfileResult = new VCVerifyProfileResult();

		vCVerifyProfileResult.setEncryptType(vp.getEncryptType());
		vCVerifyProfileResult.setKeyType(vp.getKeyType());
		vCVerifyProfileResult.setType(vp.getType());
		vCVerifyProfileResult.setData(vp.getData());
		vCVerifyProfileResult.setAuthType(vp.getAuthType());
		vCVerifyProfileResult.setDid(vp.getDid());
		vCVerifyProfileResult.setNonce(vp.getNonce());
		
		List<String> zkpSchemaNameList = verifyManager.getZkpSchemaName(trxInfo.getSvcCode(), vCVerifyProfileResult);
		
		return zkpSchemaNameList;
	}

}
