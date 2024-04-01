package ru.terentyev.itrumtesttask.controllers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.itrumtesttask.entities.Wallet;
import ru.terentyev.itrumtesttask.exceptions.NotEnoughMoneyException;
import ru.terentyev.itrumtesttask.exceptions.UnknownOperationException;
import ru.terentyev.itrumtesttask.exceptions.WalletDoesNotExistsException;
import ru.terentyev.itrumtesttask.services.WalletService;

		// TODO MvcConfig

@RestController
@RequestMapping("/api/v1")
public class WalletController {
	
	private WalletService walletService;
	private ObjectMapper objectMapper;

	public WalletController(WalletService walletService, ObjectMapper objectMapper) {
		super();
		this.walletService = walletService;
		this.objectMapper = objectMapper;
	}
	
	
	@PostMapping(value = "/wallet", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {"application/json"}
	, headers = "Accept=application/json")
	public ResponseEntity<String> performOperation(@RequestBody String request) throws JsonMappingException, JsonProcessingException{
		Map<String, String> requestMap = objectMapper.readValue
				(request, new TypeReference<Map<String, String>>(){});
		return defineOperationType(requestMap);
	}
	
	
	public ResponseEntity<String> defineOperationType(Map<String, String> requestMap) throws JsonProcessingException {		
		try {
		if (requestMap.get("operationType").equals("DEPOSIT")) {
			return handleDeposit(requestMap);
		} else if (requestMap.get("operationType").equals("WITHDRAW")) {
			return handleWithdraw(requestMap);
		} else if (requestMap.get("operationType").equals("CREATE")) {
			return handleCreate(requestMap);
		}	else {
			throw new UnknownOperationException("Неизвестная операция: " + requestMap.get("operationType"));
		}
		} catch (WalletDoesNotExistsException wdnee) {
			throw new WalletDoesNotExistsException(wdnee, UUID.fromString(requestMap.get("walletid")));
		}
	}
	
	
	public ResponseEntity<String> handleDeposit(Map<String, String> requestMap) throws JsonProcessingException {
		UUID updatedWalletUuid = UUID.fromString(requestMap.get("walletid"));
		walletService.deposit(updatedWalletUuid, Long.parseLong(requestMap.get("amount")));
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("UUID", requestMap.get("walletid"));
		responseMap.put("баланс", String.valueOf(walletService.getBalance(updatedWalletUuid)));
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), HttpStatus.OK);
	}
	
	
	public ResponseEntity<String> handleWithdraw(Map<String, String> requestMap) throws JsonProcessingException{
		UUID updatedWalletUuid = UUID.fromString(requestMap.get("walletid"));
		Wallet walletToWithdraw = walletService.findById(updatedWalletUuid).orElseThrow(() -> new WalletDoesNotExistsException(updatedWalletUuid));
		long balance = walletToWithdraw.getBalance();
		long amount = Long.parseLong(requestMap.get("amount"));
		if (balance < amount) 
			throw new NotEnoughMoneyException(walletToWithdraw);
		walletService.withdraw(updatedWalletUuid, amount);
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("UUID", String.valueOf(updatedWalletUuid));
		responseMap.put("получено", String.valueOf(amount));
		responseMap.put("баланс", String.valueOf(balance));
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), HttpStatus.OK);
	}
	
	
	public ResponseEntity<String> handleCreate(Map<String, String> requestMap) throws JsonProcessingException, NumberFormatException {
		Map<String, String> responseMap = new LinkedHashMap<>();
		long balance = Long.parseLong(requestMap.getOrDefault("balance", "0"));
		responseMap.put("UUID", String.valueOf(walletService.save(new Wallet(balance))));
		responseMap.put("баланс", String.valueOf(balance));
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), HttpStatus.CREATED);
	}
	
	@GetMapping(value = "/wallets/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {"application/json"}
	, headers = "Accept=application/json")
	public ResponseEntity<String> showBalance(@PathVariable UUID uuid) throws JsonMappingException, JsonProcessingException {
		return new ResponseEntity<>(objectMapper.writeValueAsString(Collections.singletonMap("баланс", walletService.getBalance(uuid))), HttpStatus.OK);
	}
}


