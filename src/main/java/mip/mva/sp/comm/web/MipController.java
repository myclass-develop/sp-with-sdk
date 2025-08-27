package mip.mva.sp.comm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonSyntaxException;
import com.raonsecure.omnione.core.data.iw.Unprotected;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.DirectService;
import mip.mva.sp.comm.service.MipDidVpService;
import mip.mva.sp.comm.service.MipZkpVpService;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.vo.M310VO;
import mip.mva.sp.comm.vo.M320VO;
import mip.mva.sp.comm.vo.M400VO;
import mip.mva.sp.comm.vo.M900VO;
import mip.mva.sp.comm.vo.MipApiDataVO;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;
import mip.mva.sp.config.ConfigBean;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.comm.web
 * @FileName MipController.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description MIP 검증 Controller
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@RestController
@RequestMapping("/mip")
public class MipController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MipController.class);

	/** 설정정보 */
	private final ConfigBean configBean;
	/** Direct Service */
	private final DirectService directService;
	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;
	/** VP 검증 Service */
	private final MipDidVpService mipDidVpService;
	/** VP 검증(영지식) Service */
	private final MipZkpVpService mipZkpVpService;

	/**
	 * 생성자
	 * 
	 * @param configBean      설정정보
	 * @param directService   Direct Service
	 * @param trxInfoService  거래정보 Service
	 * @param mipDidVpService VP 검증 Service
	 * @param mipZkpVpService VP 검증(영지식) Service
	 */
	public MipController(ConfigBean configBean, DirectService directService, TrxInfoService trxInfoService,
			MipDidVpService mipDidVpService, MipZkpVpService mipZkpVpService) {
		this.configBean = configBean;
		this.directService = directService;
		this.trxInfoService = trxInfoService;
		this.mipDidVpService = mipDidVpService;
		this.mipZkpVpService = mipZkpVpService;
	}

	/**
	 * Profile 요청
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 M310 메시지"}
	 * @return {"result": true, "data": "Base64로 인코딩된 M310 메시지"}
	 * @throws SpException
	 */
	@PostMapping("/profile")
	public MipApiDataVO getProfile(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("Profile 요청!");

		M310VO m310 = null;

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			m310 = ConfigBean.gson.fromJson(data, M310VO.class);

			m310 = directService.getProfile(m310);

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(m310)));
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "m310");
		} catch (SpException e) {
			String trxcode = ObjectUtils.isEmpty(m310) ? null : m310.getTrxcode();

			e.setTrxcode(trxcode);

			throw e;
		} catch (Exception e) {
			String trxcode = ObjectUtils.isEmpty(m310) ? null : m310.getTrxcode();

			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * BI 이미지 요청
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 M320 메시지"}
	 * @return {"result": true, "data": "Base64로 인코딩된 Image"}
	 * @throws SpException
	 */
	@PostMapping("/image")
	public MipApiDataVO getImage(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("BI Image 요청!");

		M320VO m320 = null;

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			m320 = ConfigBean.gson.fromJson(data, M320VO.class);

			String image = directService.getImage(m320);

			mipApiData.setResult(true);
			mipApiData.setData(image);
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			String trxcode = ObjectUtils.isEmpty(m320) ? null : m320.getTrxcode();

			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "m320");
		} catch (Exception e) {
			String trxcode = ObjectUtils.isEmpty(m320) ? null : m320.getTrxcode();

			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * 검증
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 M400 메시지"}
	 * @return {"result": true}
	 * @throws SpException
	 */
	@PostMapping("/vp")
	public MipApiDataVO verifyVp(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("VP 검증!");

		M400VO m400 = null;

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			m400 = ConfigBean.gson.fromJson(data, M400VO.class);

			Boolean result = directService.verifyVp(m400);

			mipApiData.setResult(result);
			mipApiData.setData(null);
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			String trxcode = ObjectUtils.isEmpty(m400) ? null : m400.getTrxcode();

			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "m400");
		} catch (Exception e) {
			String trxcode = ObjectUtils.isEmpty(m400) ? null : m400.getTrxcode();

			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * 오류 전송
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 오류 메시지"}
	 * @return {"result": true}
	 * @throws SpException
	 */
	@PostMapping("/error")
	public MipApiDataVO error(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("오류 전송!");

		M900VO m900 = null;

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			m900 = ConfigBean.gson.fromJson(data, M900VO.class);

			directService.sendError(m900);

			mipApiData.setResult(true);
			mipApiData.setData(null);
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			String trxcode = ObjectUtils.isEmpty(m900) ? null : m900.getTrxcode();

			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "m900");
		} catch (Exception e) {
			String trxcode = ObjectUtils.isEmpty(m900) ? null : m900.getTrxcode();

			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * 거래상태 조회
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 TrxInfoVO"}
	 * @return {"result": true, "data": "Base64로 인코딩된 TrxInfoVO"}
	 * @throws SpException
	 */
	@PostMapping(value = "/trxsts")
	public MipApiDataVO getTrxsts(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("거래상태 조회!");

		TrxInfoVO trxInfo = null;

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			trxInfo = ConfigBean.gson.fromJson(data, TrxInfoVO.class);

			trxInfo = trxInfoService.getTrxInfo(trxInfo.getTrxcode());

			trxInfo.setVp(null);

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(trxInfo)));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			String trxcode = ObjectUtils.isEmpty(trxInfo) ? null : trxInfo.getTrxcode();

			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, trxcode, "trxInfo");
		} catch (Exception e) {
			String trxcode = ObjectUtils.isEmpty(trxInfo) ? null : trxInfo.getTrxcode();

			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * VP 재검증 - 부인방지
	 * 
	 * @param mipApiData {"data": "Base64로 인코딩된 서비스코드 & VP"}
	 * @return {"result": true}
	 * @throws SpException
	 */
	@PostMapping(value = "/revp")
	public MipApiDataVO reVerifyVP(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("VP 재검증!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			VP vp = ConfigBean.gson.fromJson(data, VP.class);

			Boolean result = directService.reVerifyVP(vp);

			mipApiData.setResult(result);
			mipApiData.setData(null);
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "vp");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * VP data 조회
	 * 
	 * @param mipApiData {"data": "base64로 인코딩된 VP"}
	 * @return {"result": true, "data": "base64로 인코딩된 VP data"}
	 * @throws SpException
	 */
	//@PostMapping(value = "/vpdata")
	public MipApiDataVO getVPData(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("VP data 조회!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			VP vp = ConfigBean.gson.fromJson(data, VP.class);

			String vpData = directService.getVPData(vp);

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(vpData));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "vp");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * Privacy 조회
	 * 
	 * @param mipApiData {"data": "base64로 인코딩된 VP"}
	 * @return {"result": true, "data": "base64로 인코딩된 Privacy data"}
	 * @throws SpException
	 */
	//@PostMapping(value = "/privacy")
	public MipApiDataVO getPrivacy(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("Privacy 조회!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			TrxInfoVO trxInfo = ConfigBean.gson.fromJson(data, TrxInfoVO.class);

			List<Unprotected> privacyList = mipDidVpService.getPrivacy(trxInfo.getTrxcode());

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(privacyList)));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "privacy");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * Privacy 조회 - with VC Type
	 * 
	 * @param mipApiData {"data": "base64로 인코딩된 VP"}
	 * @return {"result": true, "data": "base64로 인코딩된 Privacy data"}
	 * @throws SpException
	 */
	//@PostMapping(value = "/privacyWithVcType")
	public MipApiDataVO getPrivacyWithVcType(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("Privacy 조회 - with VC Type!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			TrxInfoVO trxInfo = ConfigBean.gson.fromJson(data, TrxInfoVO.class);

			List<Map<String, String>> privacyList = mipDidVpService.getPrivacyWithVcType(trxInfo.getTrxcode());

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(privacyList)));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "privacy");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * ZkpSchemaName 조회
	 * 
	 * @param mipApiData {"data": "base64로 인코딩된 VP"}
	 * @return {"result": true, "data": "base64로 인코딩된 ZkpSchemaName 목록"}
	 * @throws SpException
	 */
	@PostMapping(value = "/zkpSchemaName")
	public MipApiDataVO getZkpSchemaName(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("ZkpSchemaName 조회!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			TrxInfoVO trxInfo = ConfigBean.gson.fromJson(data, TrxInfoVO.class);

			List<String> zkpSchemaNameList = mipZkpVpService.getZkpSchemaName(trxInfo.getTrxcode());

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(zkpSchemaNameList)));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "privacy");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * CA명 조회
	 * 
	 * @param mipApiData {"data": "base64로 인코딩된 VP"}
	 * @return {"result": true, "data": "base64로 인코딩된 CA명"}
	 * @throws SpException
	 */
	@PostMapping(value = "/caName")
	public MipApiDataVO getCaName(@RequestBody MipApiDataVO mipApiData) throws SpException {
		LOGGER.debug("CA명 조회!");

		try {
			String data = Base64Util.decode(mipApiData.getData());

			LOGGER.debug("data : {}", data);

			TrxInfoVO trxInfo = ConfigBean.gson.fromJson(data, TrxInfoVO.class);

			String caName = mipDidVpService.getCaName(trxInfo.getTrxcode());

			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(caName));
		} catch (SpException e) {
			throw e;
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, "privacy");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

	/**
	 * SP 정보 조회
	 * 
	 * @return {"result": true, "data": "base64로 인코딩된 SP 정보"}
	 * @throws SpException
	 */
	@PostMapping(value = "/spinfo")
	public MipApiDataVO getSpInfo() throws SpException {
		LOGGER.debug("SP 정보 조회!");

		MipApiDataVO mipApiData = new MipApiDataVO();

		try {
			Map<String, Object> spInfo = new HashMap<String, Object>();

			spInfo.put("serviceList", configBean.getVerifyConfig().getServiceList());
			spInfo.put("caList", configBean.getVerifyConfig().getCaList());
			
			mipApiData.setResult(true);
			mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(spInfo)));
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return mipApiData;
	}

}
