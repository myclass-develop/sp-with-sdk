package mip.mva.sp.qrcpm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.TrxStsCodeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.vo.M120VO;
import mip.mva.sp.comm.vo.T520VO;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.qrcpm.service.QrcpmService;
import mip.mva.sp.websocket.client.cpm.CpmClient;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.qrcpm.service.impl
 * @FileName QrcpmServiceImpl.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description QR-CPM 인터페이스 검증 처리 ServiceImpl
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Service("qrcpmService")
public class QrcpmServiceImpl implements QrcpmService {

	private static final Logger LOGGER = LoggerFactory.getLogger(QrcpmServiceImpl.class);

	/** 설정정보 */
	private final ConfigBean configBean;

	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;

	/**
	 * 생성자
	 * 
	 * @param configBean     설정정보
	 * @param trxInfoService 거래정보 Service
	 */
	public QrcpmServiceImpl(ConfigBean configBean, TrxInfoService trxInfoService) {
		this.configBean = configBean;
		this.trxInfoService = trxInfoService;
	}

	/**
	 * QR-CPM 시작
	 * 
	 * @MethodName start
	 * @param t520 QR-CPM 정보
	 * @return QR-CPM 정보
	 * @throws SpException
	 */
	@Override
	public T520VO start(T520VO t520) throws SpException {
		LOGGER.debug("t520 : {}", ConfigBean.gson.toJson(t520));

		try {
			String m120Str = Base64Util.decode(t520.getM120Base64());

			M120VO m120 = ConfigBean.gson.fromJson(m120Str, M120VO.class);

			String mode = m120.getMode();
			String host = m120.getHost();
			String trxcode = m120.getTrxcode();

			String ifType = t520.getIfType();
			String svcCode = t520.getSvcCode();

			if (ObjectUtils.isEmpty(trxcode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "t520.trxcode");
			}

			if (ObjectUtils.isEmpty(ifType)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, null, "t520.ifType");
			}

			if (ObjectUtils.isEmpty(svcCode)) {
				throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "t520.svcCode");
			}

			String trxStsCode = TrxStsCodeEnum.SERCIVE_REQ.getVal();

			Integer timeout = configBean.getVerifyConfig().getProxy().getProxyConnTimeOut();

			// 거래상태코 등록
			TrxInfoVO trxInfo = new TrxInfoVO();

			trxInfo.setIfType(ifType);
			trxInfo.setTrxcode(trxcode);
			trxInfo.setSvcCode(svcCode);
			trxInfo.setMode(mode);
			trxInfo.setTrxStsCode(trxStsCode);

			LOGGER.debug("trxInfo : {}", ConfigBean.gson.toJson(trxInfo));

			// 거래코드 등록
			trxInfoService.registTrxInfo(trxInfo);

			WsInfoVO wsInfo = new WsInfoVO();

			wsInfo.setConnUrl(host);
			wsInfo.setTrxcode(trxcode);
			wsInfo.setTimeout(timeout);
			wsInfo.setSvcCode(svcCode);

			CpmClient client = new CpmClient(wsInfo);

			client.start();
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}

		return t520;
	}

}
