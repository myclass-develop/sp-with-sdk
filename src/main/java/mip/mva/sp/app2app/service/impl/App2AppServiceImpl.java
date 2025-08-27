package mip.mva.sp.app2app.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import mip.mva.sp.app2app.service.App2AppService;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.DirectService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.vo.M200VO;
import mip.mva.sp.comm.vo.T530VO;
import mip.mva.sp.config.ConfigBean;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.app2app.service.impl
 * @FileName App2AppService.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 8.
 * @Description App to App 인터페이스 검증 처리 ServiceImpl
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Service("app2AppService")
public class App2AppServiceImpl implements App2AppService {

	private static final Logger LOGGER = LoggerFactory.getLogger(App2AppServiceImpl.class);

	/** Direct 검증 Service */
	private final DirectService directService;

	/**
	 * 생성자
	 * 
	 * @param directService Direct 검증 Service
	 */
	public App2AppServiceImpl(DirectService directService) {
		this.directService = directService;
	}

	/**
	 * App to App 시작
	 * 
	 * @MethodName start
	 * @param t530 App to App 정보
	 * @return App to App 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	@Override
	public T530VO start(T530VO t530) throws SpException {
		LOGGER.debug("t530 : {}", ConfigBean.gson.toJson(t530));

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

}
