package ru.terentyev.itrumtesttask.services;

import java.util.Optional;
import java.util.UUID;

import ru.terentyev.itrumtesttask.entities.Wallet;

public interface WalletService {
	void deposit(UUID uuid, long amount);
	Long withdraw(UUID uuid, long amount);
	Long getBalance(UUID uuid);
	UUID save(Wallet wallet);
	void remove(UUID uuid);
	boolean checkIfWalletExists(UUID uuid);
	Optional<Wallet> findById(UUID uuid);
}
