package ru.terentyev.itrumtesttask.entities;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "wallets")
public class Wallet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID uuid;
	private long balance;
	@Version
	private long version;
	
	public Wallet(){}
	

	public Wallet(long balance) {
		super();
		this.balance = balance;
	}



	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}
	
	

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}


	@Override
	public int hashCode() {
		return Objects.hash(balance, uuid, version);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wallet other = (Wallet) obj;
		return balance == other.balance && Objects.equals(uuid, other.uuid) && version == other.version;
	}


	@Override
	public String toString() {
		return "Wallet [uuid=" + uuid + ", balance=" + balance + ", version=" + version + "]";
	}

	
}
