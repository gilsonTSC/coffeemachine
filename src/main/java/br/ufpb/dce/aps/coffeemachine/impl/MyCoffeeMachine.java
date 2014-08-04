package br.ufpb.dce.aps.coffeemachine.impl;

import java.util.ArrayList;

import net.compor.frameworks.jcf.api.ComporFacade;
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
	private Drink drink;
	private final int  VALORCAFE = 35;

	public MyCoffeeMachine(ComponentsFactory factory) {
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new ArrayList<Coin>();
	}

	public void insertCoin(Coin coin) {
		if (coin == null) {
			throw new CoffeeMachineException("Coin null");
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
			for(Coin aux : this.moedas){
				if(aux == r){
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
	
	private void planCoins(int troco) {
		for (Coin r : Coin.reverse()) {
			if (r.getValue() <= troco) {
				this.factory.getCashBox().count(r);
				troco -= r.getValue();
			}
		}
	}
	
	private void releaseCoins(int troco) {		
		for (Coin r : Coin.reverse()) {
			while (r.getValue() <= troco) {
				this.factory.getCashBox().release(r);
				troco -= r.getValue();
			}
		}

	}

	private int calculaTroco(){
		int contadorMoedas = 0;
		
		for(Coin r : Coin.reverse()){
			for(Coin aux : this.moedas){
				if(aux == r){
					contadorMoedas += aux.getValue();
				}
			}
		}
		return contadorMoedas - this.VALORCAFE;
	}


	public void select(Drink drink) {
		
		if(calculaTroco()<0){
			this.factory.getDisplay().warn(Messages.NO_ENOUGHT_MONEY);
			this.returnCoin();		
			return;
		}

		if (!this.factory.getCupDispenser().contains(1)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_CUP);
			this.returnCoin();
			return;
		}
		if (!this.factory.getWaterDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_WATER);
			this.returnCoin();
			return;
		}
		if (!this.factory.getCoffeePowderDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_COFFEE_POWDER);
			this.returnCoin();
			return;
		}
		
		if (drink.equals(Drink.WHITE)) {
			this.factory.getCreamerDispenser().contains(1.2);
		}
		
		if (drink == this.drink.WHITE_SUGAR) {
			factory.getCreamerDispenser().contains(2.0); 
			factory.getSugarDispenser().contains(5.0);

		}
	

		if (drink.equals(Drink.BLACK_SUGAR)) {
			if (!this.factory.getSugarDispenser().contains(1.2)) {
				this.factory.getDisplay().warn(Messages.OUT_OF_SUGAR);
				this.returnCoin();
				return;
			}
		}
		
		planCoins(calculaTroco());
		
		this.factory.getDisplay().info(Messages.MIXING);
		this.factory.getCoffeePowderDispenser().release(1.2);
		this.factory.getWaterDispenser().release(1.2);

		if (drink.equals(Drink.BLACK_SUGAR)) {
			this.factory.getSugarDispenser().release(1.2);
		}
		if (drink.equals(Drink.WHITE)) {
			this.factory.getCreamerDispenser().release(1.2);
		}
		
		if (drink.equals(Drink.WHITE_SUGAR)) {
			this.factory.getCreamerDispenser().release(1.2);
			factory.getSugarDispenser().release(5.0);
		}
		
		this.factory.getDisplay().info(Messages.RELEASING);
		this.factory.getCupDispenser().release(1);
		this.factory.getDrinkDispenser().release(1.2);
		this.factory.getDisplay().info(Messages.TAKE_DRINK);
			
		releaseCoins(calculaTroco());
	
		this.zeraVecto();
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
}