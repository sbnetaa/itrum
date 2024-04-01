package ru.terentyev.itrumtesttask.exceptions;

import java.util.UUID;

import ru.terentyev.itrumtesttask.entities.Wallet;

public class WalletDoesNotExistsException extends RuntimeException {
	
	private UUID uuid;
	private static final String MESSAGE = "Кошелек с таким UUID не существует";
	
	
	public WalletDoesNotExistsException(UUID uuid) {
		super(MESSAGE);
		this.uuid = uuid;
	}
	
	public WalletDoesNotExistsException(Throwable cause) {
		super(MESSAGE, cause);
	}
	
	public WalletDoesNotExistsException(Throwable cause, UUID uuid) {
		super(MESSAGE, cause);
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	
	
}
