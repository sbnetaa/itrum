package ru.terentyev.itrumtesttask.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.terentyev.itrumtesttask.entities.Wallet;
import ru.terentyev.itrumtesttask.exceptions.WalletDoesNotExistsException;
import ru.terentyev.itrumtesttask.repositories.WalletRepository;

@Service
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

	private WalletRepository walletRepository;

	public WalletServiceImpl(WalletRepository walletRepository) {
		super();
		this.walletRepository = walletRepository;
	}

	
	@Transactional(readOnly = false)
	public void deposit(UUID uuid, long amount) {
		if (!checkIfWalletExists(uuid)) throw new WalletDoesNotExistsException(uuid);
		walletRepository.deposit(uuid, amount);
	}
	
	@Transactional(readOnly = false)
	public Long withdraw(UUID uuid, long amount) {
		if (!checkIfWalletExists(uuid)) throw new WalletDoesNotExistsException(uuid);
		walletRepository.withdraw(uuid, amount);
		return amount;
	}
	
	public Long getBalance(UUID uuid) {
		if (!checkIfWalletExists(uuid)) throw new WalletDoesNotExistsException(uuid);
		return walletRepository.getBalance(uuid);
	}
	
	public boolean checkIfWalletExists(UUID uuid) {
		return walletRepository.existsById(uuid);
	}
	
	@Transactional(readOnly = false)
	public UUID save(Wallet wallet) {
		return walletRepository.save(wallet).getUuid();
	}
	
	@Transactional(readOnly = false)
	public void remove(UUID uuid) {
		walletRepository.deleteById(uuid);
	}
	
	public Optional<Wallet> findById(UUID uuid) {
		return walletRepository.findById(uuid);
	}
}
