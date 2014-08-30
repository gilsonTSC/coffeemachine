package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Drink;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine {

	private ComponentsFactory factory;
	boolean notAlerta = true;
	private boolean lerCacha = false, lerCoin = false;
	private int badgeCode;

	@Override
	protected void addComponents() {
		this.add(new CafePreto(this.factory));
		this.add(new CafeBranco(this.factory));
		this.add(new CafeBrancoComAcucar(this.factory));
		this.add(new CafePretoComAcucar(this.factory));
		this.add(new CaldoDeSopa(this.factory));
		this.add(new MyCoin(this.factory));
	}

	public void insertCoin(Coin coin) {
		if (!this.lerCacha) {
			this.lerCoin = true;
			if (coin == null) {
				throw new CoffeeMachineException("");
			}
			requestService("addCoin", coin);
			this.factory.getDisplay().info(
					"Total: US$ " + requestService("dolares", coin) + "."
							+ requestService("centavos", coin));
			return;
		} else {
			this.factory.getDisplay().warn(Messages.CAN_NOT_INSERT_COINS);
			this.returnCoin(coin);
		}
	}

	public void cancel() {
		requestService("cancel");
	}

	private void returnCoin(Coin coin) {
		this.factory.getCashBox().release(coin);
	}

	public void select(Drink drink) {
		if (drink.equals(Drink.BOUILLON)) {
			requestService("setVALORCAFE");
		}
		if (!this.lerCacha) {
			if ((Integer) requestService("calculaTroco") < 0) {
				this.factory.getDisplay().warn(Messages.NO_ENOUGHT_MONEY);
				requestService("returnCoin");
				return;
			}
		}
		notAlerta = (Boolean) requestService("VerificaDrink", drink);
		if (!notAlerta) {
			requestService("returnCoin");
			return;
		}
		int[] troco = null;
		if (this.lerCoin) {
			try {
				troco = (int[]) requestService("planCoins",
						(Integer) requestService("calculaTroco"));
			} catch (Exception e) {
				this.factory.getDisplay().warn(Messages.NO_ENOUGHT_CHANGE);
				requestService("returnCoin");
				return;
			}
		}
		if (this.lerCacha) {
			if(!this.debitar((Integer) this.requestService("getValorCafe"), this.badgeCode)){
				this.factory.getDisplay().warn(Messages.UNKNOWN_BADGE_CODE);
				this.factory.getDisplay().info(Messages.INSERT_COINS);
				return;
			}
		}
		this.factory.getDisplay().info(Messages.MIXING);
		requestService("comparaDrink", drink);
		requestService("LiberandoBebida");
		if (this.lerCoin) {
			requestService("releaseCoins", troco);
		}
		requestService("zeraVecto");
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}

	private boolean debitar(int cents, int badgeCode) {
		if (!this.factory.getPayrollSystem().debit(cents, badgeCode)) {
			//this.factory.getDisplay().warn(Messages.UNKNOWN_BADGE_CODE);
			return false;
		}
		return true;
	}

	public void setFactory(ComponentsFactory factory) {
		this.factory = factory;
		this.factory.getDisplay().info(Messages.INSERT_COINS);
		this.addComponents();
	}

	public void readBadge(int badgeCode) {
		this.badgeCode = badgeCode;
		if (!this.lerCoin) {
			this.factory.getDisplay().info(Messages.BADGE_READ);
			this.lerCacha = true;
		} else {
			this.factory.getDisplay().warn(Messages.CAN_NOT_READ_BADGE);
		}
	}
}