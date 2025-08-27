package mip.mva.sp.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.util.HttpUtil;
import mip.mva.sp.config.vo.CaVO;
import mip.mva.sp.config.vo.ServiceVO;
import mip.mva.sp.config.vo.VerifyConfigVO;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.config
 * @FileName ConfigBean.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description 스프링 부트 커스텀 프로퍼티 설정 Class
 * 
 *              <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Component
@ConfigurationProperties("app")
public class ConfigBean implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBean.class);

	public static final String TYPE = "mip";
	public static final String VERSION = "1.1.0";

	public static final String M120 = "120";
	public static final String M150 = "150";
	public static final String M200 = "200";
	public static final String M310 = "310";
	public static final String M320 = "320";
	public static final String M400 = "400";
	public static final String M900 = "900";

	public static final String START_QRCPM = "start_qrcpm";
	public static final String START_NONCPM = "start_noncpm";
	public static final String WAIT_JOIN = "wait_join";
	public static final String JOIN = "join";
	public static final String WAIT_VERIFY = "wait_verify";
	public static final String VERIFY_HOLDER = "verify_holder";
	public static final String VERIFY_VERIFIER = "verify_verifier";
	public static final String ACK = "ack";
	public static final String WAIT_PROFILE = "wait_profile";
	public static final String PROFILE = "profile";
	public static final String VP = "vp";
	public static final String FINISH = "finish";
	public static final String ERROR = "error";

	public static final String DIRECT = "direct";
	public static final String QR = "QR";
	public static final String PUSH = "PUSH";
	public static final String APP = "APP";

	public static final String PROFILE_TYPE = "VERIFY";

	public static final String OK = "ok";

	public static Gson gson = new Gson();

	private String verifyFilePath;

	private VerifyConfigVO verifyConfig;

	@Override
	public void afterPropertiesSet() throws SpException {
		// 검증정보 JSON 파일 로드 Start
		try {
			LOGGER.debug("verifyFilePath : {}", this.getVerifyFilePath());

			Path verifyFilePath = Paths.get(ResourceUtils.getFile(this.getVerifyFilePath()).getAbsolutePath());

			LOGGER.debug("absolutePath : {}", verifyFilePath);

			byte[] verifyFileByteArr = Files.readAllBytes(verifyFilePath);

			String verifyFileString = new String(verifyFileByteArr, StandardCharsets.UTF_8);

			LOGGER.debug("verifyFileString : {}", verifyFileString);

			verifyConfig = ConfigBean.gson.fromJson(verifyFileString, VerifyConfigVO.class);

			LOGGER.debug("verifyConfig : {}", verifyConfig.toString());

			List<ServiceVO> serviceList = new ArrayList<ServiceVO>();
			ServiceVO service = null;
			LinkedHashMap<String, ServiceVO> services = verifyConfig.getServices();

			for (Map.Entry<String, ServiceVO> entry : services.entrySet()) {
				service = new ServiceVO();

				service.setSvcCode(entry.getValue().getSvcCode());
				service.setServiceName(entry.getValue().getServiceName());
				service.setPresentType(entry.getValue().getPresentType());

				serviceList.add(service);
			}

			verifyConfig.setServiceList(serviceList);
			
			if (verifyConfig.getPush() != null && verifyConfig.getPush().getCaListApiUse()) {
				verifyConfig.setCaList(this.getCaListFromApi());
			}
		} catch (FileNotFoundException e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, e.getMessage());
		} catch (IOException e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, e.getMessage());
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, e.getMessage());
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e.getMessage());
		}
		// 검증정보 JSON 파일 로드 End
	}

	public String getVerifyFilePath() {
		return verifyFilePath;
	}

	public void setVerifyFilePath(String verifyFilePath) {
		this.verifyFilePath = verifyFilePath;
	}

	public VerifyConfigVO getVerifyConfig() {
		return verifyConfig;
	}

	public void setVerifyConfig(VerifyConfigVO verifyConfig) {
		this.verifyConfig = verifyConfig;
	}
	
	public List<CaVO> getCaListFromApi() throws SpException {
		String serverDomain = verifyConfig.getPush().getOpnPushServerList();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("apiType", ConfigBean.TYPE);
		
		LOGGER.debug("paramMap : {}", ConfigBean.gson.toJson(paramMap));
		
		String result = HttpUtil.executeHttpPost(serverDomain, ConfigBean.gson.toJson(paramMap));
		
		LOGGER.debug("result : {}", result);
		
		JsonObject resultObj = JsonParser.parseString(result).getAsJsonObject();
		
		Boolean resultFlag = resultObj.get("result").getAsBoolean();
		
		List<CaVO> caList = null;
		
		if (resultFlag) {
			String caListStr = resultObj.get("caList").getAsJsonArray().toString();
			
			caList = ConfigBean.gson.fromJson(caListStr, new TypeToken<ArrayList<CaVO>>() {}.getType());
		} else {
			String resultMsg = resultObj.get("resultMsg").getAsString();
			
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, resultMsg);
		}
		
		return caList;
	}

}
