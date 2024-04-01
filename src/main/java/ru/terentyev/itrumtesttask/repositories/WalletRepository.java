package ru.terentyev.itrumtesttask.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import ru.terentyev.itrumtesttask.entities.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

	@Query("UPDATE Wallet w SET w.balance = w.balance + ?2 WHERE w.uuid = ?1")
	@Modifying
	void deposit(UUID uuid, long amount);
	
	@Query("UPDATE Wallet w SET w.balance = w.balance - ?2 WHERE w.uuid = ?1")
	@Modifying
	void withdraw(UUID uuid, long amount);
	
	@Query("SELECT balance FROM Wallet w WHERE w.uuid = ?1")
	Long getBalance(UUID uuid);
}
