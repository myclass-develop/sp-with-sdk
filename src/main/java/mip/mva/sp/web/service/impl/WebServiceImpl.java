package mip.mva.sp.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonSyntaxException;
import com.raonsecure.omnione.core.data.iw.Unprotected;
import com.raonsecure.omnione.core.data.iw.profile.result.VCVerifyProfileResult;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.ModeEnum;
import mip.mva.sp.comm.enums.TrxStsCodeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.DirectService;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.util.Generator;
import mip.mva.sp.comm.util.HttpUtil;
import mip.mva.sp.comm.util.VerifyManager;
import mip.mva.sp.comm.vo.M200VO;
import mip.mva.sp.comm.vo.PushInfoVO;
import mip.mva.sp.comm.vo.T510VO;
import mip.mva.sp.comm.vo.T530VO;
import mip.mva.sp.comm.vo.T540VO;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.web.service.WebService;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.web.service.impl
 * @FileName WebServiceImpl.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 웹 검증 처리 ServiceImpl
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Service("webService")
public class WebServiceImpl implements WebService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceImpl.class);

	/** 설정정보 */
	private final ConfigBean configBean;

	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;

	/** Direct 검증 Service */
	private final DirectService directService;

	/** 검증 Manager */
	private final VerifyManager verifyManager;

	/**
	 * 생성자
	 * 
	 * @param configBean     설정정보
	 * @param trxInfoService 거래정보 Service
	 * @param directService  Direct 검증 Service
	 * @param verifyManager  검증 Manager
	 */
	public WebServiceImpl(ConfigBean configBean, TrxInfoService trxInfoService, DirectService directService,
			VerifyManager verifyManager) {
		this.configBean = configBean;
		this.trxInfoService = trxInfoService;
		this.directService = directService;
		this.verifyManager = verifyManager;
	}

	/**
	 * 푸시 시작
	 * 
	 * @MethodName pushStart
	 * @param t540 푸시 정보
	 * @return 푸시 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	@Override
	public T540VO pushStart(T540VO t540) throws SpException {
		LOGGER.debug("t540 : {}", ConfigBean.gson.toJson(t540));

		try {
			String ifType = t540.getIfType();
			String appCode = t540.getAppCode();
			String svcCode = t540.getSvcCode();
			String mode = t540.getMode();
			String name = t540.getName();
			String telno = t540.getTelno();

			if (ObjectUtils.isEmpty(ifType)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.ifType");
			}

			if (ObjectUtils.isEmpty(appCode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.appCode");
			}

			if (ObjectUtils.isEmpty(svcCode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.svcCode");
			}

			if (ObjectUtils.isEmpty(mode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.mode");
			}

			if (ObjectUtils.isEmpty(name)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.name");
			}

			if (ObjectUtils.isEmpty(telno)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t540.telno");
			}

			String serverDomain = configBean.getVerifyConfig().getPush().getPushServer();
			String msCode = configBean.getVerifyConfig().getPush().getPushMsCode();
			String pushType = configBean.getVerifyConfig().getPush().getPushType();

			// M200 메시지 생성
			M200VO m200 = directService.getM200(ifType, mode, svcCode, false);

			String data = Base64Util.encode(ConfigBean.gson.toJson(m200));

			t540.setM200Base64(data);

			// 푸시 요청 원문 생성
			Map<String, Object> pushMap = new HashMap<String, Object>();

			// 민간개방앱 설정
			if (!ObjectUtils.isEmpty(appCode) && !"100".equals(appCode)) {
				serverDomain = configBean.getVerifyConfig().getPush().getOpnPushServerUse();

				pushMap.put("apiType", ConfigBean.TYPE);
				pushMap.put("appCode", appCode);
				pushMap.put("msCode", msCode);
				
				// encUserInfo 파라미터 추가
				Map<String, String> encUserInfo = new HashMap<String, String>();
				
				encUserInfo.put("userNm", name);
				encUserInfo.put("userPhone", telno);
				
				pushMap.put("encUserInfo", verifyManager.rsaEncrypt(ConfigBean.gson.toJson(encUserInfo), configBean.getVerifyConfig().getCa(appCode).getCaDid()));
				pushMap.put("data", data);
			} else {
				PushInfoVO pushInfo = new PushInfoVO();

				pushInfo.setMscode(msCode);
				pushInfo.setPushType(pushType);
				pushInfo.setName(name);
				pushInfo.setTelno(telno);
				pushInfo.setData(data);
				
				pushMap.put("data", Base64Util.encode(ConfigBean.gson.toJson(pushInfo)));
			}
			
			LOGGER.debug("pushMap : {}", ConfigBean.gson.toJson(pushMap));
			
			String pushResult = HttpUtil.executeHttpPost(serverDomain, ConfigBean.gson.toJson(pushMap));

			LOGGER.debug("pushResult : {}", pushResult);

			PushInfoVO pushInfo = ConfigBean.gson.fromJson(pushResult, PushInfoVO.class);

			if (!pushInfo.getResult()) {
				throw new SpException(MipErrorEnum.UNKNOWN_ERROR, m200.getTrxcode(), pushInfo.getErrmsg());
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return t540;
	}

	/**
	 * QR-MPM 시작
	 * 
	 * @MethodName qrStart
	 * @param t510 QR-MPM 정보
	 * @return QR-MPM 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	@Override
	public T510VO qrStart(T510VO t510) throws SpException {
		LOGGER.debug("t510 : {}", ConfigBean.gson.toJson(t510));

		try {
			String ifType = t510.getIfType();
			String mode = t510.getMode();
			String svcCode = t510.getSvcCode();

			if (ObjectUtils.isEmpty(ifType)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t510.ifType");
			}

			if (ObjectUtils.isEmpty(mode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t510.mode");
			}

			if (ObjectUtils.isEmpty(svcCode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t510.svcCode");
			}

			TrxInfoVO trxInfo = new TrxInfoVO();

			trxInfo.setIfType(ifType);
			trxInfo.setMode(mode);
			trxInfo.setSvcCode(svcCode);

			M200VO m200 = null;

			if (ModeEnum.DIRECT.getVal().equals(mode)) {
				m200 = directStart(trxInfo);
			} else {
				throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, "unsupported mode");
			}

			String m200Str = ConfigBean.gson.toJson(m200);

			LOGGER.debug("m200Str : {}", m200Str);

			String m200Base64 = Base64Util.encode(m200Str);

			t510.setM200Base64(m200Base64);
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return t510;
	}

	/**
	 * QR-MPM 시작(Direct 모드)
	 * 
	 * @MethodName directStart
	 * @param trxInfo
	 * @return
	 * @throws SpException
	 */
	private M200VO directStart(TrxInfoVO trxInfo) throws SpException {
		M200VO m200 = null;

		try {
			String spServerDomain = configBean.getVerifyConfig().getSp().getServerDomain();
			String spBiImageUrl = configBean.getVerifyConfig().getSp().getBiImageUrl();
			Boolean isCi = configBean.getVerifyConfig().getSp().getIsCi();
			Boolean isTelno = configBean.getVerifyConfig().getSp().getIsTelno();

			String trxcode = Generator.genTrxcode();
			String ifType = trxInfo.getIfType();
			String mode = trxInfo.getMode();

			trxInfo.setTrxcode(trxcode);

			trxInfoService.registTrxInfo(trxInfo);

			m200 = new M200VO();

			m200.setTrxcode(trxcode);
			m200.setIfType(ifType);
			m200.setMode(mode);
			m200.setImage(spBiImageUrl);
			m200.setCi(isCi);
			m200.setTelno(isTelno);
			m200.setHost(spServerDomain);
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return m200;
	}

	/**
	 * App to App 시작
	 * 
	 * @MethodName appToAppStart
	 * @param t530 App to App 정보
	 * @return App to App 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	@Override
	public T530VO appToAppStart(T530VO t530) throws SpException {
		try {
			String ifType = t530.getIfType();
			String mode = t530.getMode();
			String svcCode = t530.getSvcCode();

			if (ObjectUtils.isEmpty(ifType)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t530.ifType");
			}

			if (ObjectUtils.isEmpty(mode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t530.mode");
			}

			if (ObjectUtils.isEmpty(svcCode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t530.svcCode");
			}

			M200VO m200 = directService.getM200(ifType, mode, svcCode, true);

			t530.setM200Base64(Base64Util.encode(ConfigBean.gson.toJson(m200)));
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return t530;
	}

	/**
	 * 인증 완료
	 * 
	 * @MethodName crtfcCmpl
	 * @param trxcode 거래코드
	 * @throws SpException
	 */
	@Override
	public void crtfcCmpl(String trxcode) throws SpException {
		try {
			TrxInfoVO trxInfo = trxInfoService.getTrxInfo(trxcode);
			Integer trxTimeout = configBean.getVerifyConfig().getSp().getTrxTimeout();

			if (TrxStsCodeEnum.VERIFY_COM.getVal().equals(trxInfo.getTrxStsCode())) {
				if (trxInfo.getTrxTime() > trxTimeout) {
					throw new SpException(MipErrorEnum.SP_TIMEOUT_ERROR);
				}

				if (ObjectUtils.isEmpty(trxInfo.getVp())) {
					throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, "VP does not exist");
				} else {
					VP vp = null;

					try {
						vp = ConfigBean.gson.fromJson(trxInfo.getVp(), VP.class);
					} catch (JsonSyntaxException e) {
						throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "crtfcCmmplete");
					}

					VCVerifyProfileResult vCVerifyProfileResult = new VCVerifyProfileResult();

					vCVerifyProfileResult.setEncryptType(vp.getEncryptType());
					vCVerifyProfileResult.setKeyType(vp.getKeyType());
					vCVerifyProfileResult.setType(vp.getType());
					vCVerifyProfileResult.setData(vp.getData());
					vCVerifyProfileResult.setAuthType(vp.getAuthType());
					vCVerifyProfileResult.setDid(vp.getDid());
					vCVerifyProfileResult.setNonce(vp.getNonce());

					String vpData = verifyManager.getVPData(vCVerifyProfileResult);
					List<Unprotected> privacyList = verifyManager.getPrivacy(vpData);

					LOGGER.debug("privacyList size : {}", Integer.valueOf(privacyList.size()));
				}

				// 인증 완료 처리 로직 추가 필요
			} else {
				if (TrxStsCodeEnum.VERIFY_ERR.getVal().equals(trxInfo.getTrxStsCode())) {
					throw new SpException(MipErrorEnum.SP_CRTFC_ERROR, trxcode);
				} else {
					throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode);
				}
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}
	}

	/**
	 * RSA 암호화
	 * 
	 * @MethodName rsaEncrypt
	 * @param data      평문 데이터
	 * @param targetDid 복호화 대상 DID
	 * @return 암호화 데이터
	 */
	@Override
	public String rsaEncrypt(String data, String targetDid) throws SpException {
		return verifyManager.rsaEncrypt(data, targetDid);
	}

	/**
	 * RSA 복호화
	 * 
	 * @MethodName rsaDecrypt
	 * @param data 암호화 데이터
	 * @return 복호화 데이터
	 */
	@Override
	public String rsaDecrypt(String data) throws SpException {
		return verifyManager.rsaDecrypt(data);
	}

}
