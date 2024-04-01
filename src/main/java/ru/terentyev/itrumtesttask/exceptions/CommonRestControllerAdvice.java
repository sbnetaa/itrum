package ru.terentyev.itrumtesttask.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private ObjectMapper objectMapper;

	public CommonRestControllerAdvice(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}
	
	public HttpHeaders putHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.valueOf("application/json;charset=UTF-16"));
		return headers;
	}
	
	@ExceptionHandler(value = UnknownOperationException.class)
	public ResponseEntity<String> handleUnknownOperationException(UnknownOperationException uoe) throws JsonProcessingException {
		uoe.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("ошибка", uoe.getMessage());
		return new ResponseEntity<String>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
	
	}
	
	@ExceptionHandler(value = JsonProcessingException.class)
	public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException jpe, HttpServletRequest request) throws JsonProcessingException {
		jpe.printStackTrace();
		Map<String, Object> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("ошибка", "Некорректно составлен JSON");
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(value = NotEnoughMoneyException.class)
	public ResponseEntity<String> handleNotEnoughMoneyException(NotEnoughMoneyException neme) throws JsonProcessingException {
		neme.printStackTrace();
		Map<String, Object> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("ошибка", neme.getMessage());
		responseMap.put("UUID", String.valueOf(neme.getWallet().getUuid()));
		responseMap.put("баланс", String.valueOf(neme.getWallet().getBalance()));
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(value = WalletDoesNotExistsException.class)
	public ResponseEntity<String> handleWalletDoesNotExistsException(WalletDoesNotExistsException wdnee) throws JsonProcessingException {
		wdnee.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("ошибка", wdnee.getMessage());
		responseMap.put("UUID", String.valueOf(wdnee.getUuid()));
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), HttpStatus.BAD_REQUEST);
	}
}
