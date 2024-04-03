package ru.terentyev.itrumtesttask.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.hamcrest.Matchers;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.itrumtesttask.ItrumtesttaskApplication;
import ru.terentyev.itrumtesttask.entities.Wallet;
import ru.terentyev.itrumtesttask.exceptions.NotEnoughMoneyException;
import ru.terentyev.itrumtesttask.exceptions.WalletDoesNotExistsException;
import ru.terentyev.itrumtesttask.services.WalletService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = ItrumtesttaskApplication.class)
public class WalletControllerTest {
	
	
	
	private WalletService walletService;
	private Wallet wallet;
	private MockMvc mockMvc;
	private ObjectMapper objectMapper;
	
	@Autowired
	public WalletControllerTest(WalletController walletController, WalletService walletService, MockMvc mockMvc
			, ObjectMapper objectMapper) {
		super();
		this.walletService = walletService;
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}


	@BeforeEach
	public void prepare() {
		wallet = new Wallet((new Random().nextLong(0, Long.MAX_VALUE)));
		walletService.save(wallet);
	}
	
	@AfterEach
	public void clear() {
		walletService.remove(wallet.getUuid());
	}
	
	@Test
	public void givenRandomAmountToDeposit_whenPerform_thanBalanceIsCorrect() {
		long random = new Random().nextLong(0, Long.MAX_VALUE - wallet.getBalance());
		walletService.deposit(wallet.getUuid(), random);
		assertEquals(walletService.getBalance(wallet.getUuid()), wallet.getBalance() + random);
	}
	
	@Test
	public void givenCorrectDepositRequest_whenPerform_thanCorrect() throws JsonProcessingException, Exception {	
		  Map<String, String> request = new HashMap<>();
		  request.put("walletid", wallet.getUuid().toString());
		  request.put("operationType", "DEPOSIT");
		  request.put("amount", String.valueOf(new Random().nextLong(0, Long.MAX_VALUE - wallet.getBalance())));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void givenCorrectWithDrawRequest_whenPerform_thanCorrect() throws JsonProcessingException, Exception {	
		  Map<String, String> request = new HashMap<>();
		  request.put("walletid", wallet.getUuid().toString());
		  request.put("operationType", "WITHDRAW");
		  long randomAmount = new Random().nextLong(0, wallet.getBalance());
		  request.put("amount", String.valueOf(randomAmount));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.получено", Matchers.equalTo(String.valueOf(randomAmount))));
	}
	
	@Test
	public void givenIncorrectWithDrawRequestWithExceedingAmount_whenPerform_thanExceptionIsThrown() throws JsonProcessingException, Exception {	
		  Map<String, String> request = new HashMap<>();
		  request.put("walletid", wallet.getUuid().toString());
		  request.put("operationType", "WITHDRAW");
		  long randomAmount = new Random().nextLong(wallet.getBalance(), Long.MAX_VALUE);
		  request.put("amount", String.valueOf(randomAmount));
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NotEnoughMoneyException));
	}
	
	
	@Test
	public void givenMissingWallet_whenTryToUse_thenExceptionIsThrown() throws JsonProcessingException, Exception {
		  UUID randomUUID = UUID.randomUUID();
		  Optional<Wallet> temporaryWallet = walletService.findById(randomUUID);
		  if (temporaryWallet.isPresent()) walletService.remove(randomUUID);
		  
		  Map<String, String> request = new HashMap<>();
		  request.put("walletid", randomUUID.toString());
		  request.put("operationType", "WITHDRAW");
		  long randomAmount = new Random().nextLong(0, wallet.getBalance());
		  request.put("amount", String.valueOf(randomAmount));
		  
		  
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
					.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
					.characterEncoding("utf-8")
					.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isBadRequest())
					.andExpect(result -> assertTrue(result.getResolvedException() instanceof WalletDoesNotExistsException));
			
			if (temporaryWallet.isPresent()) walletService.save(temporaryWallet.get());
	}
	
	public void takeSemaphore(Semaphore semaphore) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			semaphore.release(); // ?
		} 	
	}
	
	public void runThreads(ThreadPoolExecutor executor, AtomicLong controlBalance, final int TASKS_COUNT, Semaphore semaphore) {
		for (int i = 0; i <= TASKS_COUNT; i++) { 
			while (true) {
				takeSemaphore(semaphore);
				System.out.println(walletService.getBalance(wallet.getUuid()) == controlBalance.get()); // удостовериться, что балансы совпадают
				boolean operationTypeIsDeposit = new Random().nextBoolean();	
				try {				
				if (operationTypeIsDeposit) {					
					performDepositTest(controlBalance);
					semaphore.release();
					break;
				} else {
					performWithdrawTest(controlBalance);
					semaphore.release();
					break;
				}
				} catch (StaleObjectStateException e) {
				}
			}
		}
	}
	
	/**
	 * Тест может не пройти, но баланс остается верным. Тест может не пройти из-за того,
	 *  что некоторые операции не выполняются с первого раза, и тогда их выполнение
	 *  запускается повторно (в цикле while (true) ), пока они не будут выполнены.
	 *  В результате все (вроде как :) ) операции выполняются, а Exceptions остаются.
	 */
	
	@Test
	public void givenConcurrentModyfiyngOfWalletsBalance_mustAchiveCorrectBalance() {
		AtomicLong controlBalance = new AtomicLong(wallet.getBalance());
		final int POOL_SIZE = 50;
		final int TASKS_PER_THREAD = 50;
		final int TASKS_COUNT = POOL_SIZE * TASKS_PER_THREAD;
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);
		Semaphore semaphore = new Semaphore(1);
		executor.submit(() -> runThreads(executor, controlBalance, TASKS_COUNT, semaphore));
		
		shutdownExecutor(executor);		
		assertEquals(walletService.getBalance(wallet.getUuid()), controlBalance.get());
	}
	
	public void performWithdrawTest(AtomicLong controlBalance) {
		long sum = new Random().nextLong(0, controlBalance.get());
		walletService.withdraw(wallet.getUuid(), sum);
		controlBalance.set(controlBalance.get() - sum);
	}
	
	public void performDepositTest(AtomicLong controlBalance) {
		long sum = new Random().nextLong(0, Long.MAX_VALUE - controlBalance.get());
		walletService.deposit(wallet.getUuid(), sum);
		controlBalance.set(controlBalance.get() + sum);
	}
	
	
	public void shutdownExecutor(ThreadPoolExecutor executor) {
		executor.shutdown();
		try {
		    if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
		        executor.shutdownNow();
		    } 
		} catch (InterruptedException e) {
		    executor.shutdownNow();
		}
	}
}
