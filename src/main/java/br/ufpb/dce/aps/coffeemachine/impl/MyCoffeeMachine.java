package br.ufpb.dce.aps.coffeemachine.impl;

import java.util.ArrayList;

import net.compor.frameworks.jcf.api.ComporFacade;
import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Drink;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine {

	private ComponentsFactory factory;
	private int dolares, centavos;
	private ArrayList<Coin> moedas;
	private int VALORCAFE = 35;
	boolean notAlerta = true;
	int[] trocoPlan = new int[6];


	@Override
	protected void addComponents() {
		this.add(new CafePreto(this.factory));
		this.add(new CafeBranco(this.factory));
		this.add(new CafeBrancoComAcucar(this.factory));
		this.add(new CafePretoComAcucar(this.factory));
		this.add(new CaldoDeSopa(this.factory));
	}

	public void insertCoin(Coin coin) {
		if (coin == null) {
			
			throw new CoffeeMachineException("");
		}
		this.moedas.add(coin);
		this.dolares += coin.getValue() / 100;
		this.centavos += coin.getValue() % 100;
		this.factory.getDisplay().info(
				"Total: US$ " + this.dolares + "." + this.centavos);
	}

	public void cancel() {
		if (this.dolares == 0 && this.centavos == 0) {
			throw new CoffeeMachineException("Não tem moedas inseridas");
		}
		this.factory.getDisplay().warn(Messages.CANCEL);
		this.returnCoin();
	}

	private void returnCoin() {
		for (Coin r : Coin.reverse()) {
			for (Coin aux : this.moedas) {
				if (aux == r) {
					this.factory.getCashBox().release(aux);
				}
			}
		}
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}

	private void zeraVecto() {
		this.moedas.clear();
	}
  
	private int[] planCoins(int troco) throws CoffeeMachineException {
		int[] trocoPlan = new int[6];
		int i = 0;
		for (Coin r : Coin.reverse()) {
			if (r.getValue() <= troco && factory.getCashBox().count(r) > 0) {
				while (r.getValue() <= troco) {
					troco -= r.getValue();
					trocoPlan[i]++;
				}
			}
			i++;
		}
		if (troco != 0) {
			throw new CoffeeMachineException("");
		}
		return trocoPlan;
	}

	private void releaseCoins(int[] quantCoin) {

		for (int i = 0; i < quantCoin.length; i++) {
			int count = quantCoin[i];
			Coin coin = Coin.reverse()[i];

			for (int j = 1; j <= count; j++) {
				this.factory.getCashBox().release(coin);
			}
		}
	}

	private int calculaTroco() {
		int contadorMoedas = 0;
		for (Coin r : Coin.reverse()) {
			for (Coin aux : this.moedas) {
				if (aux == r) {
					contadorMoedas += aux.getValue();
				}
			}
		}
		return contadorMoedas - this.VALORCAFE;
	}

	public void select(Drink drink) {
		if(drink.equals(Drink.BOUILLON)){
			this.VALORCAFE = 25;
		}
		if (calculaTroco() < 0) {
			this.factory.getDisplay().warn(Messages.NO_ENOUGHT_MONEY);
			this.returnCoin();
			return;
		}
		notAlerta = (Boolean) requestService("VerificaDrink", drink);
		if (!notAlerta) {
			returnCoin();
			return;
		}

		int[] troco = null;

		try {
			troco = planCoins(calculaTroco());
		} catch (Exception e) {
			this.factory.getDisplay().warn(Messages.NO_ENOUGHT_CHANGE);
			this.returnCoin();
			return;
		}
		this.factory.getDisplay().info(Messages.MIXING);
		requestService("comparaDrink", drink);
		requestService("LiberandoBebida");
		releaseCoins(troco);

		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}

	public void setFactory(ComponentsFactory factory) {
		this.factory = factory;
		this.factory.getDisplay().info(Messages.INSERT_COINS);
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new ArrayList<Coin>();
		this.addComponents();
	}
	
	@Service
	public void lerCracha(){
		this.factory.getDisplay().info(Messages.BADGE_READ);
	}

	public void readBadge(int badgeCode) {
		this.lerCracha();
	}
}