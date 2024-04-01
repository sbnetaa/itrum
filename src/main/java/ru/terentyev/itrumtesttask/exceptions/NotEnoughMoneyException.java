package ru.terentyev.itrumtesttask.exceptions;

import ru.terentyev.itrumtesttask.entities.Wallet;

public class NotEnoughMoneyException extends RuntimeException {
	
	private Wallet wallet;
	private static final String MESSAGE = "Недостаточно денег для снятия";

	public NotEnoughMoneyException() {
		super(MESSAGE);
	}
	
	public NotEnoughMoneyException(Wallet wallet) {
		super(MESSAGE);
		this.wallet = wallet;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

}
